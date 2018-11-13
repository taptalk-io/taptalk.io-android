package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.TAPBaseViewHolder;
import com.moselo.HomingPigeon.Interface.TAPCustomKeyboardInterface;
import com.moselo.HomingPigeon.Model.TAPCustomKeyboardModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpCustomKeyboardAdapter extends HpBaseAdapter<TAPCustomKeyboardModel, TAPBaseViewHolder<TAPCustomKeyboardModel>> {

    private TAPCustomKeyboardInterface listener;

    public HpCustomKeyboardAdapter(List<TAPCustomKeyboardModel> keyboardMenuList, TAPCustomKeyboardInterface listener) {
        setItems(keyboardMenuList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPCustomKeyboardModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeyboardMenuHolder(parent, R.layout.hp_cell_custom_keyboard_menu);
    }

    class KeyboardMenuHolder extends TAPBaseViewHolder<TAPCustomKeyboardModel> {

        TextView tvMenuIcon, tvMenuLabel;

        protected KeyboardMenuHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvMenuIcon = itemView.findViewById(R.id.tv_menu_icon);
            tvMenuLabel = itemView.findViewById(R.id.tv_menu_label);
        }

        @Override
        protected void onBind(TAPCustomKeyboardModel item, int position) {
            tvMenuIcon.setText(item.getIcon());
            tvMenuLabel.setText(item.getLabel());

            itemView.setOnClickListener(v -> onMenuClicked(item.getType()));
        }
    }

    private void onMenuClicked(TAPCustomKeyboardModel.Type type) {
        switch (type) {
            case SEE_PRICE_LIST:
                listener.onSeePriceListClicked();
                break;
            case READ_EXPERT_NOTES:
                listener.onReadExpertNotesClicked();
                break;
            case SEND_SERVICES:
                listener.onSendServicesClicked();
                break;
            case CREATE_ORDER:
                listener.onCreateOrderClicked();
                break;
        }
    }
}
