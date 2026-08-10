package com.yiman.ad;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adbid.sdk.AdbidSdkConfiguration;
import com.adbid.utils.sp.PreferencesUtils;
import com.yiman.ad.adbid.AdConfig;
import com.yiman.ad.adbid.AdbidAdLoad;
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
    private final MainLogConsole logConsole = new MainLogConsole();
    private final TextView textAppId;
    private final Switch s2sCheckBox;
    private final BounceMarqueeTextView textPlatform;

    public MainPanelController(@NonNull MainActivity activity) {
        this.activity = activity;
        this.textAppId = activity.findViewById(R.id.text_app_id_value);
        this.s2sCheckBox = activity.findViewById(R.id.btn_check_s2s);
        this.textPlatform = activity.findViewById(R.id.text_platform);
    }

    public void bind() {
        bindLogConsole(true);

        textPlatform.setText(PlatformManager.getSelectedNamesText());
        activity.findViewById(R.id.btn_change).setOnClickListener(
                view -> new BottomSelectDialog(activity, () -> {
                    textPlatform.setText(PlatformManager.getSelectedNamesText());
                }).show());

        boolean isS2SMode = PreferencesUtils.getBoolean("is_check_s2s", false);
        s2sCheckBox.setChecked(isS2SMode);
        AdConfig.setS2SBiddingEnabled(isS2SMode);
        s2sCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            PreferencesUtils.put("is_check_s2s", checked);
            AdConfig.setS2SBiddingEnabled(checked);
        });

        activity.findViewById(R.id.btn_switch_app_id).setOnClickListener(v -> showAppSwitchDialog());
        refreshCurrentAppId();
    }

    public void rebindLogConsole() {
        bindLogConsole(false);
    }

    public void unbind() {
        logConsole.unbind();
    }

    @NonNull public MainLogConsole getLogConsole() {
        return logConsole;
    }

    public IAdLoad getCurrentAdLoad() {
        return AdbidAdLoad.getInstance(activity, logConsole);
    }

    private void showAppSwitchDialog() {
        List<String> ids = AdConfig.getAvailableAppIds();
        if (ids.isEmpty()) {
            ToastHub.show(activity, "当前模式无可用应用");
            return;
        }
        Collections.sort(ids);
        String current = AppIdStore.getSelectedAppKey();

        new AppSwitchDialog(activity, ids, current, selectedAppId -> {
            if (selectedAppId.equals(current)) {
                return;
            }
            AppIdStore.saveSelectedAppId(selectedAppId);
            refreshCurrentAppId();

            if (AdbidSdkConfiguration.Instance.isInit()) {
                ToastHub.show(activity, "已切换 " + selectedAppId + "，请手动重启");
                restartApp();
            } else {
                ToastHub.show(activity, "已切换 " + selectedAppId + "，请重新初始化");
            }
        }).show();
    }

    private void restartApp() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Process.killProcess(Process.myPid());
            System.exit(0);
        }, 500);
    }

    private void refreshCurrentAppId() {
        textAppId.setText(AppIdStore.getSelectedAppKey());
    }

    private void bindLogConsole(boolean clearLogs) {
        ScrollView logScroll = activity.findViewById(R.id.scroll_log);
        TextView logText = activity.findViewById(R.id.text_log);
        logConsole.bind(logScroll, logText);
        if (clearLogs) {
            logConsole.clear();
        }
    }
}
