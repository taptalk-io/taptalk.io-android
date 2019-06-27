package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Interface.TapTalkCustomKeyboardInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.Taptalk.R;

public class TAPCustomKeyboardAdapter extends TAPBaseAdapter<TAPCustomKeyboardItemModel, TAPBaseViewHolder<TAPCustomKeyboardItemModel>> {

    private TapTalkCustomKeyboardInterface customKeyboardInterface;

    public TAPCustomKeyboardAdapter(List<TAPCustomKeyboardItemModel> keyboardMenuList, TapTalkCustomKeyboardInterface customKeyboardInterface) {
        setItems(keyboardMenuList);
        this.customKeyboardInterface = customKeyboardInterface;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPCustomKeyboardItemModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeyboardMenuHolder(parent, R.layout.tap_cell_custom_keyboard_menu);
    }

    class KeyboardMenuHolder extends TAPBaseViewHolder<TAPCustomKeyboardItemModel> {

        ImageView ivMenuIcon;
        TextView tvMenuLabel;

        KeyboardMenuHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivMenuIcon = itemView.findViewById(R.id.iv_menu_icon);
            tvMenuLabel = itemView.findViewById(R.id.tv_menu_label);
        }

        @Override
        protected void onBind(TAPCustomKeyboardItemModel item, int position) {
            if (null != item.getIconImage()) {
                Glide.with(itemView.getContext()).load(item.getIconImage()).into(ivMenuIcon);
            } else if (null != item.getIconURL()) {
                Glide.with(itemView.getContext()).load(item.getIconURL()).into(ivMenuIcon);
            } else {
                ivMenuIcon.setVisibility(View.GONE);
            }
            tvMenuLabel.setText(item.getItemName());

            itemView.setOnClickListener(v -> customKeyboardInterface.onCustomKeyboardClicked(item));
        }
    }
}
