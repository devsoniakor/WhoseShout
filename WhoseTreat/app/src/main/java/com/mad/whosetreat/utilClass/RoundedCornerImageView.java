package com.mad.whosetreat.utilClass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Reference: https://stackoverflow.com/questions/2459916/how-to-make-an-imageview-with-rounded-corners
 *
 * The class will be used for making image rounded corner.
 */
public class RoundedCornerImageView extends androidx.appcompat.widget.AppCompatImageView{

    // fields
    private float radius = 18.0f;
    private Path path;
    private RectF rect;

    public RoundedCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
