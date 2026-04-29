package com.yiman.ad.adbid.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatTextView;

public class BounceMarqueeTextView extends AppCompatTextView {

    private static final long BASE_DURATION_MS = 2200L;
    private static final long DURATION_PER_100PX_MS = 900L;
    private ObjectAnimator marqueeAnimator;

    public BounceMarqueeTextView(Context context) {
        super(context);
        init();
    }

    public BounceMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BounceMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSingleLine(true);
        setHorizontalFadingEdgeEnabled(false);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(this::restartIfNeeded);
    }

    @Override protected void onDetachedFromWindow() {
        stopAnimator();
        super.onDetachedFromWindow();
    }

    @Override protected void onTextChanged(CharSequence text, int start, int lengthBefore,
            int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        post(this::restartIfNeeded);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(this::restartIfNeeded);
    }

    private void restartIfNeeded() {
        stopAnimator();
        Layout layout = getLayout();
        if (layout == null || getWidth() <= 0) {
            return;
        }
        int contentWidth = (int) Math.ceil(layout.getLineWidth(0));
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int overflow = contentWidth - availableWidth;
        if (overflow <= 0) {
            scrollTo(0, 0);
            return;
        }
        long duration = BASE_DURATION_MS + (overflow * DURATION_PER_100PX_MS / 100L);
        marqueeAnimator = ObjectAnimator.ofInt(this, "scrollX", 0, overflow);
        marqueeAnimator.setDuration(duration);
        marqueeAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        marqueeAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        marqueeAnimator.setInterpolator(new LinearInterpolator());
        marqueeAnimator.start();
    }

    private void stopAnimator() {
        if (marqueeAnimator != null) {
            marqueeAnimator.cancel();
            marqueeAnimator = null;
        }
        scrollTo(0, 0);
    }
}
