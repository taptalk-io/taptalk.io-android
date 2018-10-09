package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Model.HpCustomKeyboardModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpCustomKeyboardAdapter extends HpBaseAdapter<HpCustomKeyboardModel, HpBaseViewHolder<HpCustomKeyboardModel>> {

    public HpCustomKeyboardAdapter(List<HpCustomKeyboardModel> keyboardMenuList) {
        setItems(keyboardMenuList);
    }

    @NonNull
    @Override
    public HpBaseViewHolder<HpCustomKeyboardModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeyboardMenuHolder(parent, R.layout.hp_cell_custom_keyboard_menu);
    }

    class KeyboardMenuHolder extends HpBaseViewHolder<HpCustomKeyboardModel> {

        TextView tvMenuIcon, tvMenuLabel;

        protected KeyboardMenuHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvMenuIcon = itemView.findViewById(R.id.tv_menu_icon);
            tvMenuLabel = itemView.findViewById(R.id.tv_menu_label);
        }

        @Override
        protected void onBind(HpCustomKeyboardModel item, int position) {
            tvMenuIcon.setText(item.getIcon());
            tvMenuLabel.setText(item.getLabel());

            itemView.setOnClickListener(v -> onMenuClicked(item.getType()));
        }
    }

    private void onMenuClicked(HpCustomKeyboardModel.Type type) {
        switch (type) {
            case SEE_PRICE_LIST:
                break;
            case READ_EXPERT_NOTES:
                break;
            case SEND_SERVICES:
                break;
            case CREATE_ORDER_CARD:
                break;
        }
    }
}
