package com.yiman.ad.adbid.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiman.ad.adbid.R;

public class TitleBar extends RelativeLayout {

    TextView mTextView;
    OnClickListener mListener;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.include_title_bar, this, true);

        mTextView = ((TextView) findViewById(R.id.tv_title));
        mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(v);
                }
            }
        });
    }

    public void setTitle(int titleResId) {
        mTextView.setText(titleResId);
    }

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }


}