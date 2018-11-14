package io.taptalk.TapTalk.Helper.OverScrolled;

public interface IOverScrollStateListener {

    /**
     * The invoked callback.
     *
     * @param decor The associated over-scroll 'decorator'.
     * @param oldState The old over-scroll state; ID's specified by {@link IOverScrollState}, e.g.
     *                 {@link IOverScrollState#STATE_IDLE}.
     * @param newState The <b>new</b> over-scroll state; ID's specified by {@link IOverScrollState},
     *                 e.g. {@link IOverScrollState#STATE_IDLE}.
     */
    void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState);
}
