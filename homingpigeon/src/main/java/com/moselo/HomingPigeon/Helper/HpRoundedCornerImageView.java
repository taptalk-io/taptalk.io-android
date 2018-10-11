package com.moselo.HomingPigeon.Helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class HpRoundedCornerImageView extends android.support.v7.widget.AppCompatImageView {

    public float topLeftRad = HpUtils.getInstance().dpToPx(4);
    public float topRightRad = HpUtils.getInstance().dpToPx(4);
    public float bottomLeftRad = HpUtils.getInstance().dpToPx(4);
    public float bottomRightRad = HpUtils.getInstance().dpToPx(4);
    private Path path;
    private RectF rect, rounded;
    private int w, h, oldW, oldH;

    public HpRoundedCornerImageView(Context context) {
        super(context);
        init();
    }

    public HpRoundedCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HpRoundedCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        path = new Path();
        rounded = new RectF();
    }

    public void setCornerRadius(float radius) {
        this.topLeftRad = radius;
        this.topRightRad = radius;
        this.bottomLeftRad = radius;
        this.bottomRightRad = radius;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setCornerRadius(float topLeftRad, float topRightRad, float bottomLeftRad, float bottomRightRad) {
        this.topLeftRad = topLeftRad;
        this.topRightRad = topRightRad;
        this.bottomLeftRad = bottomLeftRad;
        this.bottomRightRad = bottomRightRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setTopLeftRadius(float topLeftRad) {
        this.topLeftRad = topLeftRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setTopRightRadius(float topRightRad) {
        this.topRightRad = topRightRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setBottomLeftRadius(float bottomLeftRad) {
        this.bottomLeftRad = bottomLeftRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setBottomRightRadius(float bottomRightRad) {
        this.bottomRightRad = bottomRightRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
        this.w = w;
        this.h = h;
        this.oldW = oldw;
        this.oldH = oldh;
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());

        // Top Left
        rounded.set(0, 0, (2 * topLeftRad), (2 * topLeftRad));
        path.moveTo(0, topLeftRad);
        path.arcTo(rounded, -180, 90);
        path.rLineTo(w - topLeftRad - topRightRad, 0);

        // Top Right
        rounded.set(w - (2 * topRightRad), 0, w, (2 * topRightRad));
        path.arcTo(rounded, -90, 90);
        path.rLineTo(0, h - topRightRad - bottomRightRad);

        // Bottom Right
        rounded.set(w - (2 * bottomRightRad), h - (2 * bottomRightRad), w, h);
        path.arcTo(rounded, 0, 90);
        path.rLineTo(-w + bottomRightRad + bottomLeftRad, 0);

        // Bottom Left
        rounded.set(0, h - (2 * bottomLeftRad), (2 * bottomLeftRad), h);
        path.arcTo(rounded, 90, 90);
        path.rLineTo(0, -h + bottomLeftRad + topLeftRad);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        MeasureSpec.makeMeasureSpec(size, mode);
    }
}