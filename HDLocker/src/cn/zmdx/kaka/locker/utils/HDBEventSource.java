/**
 * Copyright (c) Tapas Mobile.  All Rights Reserved.
 * 
 * Event source: monitor system level events & forward it to application
 * 
 * Following events are expected to support:
 * 
 * 1. Network connection state
 * 2. Time tick
 * 3. battery level
 * 4. user present or not
 * 5. power
 * 6. external storage state
 * 7. headset plug
 * 8. package install / remove / replace
 * 9. Airplane mode
 * 10. Screen on / off
 * 11. shutdown
 */

package cn.zmdx.kaka.locker.utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

public class HDBEventSource {
    private static final boolean LOGV = HDBConfig.SHOULD_LOG && false;

    // -----------------------------------------------------------------------------------

    public static class GeneralReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dispatchIntent(intent);
        }
    }

    public static interface IntentListener {
        // onIntentArrival would always invoked from non-UI thread
        public void onIntentArrival(final Intent intent);
    }

    static class EventListenerData {
        public WeakReference<IntentListener> listener;

        public HashSet<String> actions = new HashSet<String>();
    }

    // -----------------------------------------------------------------------------------

    static WeakHashMap<IntentListener, EventListenerData> sListeners = new WeakHashMap<IntentListener, EventListenerData>();

    @SuppressWarnings("deprecation")
    private static final String[] sGeneralActions = new String[] {
        ConnectivityManager.CONNECTIVITY_ACTION,
        WifiManager.WIFI_STATE_CHANGED_ACTION,
        Intent.ACTION_TIME_TICK,
        Intent.ACTION_TIME_CHANGED,
        Intent.ACTION_LOCALE_CHANGED,
        Intent.ACTION_TIMEZONE_CHANGED,
        Intent.ACTION_BATTERY_CHANGED,
        Intent.ACTION_BATTERY_LOW,
        Intent.ACTION_BATTERY_OKAY,
        Intent.ACTION_USER_PRESENT,
        Intent.ACTION_POWER_CONNECTED,
        Intent.ACTION_POWER_DISCONNECTED,
        Intent.ACTION_HEADSET_PLUG,
        Intent.ACTION_AIRPLANE_MODE_CHANGED,
        Intent.ACTION_SCREEN_ON,
        Intent.ACTION_SCREEN_OFF,
        Intent.ACTION_SHUTDOWN,
        Intent.ACTION_WALLPAPER_CHANGED
    };

    private static final String[] sPackageActions = new String[] {
        Intent.ACTION_PACKAGE_ADDED,
        Intent.ACTION_PACKAGE_CHANGED,
        Intent.ACTION_PACKAGE_DATA_CLEARED,
        Intent.ACTION_PACKAGE_REMOVED,
        Intent.ACTION_PACKAGE_REPLACED,
        Intent.ACTION_PACKAGE_RESTARTED
    };

    private static final String[] sMediaActions = new String[] {
        Intent.ACTION_MEDIA_MOUNTED,
        Intent.ACTION_MEDIA_EJECT,
        Intent.ACTION_MEDIA_UNMOUNTED,
        Intent.ACTION_MEDIA_BAD_REMOVAL
    };

    private static final String[] SYNCHRONIZED_INTENTS = new String[] {
        Intent.ACTION_SHUTDOWN,
        Intent.ACTION_MEDIA_EJECT,
    };

    private static GeneralReceiver sReceiverGeneral = new GeneralReceiver();
    private static GeneralReceiver sReceiverPackage = new GeneralReceiver();
    private static GeneralReceiver sReceiverStorage = new GeneralReceiver();
    private static HashMap<String, GeneralReceiver> sDynamicReceivers = new HashMap<String, HDBEventSource.GeneralReceiver>();

    private static Context sAppContext;

    // Intent action string --> all listeners WeakHashMap (in fact WeakHashSet is enough)
    private static final HashMap<String, WeakHashMap<IntentListener, EventListenerData>> mIntentIndex = new HashMap<String, WeakHashMap<IntentListener,EventListenerData>>();
    private static final HashSet<String> mSyncIntent = new HashSet<String>();

    // -----------------------------------------------------------------------------------

    public static void startup(Context ctx, String[] actions) {
        final Context appContext = ctx.getApplicationContext();
        sAppContext = appContext;

        IntentFilter filter = null;
//        // build sync intent index: those intent must be process synchonized
//        for (String s : SYNCHRONIZED_INTENTS) {
//            mSyncIntent.add(s);
//        }
        HashSet<String> filteredActions = new HashSet<String>();
        if (actions != null) {
            for (String s : actions) {
                filteredActions.add(s);
            }
        }
//        // register receiver for general intents
        filter = new IntentFilter();
        for (String s : sGeneralActions) {
            if (!filteredActions.contains(s)) {
                filter.addAction(s);
            }
        }
        appContext.registerReceiver(sReceiverGeneral, filter);
//
//        // register receiver for package
//        filter = new IntentFilter();
//        for (String s : sPackageActions) {
//            if (!filteredActions.contains(s)) {
//                filter.addAction(s);
//            }
//        }
//        filter.addDataScheme("package");
//        appContext.registerReceiver(sReceiverPackage, filter);
//
//        // register receiver for media
//        filter = new IntentFilter();
//        for (String s : sMediaActions) {
//            if (!filteredActions.contains(s)) {
//                filter.addAction(s);
//            }
//        }
//        filter.addDataScheme("file");
//        appContext.registerReceiver(sReceiverStorage, filter);

        HDBNetworkState.init(sAppContext);
    }

    public static void shutdown() {
//        sAppContext.unregisterReceiver(sReceiverGeneral);
//        sAppContext.unregisterReceiver(sReceiverPackage);
//        sAppContext.unregisterReceiver(sReceiverStorage);
//        for (GeneralReceiver gr : sDynamicReceivers.values()) {
//            if (gr != null) {
//                sAppContext.unregisterReceiver(gr);
//            }
//        }
//        sDynamicReceivers.clear();
    }

    private static boolean searchForActions(String action, String[] data) {
        if (action == null || data == null) {
            return false;
        }
        for (String s : data) {
            if (action.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean registerEventListener(IntentListener l, String action) {
        return registerEventListener(l, action, null);
    }

    public static boolean registerEventListener(IntentListener l, String action, String category) {
        return registerEventListener(l, action, category, null);
    }

    public static boolean registerEventListener(IntentListener l, String action, String category, String permission) {
        if (l == null || action == null || action.length() == 0) {
            HDBLOG.logE("bad parameter found");
            return false;
        }

        boolean found = false;

        // if category or permission is specified, we register it dynamically
        if (category != null || permission != null) {
            found = false;
        } else {
            found = found ? found : searchForActions(action, sGeneralActions);
            found = found ? found : searchForActions(action, sPackageActions);
            found = found ? found : searchForActions(action, sMediaActions);
        }

        if (!found) {
            final String key = "[" + action + "]_[" + category + "]_[" + permission + "]";

            if (!sDynamicReceivers.containsKey(key)) {
                GeneralReceiver gr = new GeneralReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(action);
                if (category != null) {
                    filter.addCategory(category);
                }

                if (permission == null) {
                    sAppContext.registerReceiver(gr, filter);
                } else {
                    sAppContext.registerReceiver(gr, filter, permission, null);
                }

                sDynamicReceivers.put(key, gr);

                HDBLOG.logI("register dynamic receiver[" + sDynamicReceivers.size() + "]: " + key);
            }
        }

        synchronized (sListeners) {
            EventListenerData data = sListeners.get(l);
            if (data == null) {
                data = new EventListenerData();
                sListeners.put(l, data);
            }
            data.listener = new WeakReference<HDBEventSource.IntentListener>(l);
            if (!data.actions.contains(action)) {
                data.actions.add(action);
            }

            // now build index
            WeakHashMap<IntentListener, EventListenerData> target = mIntentIndex.get(action);
            if (target == null) {
                target = new WeakHashMap<HDBEventSource.IntentListener, EventListenerData>();
                mIntentIndex.put(action, target);
                if (LOGV) HDBLOG.logI("register target: " + action + " , " + target);
            }
            if (!target.containsKey(l)) {
                target.put(l, data);
            }
            if (LOGV) HDBLOG.logI("register listener: " + l + " to target data: " + data);

            return true;
        }
    }

    /**
     * unregister all interested intent associated with l
     * 
     * @param l
     */
    public static void unregisterEventListener(IntentListener l) {
        HDBLOG.logI(l.toString());

        synchronized (sListeners) {
            EventListenerData data = sListeners.get(l);

            if (data != null) {
                sListeners.remove(l);
                if (LOGV) HDBLOG.logI("remove " + l + " from " + sListeners);

                // remove from index
                for (String action : data.actions) {
                    WeakHashMap<IntentListener, EventListenerData> target = mIntentIndex.get(action);
                    if (target != null) {
                        target.remove(l);
                        if (LOGV) HDBLOG.logI("revmoe " + l + " from index: " + target);
                    }
                }
            }
        }
    }

    public static boolean registerAllPackageListener(IntentListener l) {
        for (String action : sPackageActions) {
            registerEventListener(l, action);
        }
        return true;
    }

    public static void unregisterAllPackageListener(IntentListener l) {
        unregisterEventListener(l);
    }

    public static void dispatchIntent(final Intent intent) {
        final String action = intent.getAction();

        if (LOGV) {
            // too many logs
            HDBLOG.logI("action=" + action + ", " + intent);
        }

        /**
         * 1. Network connection state
         * 2. Time tick
         * 3. battery level
         * 4. user present or not
         * 5. power
         * 6. external storage state
         * 7. headset plug
         * 8. package install / remove / replace / uid
         * 9. Airplane mode
         * 10. screen on / off
         * 11. shutdown
         */

        boolean sync = mSyncIntent.contains(action);

        synchronized (sListeners) {
            WeakHashMap<IntentListener, EventListenerData> target = mIntentIndex.get(action);
            if (target != null) {
                for (final IntentListener l : target.keySet()) {
                    // too many logs
                    if (LOGV) HDBLOG.logI("dispatch " + action + " to: " + l);

                    if (sync) {
                        l.onIntentArrival(intent);
                    } else {
                        HDBThreadUtils.runOnWorker(new Runnable() {
                            @Override
                            public void run() {
                                l.onIntentArrival(intent);
                            }
                        });
                    }
                }
            }
        }
    }
}
