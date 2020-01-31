package io.taptalk.TapTalk.Helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import io.taptalk.Taptalk.R;


/**
 **** Use app:cornerRadius to set a universal corner radius for the ImageView
 **** Setting individual radius (e.g. app:topLeftRadius) will override cornerRadius value
 **** app:dimensionRatio is used to set a limit to the ImageView's dimension ratio
 **** app:minWidth, app:minHeight, android:maxWidth, android:maxHeight are required along with
 *    app:dimensionRatio to to limit the view's ratio
 **** corner radius value might be inaccurate (less than the set value)
 */
public class TAPRoundedCornerImageView extends android.support.v7.widget.AppCompatImageView {

    private Path path;
    private RectF rounded;
    private AttributeSet attributeSet;
    private int w, h, oldW, oldH, imageWidth, imageHeight, minWidth, minHeight, maxWidth, maxHeight;
    private float dimensionRatio;
    private final float DEFAULT_RADIUS = TAPUtils.dpToPx(4);

    public float topLeftRad = DEFAULT_RADIUS;
    public float topRightRad = DEFAULT_RADIUS;
    public float bottomLeftRad = DEFAULT_RADIUS;
    public float bottomRightRad = DEFAULT_RADIUS;

    public TAPRoundedCornerImageView(Context context) {
        super(context);
        init(null);
    }

    public TAPRoundedCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TAPRoundedCornerImageView(Context context, AttributeSet attrs, int defStyle) {
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
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TAPRoundedCornerImageView);
            // Set values from cornerRadius
            topLeftRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_cornerRadius, topLeftRad);
            topRightRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_cornerRadius, topRightRad);
            bottomLeftRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_cornerRadius, bottomLeftRad);
            bottomRightRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_cornerRadius, bottomRightRad);
            // Set values from individual radius
            topLeftRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_topLeftRadius, topLeftRad);
            topRightRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_topRightRadius, topRightRad);
            bottomLeftRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_bottomLeftRadius, bottomLeftRad);
            bottomRightRad = typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_bottomRightRadius, bottomRightRad);

            dimensionRatio = typedArray.getFloat(R.styleable.TAPRoundedCornerImageView_dimensionRatio, dimensionRatio);
            minWidth = (int) typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_minWidth, minWidth);
            minHeight = (int) typedArray.getDimension(R.styleable.TAPRoundedCornerImageView_minHeight, minHeight);
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

    public void setImageDimensions(int width, int height) {
        imageWidth = width;
        imageHeight = height;
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

        if (dimensionRatio == 0 || minWidth == 0 || minHeight == 0 || maxWidth == 0 || maxHeight == 0) {
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

        int resultWidth, resultHeight;
        float ratio = ((float) imageWidth) / imageHeight;
        if (ratio > ((float) maxWidth / minHeight)) {
            // Image width is higher than maxWidth, but height is lower than minHeight
            // Set width to maxWidth, height to minHeight and crop image
            resultWidth = maxWidth;
            resultHeight = minHeight;
            setScaleType(ScaleType.CENTER_CROP);
        } else if (ratio < ((float) minWidth / maxHeight)) {
            // Image height is higher than maxHeight, but width is lower than minWidth
            // Set width to minWidth, height to maxHeight and crop image
            resultWidth = minWidth;
            resultHeight = maxHeight;
            setScaleType(ScaleType.CENTER_CROP);
        } else if (ratio > dimensionRatio) {
            // Width ratio is higher than limit -> use maxWidth
            if (imageWidth > maxWidth) {
                resultWidth = maxWidth;
                resultHeight = (int) (resultWidth / ratio);
            } else if (imageWidth < minWidth) {
                resultWidth = minWidth;
                resultHeight = (int) (resultWidth / ratio);
            } else {
                resultWidth = imageWidth;
                resultHeight = imageHeight;
            }
            setScaleType(ScaleType.FIT_CENTER);
        } else {
            // Height ratio is higher than limit -> use maxHeight
            if (imageHeight > maxHeight) {
                resultHeight = maxHeight;
                resultWidth = (int) (resultHeight * ratio);
            } else if (imageHeight < minHeight) {
                resultHeight = minHeight;
                resultWidth = (int) (resultHeight * ratio);
            } else {
                resultWidth = imageWidth;
                resultHeight = imageHeight;
            }
            setScaleType(ScaleType.FIT_CENTER);
        }
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }
}
