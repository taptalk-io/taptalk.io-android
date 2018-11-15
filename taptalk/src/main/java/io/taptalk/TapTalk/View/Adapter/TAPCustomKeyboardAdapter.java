package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Interface.TAPCustomKeyboardInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardModel;
import io.taptalk.Taptalk.R;

public class TAPCustomKeyboardAdapter extends TAPBaseAdapter<TAPCustomKeyboardModel, TAPBaseViewHolder<TAPCustomKeyboardModel>> {

    private TAPCustomKeyboardInterface listener;

    public TAPCustomKeyboardAdapter(List<TAPCustomKeyboardModel> keyboardMenuList, TAPCustomKeyboardInterface listener) {
        setItems(keyboardMenuList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPCustomKeyboardModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeyboardMenuHolder(parent, R.layout.tap_cell_custom_keyboard_menu);
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
