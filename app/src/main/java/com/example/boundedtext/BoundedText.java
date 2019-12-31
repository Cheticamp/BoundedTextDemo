package com.example.boundedtext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;

/**
 * This class does not accommodate spanned text.
 */
public class BoundedText extends androidx.appcompat.widget.AppCompatTextView {
    private final Rect mLineBounds = new Rect();
    private final Rect mTextBounds = new Rect();
    private final Paint mRectPaint = new Paint();

    public BoundedText(Context context) {
        super(context);
        init();
    }

    public BoundedText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoundedText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(2f);
        mRectPaint.setColor(Color.RED);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Layout layout = getLayout();
        getTightBounds(layout, 0, mTextBounds);

        if (layout.getLineCount() > 1) { // Multi-line
            // We already have bounds of first line. Check bounds of last line since it will
            // be the bottom of the bounding rectangle for all the text.
            getTightBounds(layout, layout.getLineCount() - 1, mLineBounds);
            mTextBounds.bottom = mLineBounds.bottom;

            // Now check remaining lines for min left bound and max right bound.
            for (int line = 1; line < layout.getLineCount(); line++) {
                getTightBounds(layout, line, mLineBounds);
                if (mLineBounds.left < mTextBounds.left) {
                    mTextBounds.left = mLineBounds.left;
                }
                if (mLineBounds.right > mTextBounds.right) {
                    mTextBounds.right = mLineBounds.right;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.drawRect(mTextBounds, mRectPaint);
        canvas.restore();
    }

    private void getTightBounds(Layout layout, int line, Rect bounds) {
        int firstCharOnLine = layout.getLineStart(line);
        int lastCharOnLine = layout.getLineVisibleEnd(line);
        CharSequence s = getText().subSequence(firstCharOnLine, lastCharOnLine);

        // Get the bounds for the text. Top and bottom are measured from the baseline. Left
        // and right are measured from 0.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getPaint().getTextBounds(s, 0, s.length(), bounds);
        } else {
            getPaint().getTextBounds(s.toString(), 0, s.length(), bounds);
        }
        int baseline = layout.getLineBaseline(line);
        bounds.top = baseline + bounds.top;
        bounds.bottom = baseline + bounds.bottom;
    }
}