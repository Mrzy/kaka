
package cn.zmdx.kaka.locker.meiwen.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class FoldableBoxAdapter extends CardArrayAdapter {

    private List<Card> mCards;

    public FoldableBoxAdapter(Context context, List<Card> cards) {
        super(context, cards);
        mCards = cards;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public List<Card> getCardsData() {
        return mCards;
    }
}
