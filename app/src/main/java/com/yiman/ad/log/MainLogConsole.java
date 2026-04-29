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

    private static final SpannableStringBuilder LOG_BUILDER = new SpannableStringBuilder();
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private static WeakReference<ScrollView> scrollViewRef;
    private static WeakReference<TextView> textViewRef;

    private MainLogConsole() {
    }

    public static void bind(ScrollView scrollView, TextView textView) {
        scrollViewRef = new WeakReference<>(scrollView);
        textViewRef = new WeakReference<>(textView);
        render();
    }

    public static void unbind() {
        scrollViewRef = null;
        textViewRef = null;
    }

    public static void clear() {
        LOG_BUILDER.clear();
        render();
    }

    public static void info(String msg) {
        append(msg, COLOR_INFO);
    }

    public static void success(String msg) {
        append(msg, COLOR_SUCCESS);
    }

    public static void warning(String msg) {
        append(msg, COLOR_WARNING);
    }

    public static void error(String msg) {
        append(msg, COLOR_ERROR);
    }

    public static void toast(android.content.Context context, String msg) {
        ToastHub.show(context, msg);
    }

    private static void append(String msg, int color) {
        if (msg == null) {
            return;
        }
        if (LOG_BUILDER.length() > 0) {
            LOG_BUILDER.append('\n');
        }
        String lineText = formatNow() + " " + msg;
        SpannableString line = new SpannableString(lineText);
        line.setSpan(new ForegroundColorSpan(color), 0, line.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        LOG_BUILDER.append(line);
        trimIfNeeded();
        render();
    }

    private static String formatNow() {
        synchronized (TIME_FORMAT) {
            return TIME_FORMAT.format(new Date());
        }
    }

    private static void trimIfNeeded() {
        if (LOG_BUILDER.length() <= MAX_LOG_LENGTH) {
            return;
        }
        int trimEnd = Math.min(LOG_BUILDER.length(), LOG_BUILDER.length() - MAX_LOG_LENGTH + 400);
        LOG_BUILDER.delete(0, trimEnd);
    }

    private static void render() {
        TextView textView = textViewRef == null ? null : textViewRef.get();
        if (textView == null) {
            return;
        }
        textView.post(() -> {
            textView.setText(LOG_BUILDER);
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
