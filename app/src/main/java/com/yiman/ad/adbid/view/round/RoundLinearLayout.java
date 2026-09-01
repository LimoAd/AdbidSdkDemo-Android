package com.yiman.ad.adbid.view.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.yiman.ad.adbid.R;

/**
 * Local replacement for {@code com.adbid.adx.view.round.LMRoundLinearLayout}.
 */
public class RoundLinearLayout extends LinearLayout {

    private final GradientDrawable backgroundDrawable = new GradientDrawable();
    private final GradientDrawable backgroundPressDrawable = new GradientDrawable();

    @ColorInt
    private int backgroundColor;
    @ColorInt
    private int backgroundPressColor = Integer.MAX_VALUE;
    private int cornerRadius;
    private int cornerRadiusTl;
    private int cornerRadiusTr;
    private int cornerRadiusBl;
    private int cornerRadiusBr;
    private int strokeWidth;
    @ColorInt
    private int strokeColor;

    public RoundLinearLayout(Context context) {
        this(context, null);
    }

    public RoundLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundLinearLayout);
        backgroundColor = ta.getColor(R.styleable.RoundLinearLayout_round_backgroundColor, 0);
        backgroundPressColor = ta.getColor(
                R.styleable.RoundLinearLayout_round_backgroundPressColor, Integer.MAX_VALUE);
        cornerRadius = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_round_cornerRadius, 0);
        cornerRadiusTl = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_round_cornerRadius_TL, 0);
        cornerRadiusTr = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_round_cornerRadius_TR, 0);
        cornerRadiusBl = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_round_cornerRadius_BL, 0);
        cornerRadiusBr = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_round_cornerRadius_BR, 0);
        strokeWidth = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_round_strokeWidth, 0);
        strokeColor = ta.getColor(R.styleable.RoundLinearLayout_round_strokeColor, 0);
        ta.recycle();
        applyBackground();
    }

    public void setRoundBackgroundColor(@ColorInt int color) {
        backgroundColor = color;
        applyBackground();
    }

    public void setCornerRadius(int radiusPx) {
        cornerRadius = radiusPx;
        applyBackground();
    }

    private void applyBackground() {
        applyDrawable(backgroundDrawable, backgroundColor, strokeColor);

        if (backgroundPressColor != Integer.MAX_VALUE) {
            applyDrawable(backgroundPressDrawable, backgroundPressColor, strokeColor);
            StateListDrawable selector = new StateListDrawable();
            selector.addState(new int[]{android.R.attr.state_pressed}, backgroundPressDrawable);
            selector.addState(new int[]{}, backgroundDrawable);
            setBackground(selector);
        } else {
            setBackground(backgroundDrawable);
        }
    }

    private void applyDrawable(GradientDrawable drawable, @ColorInt int color, @ColorInt int stroke) {
        drawable.setColor(color);
        if (cornerRadiusTl > 0 || cornerRadiusTr > 0 || cornerRadiusBl > 0 || cornerRadiusBr > 0) {
            drawable.setCornerRadii(new float[]{
                    cornerRadiusTl, cornerRadiusTl,
                    cornerRadiusTr, cornerRadiusTr,
                    cornerRadiusBr, cornerRadiusBr,
                    cornerRadiusBl, cornerRadiusBl
            });
        } else {
            drawable.setCornerRadius(cornerRadius);
        }
        drawable.setStroke(strokeWidth, stroke);
    }
}
