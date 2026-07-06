package com.yiman.ad;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adbid.utils.sp.PreferencesUtils;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.platform.BottomSelectDialog;
import com.yiman.ad.adbid.platform.PlatformManager;
import com.yiman.ad.adbid.view.BounceMarqueeTextView;
import com.yiman.ad.appswitch.AppSwitchDialog;
import com.yiman.ad.log.MainLogConsole;
import com.yiman.ad.log.ToastHub;

import java.util.Collections;
import java.util.List;

public final class MainPanelController {

    private final MainActivity activity;
    private final Switch s2sCheckBox;
    private final BounceMarqueeTextView textPlatform;
    private final View platformContainer;

    public MainPanelController(@NonNull MainActivity activity) {
        this.activity = activity;
        this.s2sCheckBox = activity.findViewById(R.id.btn_check_s2s);
        this.textPlatform = activity.findViewById(R.id.text_platform);
        this.platformContainer = activity.findViewById(R.id.llayout_platform);
    }

    public void bind() {
        ScrollView logScroll = activity.findViewById(R.id.scroll_log);
        TextView logText = activity.findViewById(R.id.text_log);
        MainLogConsole.bind(logScroll, logText);
        MainLogConsole.clear();

        textPlatform.setText(PlatformManager.getSelectedNamesText());
        activity.findViewById(R.id.btn_change).setOnClickListener(
                view -> new BottomSelectDialog(activity, () -> {
                    textPlatform.setText(PlatformManager.getSelectedNamesText());
                }).show());

        boolean isAdxMode = PreferencesUtils.getBoolean("is_check_adx", false);
        updatePlatformVisibility(isAdxMode);


        boolean isS2SMode = PreferencesUtils.getBoolean("is_check_s2s", false);
        s2sCheckBox.setChecked(isS2SMode);
        AdConfig.setS2SBiddingEnabled(isS2SMode);
        s2sCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            PreferencesUtils.put("is_check_s2s", checked);
            AdConfig.setS2SBiddingEnabled(checked);
        });

    }

    public void unbind() {
        MainLogConsole.unbind();
    }

    public IAdLoad getCurrentAdLoad() {
        return AdConfig.getAdLoad(activity);
    }



    private void restartApp() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Process.killProcess(Process.myPid());
            System.exit(0);
        }, 500);
    }

    private void updatePlatformVisibility(boolean isAdxMode) {
        platformContainer.setVisibility(isAdxMode ? View.GONE : View.VISIBLE);
    }
}
