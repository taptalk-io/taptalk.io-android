package io.taptalk.TapTalk.Helper.OverScrolled;

public interface IOverScrollUpdateListener {

    /**
     * The invoked callback.
     *
     * @param decor  The associated over-scroll 'decorator'.
     * @param state  One of: {@link IOverScrollState#STATE_IDLE}, {@link IOverScrollState#STATE_DRAG_START_SIDE},
     *               {@link IOverScrollState#STATE_DRAG_START_SIDE} or {@link IOverScrollState#STATE_BOUNCE_BACK}.
     * @param offset The currently visible offset created due to over-scroll.
     */
    void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset);
}
