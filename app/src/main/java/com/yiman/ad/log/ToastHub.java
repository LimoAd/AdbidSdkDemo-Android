package com.yiman.ad.log;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public final class ToastHub {

    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static Toast currentToast;

    private ToastHub() {
    }

    public static void show(Context context, String msg) {
        MAIN.post(() -> {
            if (currentToast != null) {
                currentToast.cancel();
            }
            currentToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            currentToast.show();
            MAIN.removeCallbacks(clearRunnable);
            MAIN.postDelayed(clearRunnable, 1200L);
        });
    }

    private static final Runnable clearRunnable = () -> {
        if (currentToast != null) {
            currentToast.cancel();
            currentToast = null;
        }
    };
}
