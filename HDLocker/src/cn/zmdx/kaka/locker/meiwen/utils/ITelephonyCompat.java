/*
 * Copyright (C) 2012 Tapas Mobile Ltd.  All Rights Reserved.
 */

package cn.zmdx.kaka.locker.meiwen.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ITelephonyCompat {
    private static final boolean DEBUG = false;
    private static final String TAG = "ITelephonyCompat";

    private static final int APN_TYPE_NOT_AVAILABLE = 2;
    private static final int APN_REQUEST_FAILED     = 3;

    private static final String APN_DEFAULT = "default";
    private static final String CLASSNAME_ITELEPONY = "com.android.internal.telephony.ITelephony";

    private static Method sEnableApnTypeMethod = null;
    private static Method sDisableApnTypeMethod = null;
    private static Method sEnableDataConnectivityMethod = null;
    private static Method sDisableDataConnectivityMethod = null;
    private static Method sEndCallMethod = null;
    private static Method sGetCallStateMethod = null;
    private static Method sGetITelephonyMethod = null;
    private static Method sIsRingingMethod = null;

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(CLASSNAME_ITELEPONY, false, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            if (DEBUG) e.printStackTrace();
        }
        if (clazz != null) {
            try {
                Class<?>[] args1 = new Class[] { String.class };
                Class<?>[] args2 = new Class[0];
                sEnableApnTypeMethod = clazz.getDeclaredMethod("enableApnType", args1);
                sDisableApnTypeMethod = clazz.getDeclaredMethod("disableApnType", args1);
                sEnableDataConnectivityMethod = clazz.getDeclaredMethod("enableDataConnectivity",
                        args2);
                sDisableDataConnectivityMethod = clazz.getDeclaredMethod("disableDataConnectivity",
                        args2);
                sEndCallMethod = clazz.getDeclaredMethod("endCall", args2);
                sGetCallStateMethod = clazz.getMethod("getCallState", args2);
                sIsRingingMethod = clazz.getMethod("isRinging", args2);
            } catch (Exception e) {
            }
        }
        try {
            sGetITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony",
                    new Class[0]);
            sGetITelephonyMethod.setAccessible(true);
        } catch (Exception e) {
        }
    }

    private static Object getITelephony(Context context) {
        if (sGetITelephonyMethod != null) {
            try {
                TelephonyManager obj = getRingingTelephonyMgr(context);
                Method localMethod = sGetITelephonyMethod;
                Object[] arrayOfObject = new Object[0];
                return localMethod.invoke(obj, arrayOfObject);
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "getITelephony failure");
        return null;
    }

    private static Object getITelephony(Context context,String service) {
        if (sGetITelephonyMethod != null) {
            try {
                TelephonyManager obj = (TelephonyManager) context.getSystemService(service);
                Method localMethod = sGetITelephonyMethod;
                Object[] arrayOfObject = new Object[0];
                return localMethod.invoke(obj, arrayOfObject);
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "getITelephony failure");
        return null;
    }

    public static boolean setApnEnabled(Context context) {
        if (sEnableDataConnectivityMethod != null && sGetITelephonyMethod != null) {
            try {
                Object obj = getITelephony(context);
                Method localMethod = sEnableApnTypeMethod;
                Object[] arrayOfObject = new Object[] { APN_DEFAULT };
                Object ret = localMethod.invoke(obj, arrayOfObject);
                int retv = (Integer) ret;
                if (retv == APN_TYPE_NOT_AVAILABLE || retv == APN_REQUEST_FAILED) {
                    return false;
                }
                localMethod = sEnableDataConnectivityMethod;
                ret = localMethod.invoke(obj, new Object[0]);
                return (Boolean) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        return false;
    }

    public static boolean setApnDisabled(Context context) {
        if (sDisableDataConnectivityMethod != null && sGetITelephonyMethod != null) {
            try {
                Object obj = getITelephony(context);
                Method localMethod = sDisableApnTypeMethod;
                Object[] arrayOfObject = new Object[] { APN_DEFAULT };
                Object ret = localMethod.invoke(obj, arrayOfObject);
                int retv = (Integer) ret;
                if (retv == APN_TYPE_NOT_AVAILABLE || retv == APN_REQUEST_FAILED) {
                    return false;
                }
                localMethod = sDisableDataConnectivityMethod;
                ret = localMethod.invoke(obj, new Object[0]);
                return (Boolean) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        return false;
    }

    public static boolean endCall(Context context) {
        if (sEndCallMethod != null && sGetITelephonyMethod != null) {
            try {
                Object obj = getITelephony(context);
                Method localMethod = sEndCallMethod;
                Object ret = localMethod.invoke(obj, new Object[0]);
                return (Boolean) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        return false;
    }

    public static TelephonyManager getRingingTelephonyMgr(Context context)
    {
        TelephonyManager tm = null;
        String device = android.os.Build.MODEL;
        if (device.equals("SCH-N719")) {
            TelephonyManager tm1 = (TelephonyManager) context.getSystemService("phone");
            TelephonyManager tm2 = (TelephonyManager) context.getSystemService("phone2");
            tm = tm1;
            if (DEBUG) {
                Log.d(TAG, "Card 1 Call State is: " + tm1.getCallState() + " Card 2 Call State is: "
                        + tm2.getCallState());
            }
            if (tm2 == null) {
                Log.d(TAG, " Card 2 TelephonyManager initialization error occurs");
                return tm;
            }
            if (TelephonyManager.CALL_STATE_RINGING != tm.getCallState()
                    && TelephonyManager.CALL_STATE_RINGING == tm2.getCallState())
            {
                Log.d(TAG, "Incomming call is comming for Sim Card 2");
                tm = tm2;
            }
        } else if (device.equals("SM-N9002")) {
            tm = (TelephonyManager) context.getSystemService("phone");
            TelephonyManager tm2 = (TelephonyManager) context.getSystemService("phone2");
            if (tm == null || tm.getSimState() != TelephonyManager.SIM_STATE_READY)
            {
                tm = tm2;
                return tm;
            }
        } else if (device.equals("GT-N7102")) {
            if(sGetCallStateMethod != null) {
                TelephonyManager tm1 = (TelephonyManager) context.getSystemService("phone");
                TelephonyManager tm2 = (TelephonyManager) context.getSystemService("phone2");
                try {
                    Object obj1 = getITelephony(context, "phone");//ITelephony
                    Object obj2 = getITelephony(context, "phone2");
                    int sta1 = (Integer) sGetCallStateMethod.invoke(obj1);
                    int sta2 = (Integer) sGetCallStateMethod.invoke(obj2);
                    if (DEBUG) {
                        Log.d(TAG, "Card 1 Call State is: " + sta1 + " Card 2 Call State is: "
                                + sta2);
                    }
                    if (tm1.getSimState() == TelephonyManager.SIM_STATE_READY
                            && tm2.getSimState() == TelephonyManager.SIM_STATE_READY) {
                        // 在双卡下面
                        if (sta1 == TelephonyManager.CALL_STATE_RINGING) {
                            tm = tm1;
                        } else if (sta2 == TelephonyManager.CALL_STATE_RINGING) {
                            tm = tm2;
                        } else {
                            tm = tm1;
                        }
                    } else if (tm1.getSimState() != TelephonyManager.SIM_STATE_READY
                            && tm2.getSimState() == TelephonyManager.SIM_STATE_READY) {
                        // 在单卡下面
                        tm = tm2;
                    } else if (tm1.getSimState() == TelephonyManager.SIM_STATE_READY
                            && tm2.getSimState() != TelephonyManager.SIM_STATE_READY) {
                        tm = tm1;
                    } else {
                        tm = tm1;
                    }
                } catch (Exception e) {
                    if (DEBUG) {
                        Log.e(TAG, "getCallState method changed...system might be updated");
                    }
                    e.printStackTrace();
                }
            }
        } else if (device.equals("SM-N9009")) {
            TelephonyManager tm1 = (TelephonyManager) context.getSystemService("phone");
            TelephonyManager tm2 = (TelephonyManager) context.getSystemService("phone2");
            tm = tm1;

            if (tm.getSimState() != TelephonyManager.SIM_STATE_READY)
            {
                tm = tm2;
            } else {
                if (sIsRingingMethod != null) {
                    try {
                        Object obj1 = getITelephony(context, "phone");// ITelephony
                        Object obj2 = getITelephony(context, "phone2");
                        Boolean isSim1Ringing = (Boolean) sIsRingingMethod.invoke(obj1);
                        Boolean isSim2Ringing = (Boolean) sIsRingingMethod.invoke(obj2);

                        if (DEBUG) {
                            Log.d(TAG, "Card1 ringing: " + isSim1Ringing + " , Card2 ringing: "
                                    + isSim2Ringing);
                        }

                        if (isSim2Ringing)
                            tm = tm2;

                    } catch (Exception e)
                    {
                        if (DEBUG) {
                            Log.e(TAG, "isRinging method changed...system might be updated");
                        }
                        e.printStackTrace();
                    }
                }
            }
        } else {
            tm = (TelephonyManager) context.getSystemService("phone");
        }
        if (tm == null) {
            tm = (TelephonyManager) context.getSystemService("phone");
        }
        return tm;
    }
}
