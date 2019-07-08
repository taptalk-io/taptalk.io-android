package io.taptalk.TapTalk.View.Adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMenuItem;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_SEND_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_VIEW_MEMBERS;

public class TAPMenuButtonAdapter extends TAPBaseAdapter<TAPMenuItem, TAPBaseViewHolder<TAPMenuItem>> {

    private TAPChatProfileActivity.ProfileMenuInterface menuInterface;

    public TAPMenuButtonAdapter(List<TAPMenuItem> items, TAPChatProfileActivity.ProfileMenuInterface menuInterface) {
        setItems(items, true);
        this.menuInterface = menuInterface;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPMenuItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuButtonVH(parent, R.layout.tap_cell_menu_button);
    }

    private class MenuButtonVH extends TAPBaseViewHolder<TAPMenuItem> {

        private ConstraintLayout clContainer;
        private ImageView ivMenuIcon, ivRightArrow;
        private TextView tvMenuLabel;
        private SwitchCompat swMenuSwitch;

        MenuButtonVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivMenuIcon = itemView.findViewById(R.id.iv_menu_icon);
            ivRightArrow = itemView.findViewById(R.id.iv_right_arrow);
            tvMenuLabel = itemView.findViewById(R.id.tv_menu_label);
            swMenuSwitch = itemView.findViewById(R.id.sw_menu_switch);
        }

        @SuppressLint("PrivateResource")
        @Override
        protected void onBind(TAPMenuItem item, int position) {
            ivMenuIcon.setImageResource(item.getIconRes());
            ivMenuIcon.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(item.getIconColorRes())));

            tvMenuLabel.setText(item.getMenuLabel());
            TypedArray typedArray = itemView.getContext().obtainStyledAttributes(item.getTextStyleRes(), R.styleable.TextAppearance);
            tvMenuLabel.setTextColor(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1));
            typedArray.recycle();

            if (item.isSwitchMenu()) {
                swMenuSwitch.setVisibility(View.VISIBLE);
                swMenuSwitch.setChecked(item.isChecked());
                int switchColorRes = item.isChecked() ? R.color.tapSwitchActiveBackgroundColor : R.color.tapSwitchInactiveBackgroundColor;
                changeSwitchColor(itemView.getResources().getColor(switchColorRes));
                swMenuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    private ValueAnimator transitionToActive, transitionToInactive;

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        item.setChecked(isChecked);
                        menuInterface.onMenuClicked(item);
                        if (isChecked) {
                            // Turn switch ON
                            getTransitionInactive().cancel();
                            getTransitionActive().start();
                            changeSwitchColor(itemView.getResources().getColor(R.color.tapSwitchActiveBackgroundColor));

                        } else {
                            // Turn switch OFF
                            getTransitionActive().cancel();
                            getTransitionInactive().start();
                            changeSwitchColor(itemView.getResources().getColor(R.color.tapSwitchInactiveBackgroundColor));
                        }
                    }

                    private ValueAnimator getTransitionActive() {
                        if (null == transitionToActive) {
                            transitionToActive = ValueAnimator.ofArgb(
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationInactive),
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationActive));
                            transitionToActive.setDuration(DEFAULT_ANIMATION_TIME);
                            transitionToActive.addUpdateListener(valueAnimator -> ivMenuIcon.setColorFilter(
                                    (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                        }
                        return transitionToActive;
                    }

                    private ValueAnimator getTransitionInactive() {
                        if (null == transitionToInactive) {
                            transitionToInactive = ValueAnimator.ofArgb(
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationActive),
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationInactive));
                            transitionToInactive.setDuration(DEFAULT_ANIMATION_TIME);
                            transitionToInactive.addUpdateListener(valueAnimator -> ivMenuIcon.setColorFilter(
                                    (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                        }
                        return transitionToInactive;
                    }
                });
            } else {
                swMenuSwitch.setVisibility(View.GONE);
            }

            if (item.getMenuID() == MENU_VIEW_MEMBERS && !item.isSwitchMenu()) {
                ivRightArrow.setVisibility(View.VISIBLE);
            } else if (!item.isSwitchMenu()) ivRightArrow.setVisibility(View.GONE);

            clContainer.setOnClickListener(v -> {
                if (item.isSwitchMenu()) {
                    swMenuSwitch.setChecked(!swMenuSwitch.isChecked());
                } else {
                    menuInterface.onMenuClicked(item);
                }
            });
        }

        private void changeSwitchColor(int color) {
            DrawableCompat.setTintList(DrawableCompat.wrap(swMenuSwitch.getThumbDrawable()),
                    ColorStateList.valueOf(color));
            DrawableCompat.setTintList(DrawableCompat.wrap(swMenuSwitch.getTrackDrawable()),
                    ColorStateList.valueOf(TAPUtils.getInstance().adjustAlpha(color, 0.3f)));
        }
    }
}
