package com.yiman.ad.log;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class MainLogConsole {

    private static final int COLOR_INFO = Color.parseColor("#BFD5FF");
    private static final int COLOR_SUCCESS = Color.parseColor("#A7EFC1");
    private static final int COLOR_WARNING = Color.parseColor("#FFE08A");
    private static final int COLOR_ERROR = Color.parseColor("#FFB0B0");
    private static final int MAX_LOG_LENGTH = 6000;

    private final SpannableStringBuilder logBuilder = new SpannableStringBuilder();
    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private WeakReference<ScrollView> scrollViewRef;
    private WeakReference<TextView> textViewRef;

    public void bind(ScrollView scrollView, TextView textView) {
        scrollViewRef = new WeakReference<>(scrollView);
        textViewRef = new WeakReference<>(textView);
        render();
    }

    public void unbind() {
        scrollViewRef = null;
        textViewRef = null;
    }

    public void clear() {
        logBuilder.clear();
        render();
    }

    public void info(String msg) {
        append(msg, COLOR_INFO, "💡");
    }

    public void success(String msg) {
        append(msg, COLOR_SUCCESS, "✅");
    }

    public void warning(String msg) {
        append(msg, COLOR_WARNING, "⚠️");
    }

    public void error(String msg) {
        append(msg, COLOR_ERROR, "❌");
    }

    public static void toast(android.content.Context context, String msg) {
        ToastHub.show(context, msg);
    }

    private void append(String msg, int color, String icon) {
        if (msg == null) {
            return;
        }
        if (logBuilder.length() > 0) {
            logBuilder.append('\n');
        }
        String lineText = formatNow() + " " + icon + " " + msg;
        SpannableString line = new SpannableString(lineText);
        line.setSpan(new ForegroundColorSpan(color), 0, line.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        logBuilder.append(line);
        trimIfNeeded();
        render();
    }

    private String formatNow() {
        synchronized (timeFormat) {
            return timeFormat.format(new Date());
        }
    }

    private void trimIfNeeded() {
        if (logBuilder.length() <= MAX_LOG_LENGTH) {
            return;
        }
        int trimEnd = Math.min(logBuilder.length(), logBuilder.length() - MAX_LOG_LENGTH + 400);
        logBuilder.delete(0, trimEnd);
    }

    private void render() {
        TextView textView = textViewRef == null ? null : textViewRef.get();
        if (textView == null) {
            return;
        }
        textView.post(() -> {
            textView.setText(logBuilder);
            ScrollView scrollView = scrollViewRef == null ? null : scrollViewRef.get();
            if (scrollView != null) {
                scrollView.post(() -> {
                    View content = scrollView.getChildAt(0);
                    if (content == null) {
                        return;
                    }
                    int visibleHeight = scrollView.getHeight() - scrollView.getPaddingTop()
                            - scrollView.getPaddingBottom();
                    if (content.getHeight() > visibleHeight) {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    } else {
                        scrollView.scrollTo(0, 0);
                    }
                });
            }
        });
    }
}
