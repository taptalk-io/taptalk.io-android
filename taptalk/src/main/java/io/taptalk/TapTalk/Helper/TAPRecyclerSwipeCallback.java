package io.taptalk.TapTalk.Helper;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import io.taptalk.TapTalk.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

@Deprecated
public class TAPRecyclerSwipeCallback extends ItemTouchHelper.Callback {

    public interface SwipeInterface {
        void onItemSwiped(RecyclerView.ViewHolder viewHolder);
    }

    private SwipeInterface swipeInterface;
    private RecyclerView.ViewHolder currentViewHolder;
    private int buttonState = View.GONE;
    private boolean swipeBack = false;

    public TAPRecyclerSwipeCallback(SwipeInterface swipeInterface) {
        this.swipeInterface = swipeInterface;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int movementFlag = makeMovementFlags(0, LEFT | RIGHT);
        Log.e("RecyclerSwipeCallback", "getMovementFlags: " + movementFlag);
        return movementFlag;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        Log.e("RecyclerSwipeCallback", "onMove: ");
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e("RecyclerSwipeCallback", "onSwiped: " + direction);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            Log.e("RecyclerSwipeCallback", "convertToAbsoluteDirection: 0");
            return 0;
        }
        Log.e("RecyclerSwipeCallback", "convertToAbsoluteDirection: " + super.convertToAbsoluteDirection(flags, layoutDirection));
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
//            Log.e("RecyclerSwipeCallback", "onChildDraw: setTouchListener");
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        currentViewHolder = viewHolder;
        drawButtons(c, viewHolder, dX);
    }

    private long lastReplyButtonAnimationTime = 0L;
    private float replyButtonProgress = 0f;
    private boolean startTracking = false;
    private boolean isVibrate = false;

    private void drawButtons(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX) {
        Drawable replyButton = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.tap_ic_reply_circle_white);
        long newTime = System.currentTimeMillis();
        long dt = Math.min(17, newTime - lastReplyButtonAnimationTime);
        lastReplyButtonAnimationTime = newTime;
        boolean showing = dX >= (30);
        if (showing) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 180.0f;
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f;
                } else {
                    viewHolder.itemView.invalidate();
                }
            }
        } else if (dX <= 0.0f) {
            replyButtonProgress = 0f;
            startTracking = false;
            isVibrate = false;
        } else {
            if (replyButtonProgress > 0.0f) {
                replyButtonProgress -= dt / 180.0f;
                if (replyButtonProgress < 0.1f) {
                    replyButtonProgress = 0f;
                } else {
                    viewHolder.itemView.invalidate();
                }
            }
        }
        int alpha;
        float scale;
        if (showing) {
            if (replyButtonProgress <= 0.8f) {
                scale = 1.2f * (replyButtonProgress / 0.8f);
            } else {
                scale = 1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f);
            }
            alpha = (int) Math.min(255f, 255 * (replyButtonProgress / 0.8f));
        } else {
            scale = replyButtonProgress;
            alpha = (int) Math.min(255f, 255 * replyButtonProgress);
        }

        replyButton.setAlpha(alpha);
        if (startTracking) {
            if (!isVibrate && dX >= (100)) {
                viewHolder.itemView.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                );
                isVibrate = true;
            }
        }

        int x;
        if (dX > (130)) {
            x = (130) / 2;
        } else {
            x = (int) (dX / 2);
        }

        int y = (viewHolder.itemView.getTop() + viewHolder.itemView.getMeasuredHeight() / 2);

        replyButton.setBounds(
                (int) (x - (12) * scale),
                (int) (y - (11) * scale),
                (int) (x + (12) * scale),
                (int) (y + (10) * scale)
        );
        replyButton.draw(canvas);
        replyButton.setAlpha(255);

//        float buttonWidthWithoutPadding = buttonWidth - 20;
//        float corners = 16;
//
//        View itemView = viewHolder.itemView;
//        Paint p = new Paint();
//
//        RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
//        p.setColor(Color.BLUE);
//        c.drawRoundRect(leftButton, corners, corners, p);
//        drawText("EDIT", c, leftButton, p);
//
//        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
//        p.setColor(Color.RED);
//        c.drawRoundRect(rightButton, corners, corners, p);
//        drawText("DELETE", c, rightButton, p);
//
//        buttonInstance = null;
//        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
//            buttonInstance = leftButton;
//        }
//        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
//            buttonInstance = rightButton;
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(Canvas c,
                                  RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
//            if (swipeBack) {
//                if (dX < - TAPUtils.dpToPx(30)) {
//                    buttonState = View.VISIBLE;
//                }
//                if (buttonState != View.GONE) {
//                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                    setItemsClickable(recyclerView, false);
//                }
//            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                TAPRecyclerSwipeCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener((v1, event1) -> false);
                setItemsClickable(recyclerView, true);
                swipeBack = false;
                buttonState = View.GONE;
            }
            return false;
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }
}
