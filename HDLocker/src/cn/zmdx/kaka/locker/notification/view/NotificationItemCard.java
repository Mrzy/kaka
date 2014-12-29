package cn.zmdx.kaka.locker.notification.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import it.gmariotti.cardslib.library.internal.Card;

public class NotificationItemCard extends Card {

    public NotificationItemCard(Context context, int innerLayout) {
        super(context, innerLayout);
        setSwipeable(true);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        // TODO Auto-generated method stub
        super.setupInnerViewElements(parent, view);
    }
}
