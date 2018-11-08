package com.moselo.HomingPigeon.Helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.moselo.HomingPigeon.R;

public class HpRoundedCornerImageView extends android.support.v7.widget.AppCompatImageView {

    private Path path;
    private RectF rounded;
    private AttributeSet attributeSet;
    private int w, h, oldW, oldH, minWidth, minHeight, maxWidth, maxHeight;
    private float dimensionRatio;
    private final float DEFAULT_RADIUS = HpUtils.getInstance().dpToPx(4);

    public float topLeftRad = DEFAULT_RADIUS;
    public float topRightRad = DEFAULT_RADIUS;
    public float bottomLeftRad = DEFAULT_RADIUS;
    public float bottomRightRad = DEFAULT_RADIUS;

    public HpRoundedCornerImageView(Context context) {
        super(context);
        init(null);
    }

    public HpRoundedCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HpRoundedCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(@Nullable AttributeSet attrs) {
        path = new Path();
        rounded = new RectF();

        if (attrs != null) {
            attributeSet = attrs;
        }
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.HpRoundedCornerImageView);
            topLeftRad = typedArray.getDimension(R.styleable.HpRoundedCornerImageView_topLeftRadius, topLeftRad);
            topRightRad = typedArray.getDimension(R.styleable.HpRoundedCornerImageView_topRightRadius, topRightRad);
            bottomLeftRad = typedArray.getDimension(R.styleable.HpRoundedCornerImageView_bottomLeftRadius, bottomLeftRad);
            bottomRightRad = typedArray.getDimension(R.styleable.HpRoundedCornerImageView_bottomRightRadius, bottomRightRad);
            minWidth = (int) typedArray.getDimension(R.styleable.HpRoundedCornerImageView_minWidth, minWidth);
            minHeight = (int) typedArray.getDimension(R.styleable.HpRoundedCornerImageView_minHeight, minHeight);
            minHeight = (int) typedArray.getDimension(R.styleable.HpRoundedCornerImageView_minHeight, minHeight);
            dimensionRatio = typedArray.getFloat(R.styleable.HpRoundedCornerImageView_dimensionRatio, dimensionRatio);
            typedArray.recycle();
        }
    }

    public void setCornerRadius(float radius) {
        this.attributeSet = null;
        this.topLeftRad = radius;
        this.topRightRad = radius;
        this.bottomLeftRad = radius;
        this.bottomRightRad = radius;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setCornerRadius(float topLeftRad, float topRightRad, float bottomLeftRad, float bottomRightRad) {
        this.attributeSet = null;
        this.topLeftRad = topLeftRad;
        this.topRightRad = topRightRad;
        this.bottomLeftRad = bottomLeftRad;
        this.bottomRightRad = bottomRightRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setTopLeftRadius(float topLeftRad) {
        this.attributeSet = null;
        this.topLeftRad = topLeftRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setTopRightRadius(float topRightRad) {
        this.attributeSet = null;
        this.topRightRad = topRightRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setBottomLeftRadius(float bottomLeftRad) {
        this.attributeSet = null;
        this.bottomLeftRad = bottomLeftRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    public void setBottomRightRadius(float bottomRightRad) {
        this.attributeSet = null;
        this.bottomRightRad = bottomRightRad;
        onSizeChanged(w, h, oldW, oldH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(attributeSet);
        this.w = w;
        this.h = h;
        this.oldW = oldw;
        this.oldH = oldh;

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
    public void setMaxWidth(int maxWidth) {
        super.setMaxWidth(maxWidth);
        this.maxWidth = maxWidth;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        super.setMaxHeight(maxHeight);
        this.maxHeight = maxHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        MeasureSpec.makeMeasureSpec(size, mode);

        if (dimensionRatio == 0) {
            return;
        }
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (wMode == MeasureSpec.EXACTLY || hMode == MeasureSpec.EXACTLY) {
            return;
        }

        // Calculate the most appropriate size for the view. Take into
        // account minWidth, minHeight, maxWith, maxHeight and allowed size
        // for the view.
//        int maxWidth = wMode == MeasureSpec.AT_MOST
//                ? Math.min(MeasureSpec.getSize(widthMeasureSpec), this.maxWidth)
//                : this.maxWidth;
//        int maxHeight = hMode == MeasureSpec.AT_MOST
//                ? Math.min(MeasureSpec.getSize(heightMeasureSpec), this.maxHeight)
//                : this.maxHeight;

        // FIXME: 7 November 2018 minWidth AND minHeight ARE NOT WORKING, DRAWABLE DIMENSIONS RETURN WRONG SIZE
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        float ratio = ((float) dWidth) / dHeight;
        Log.e("]]]]", "dWidth: " + dWidth);
        Log.e("]]]]", "dHeight: " + dHeight);

        int resultWidth, resultHeight;
        if (ratio > ((float) maxWidth / minHeight)) {
            // Image width is higher than maxWidth, but height is lower than minHeight
            // Set width to maxWidth, height to minHeight and crop image
            Log.e("]]]]", "ratio >>>: " + ratio);
            Log.e("]]]]", "ratio limit: " + ((float) maxWidth / minHeight));
            resultWidth = maxWidth;
            resultHeight = minHeight;
            setScaleType(ScaleType.CENTER_CROP);
        } else if (ratio < ((float) minWidth / maxHeight)) {
            // Image height is higher than maxHeight, but width is lower than minWidth
            // Set width to minWidth, height to maxHeight and crop image
            Log.e("]]]]", "ratio <<<: " + ratio);
            Log.e("]]]]", "ratio limit: " + ((float) minWidth / maxHeight));
            resultWidth = minWidth;
            resultHeight = maxHeight;
            setScaleType(ScaleType.CENTER_CROP);
        } else if (ratio > dimensionRatio) {
            // Width ratio is higher than limit -> use maxWidth
            Log.e("]]]]", "ratio >: " + ratio);
            if (dWidth > maxWidth) {
                resultWidth = maxWidth;
                resultHeight = (int) (resultWidth / ratio);
            } else if (dWidth < minWidth) {
                resultWidth = minWidth;
                resultHeight = (int) (resultWidth / ratio);
            } else {
                resultWidth = dWidth;
                resultHeight = dHeight;
            }
            setScaleType(ScaleType.FIT_CENTER);
        } else {
            // Height ratio is higher than limit -> use maxHeight
            Log.e("]]]]", "ratio <: " + ratio);
            if (dHeight > maxHeight) {
                resultHeight = maxHeight;
                resultWidth = (int) (resultHeight * ratio);
            } else if (dHeight < minHeight) {
                resultHeight = minHeight;
                resultWidth = (int) (resultHeight * ratio);
            } else {
                resultWidth = dWidth;
                resultHeight = dHeight;
            }
            setScaleType(ScaleType.FIT_CENTER);
        }
        Log.e("]]]]", "resultWidth: " + resultWidth);
        Log.e("]]]]", "resultHeight: " + resultHeight);
        setMeasuredDimension(resultWidth, resultHeight);
    }
}
