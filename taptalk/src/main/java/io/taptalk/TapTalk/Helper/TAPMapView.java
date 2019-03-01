package io.taptalk.TapTalk.Helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.google.android.gms.maps.MapView;

import io.taptalk.Taptalk.R;

public class TAPMapView extends MapView {

    private Path path;
    private RectF rounded;
    private AttributeSet attributeSet;
    private int w, h, oldW, oldH;
    private final float DEFAULT_RADIUS = TAPUtils.getInstance().dpToPx(8);

    public float topLeftRad = DEFAULT_RADIUS;
    public float topRightRad = DEFAULT_RADIUS;
    public float bottomLeftRad = DEFAULT_RADIUS;
    public float bottomRightRad = DEFAULT_RADIUS;

    public TAPMapView(Context context) {
        super(context);
        init(null);
    }

    public TAPMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TAPMapView(Context context, AttributeSet attrs, int defStyle) {
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
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TAPMapView);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        MeasureSpec.makeMeasureSpec(size, mode);
    }
}
