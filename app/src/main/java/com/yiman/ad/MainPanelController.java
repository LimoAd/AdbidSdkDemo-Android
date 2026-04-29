package com.yiman.ad;

import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yiman.ad.adbid.R;
import com.yiman.ad.adbid.platform.BottomSelectDialog;
import com.yiman.ad.adbid.platform.PlatformManager;
import com.yiman.ad.adbid.view.BounceMarqueeTextView;
import com.yiman.ad.log.MainLogConsole;

public final class MainPanelController {

    private final MainActivity activity;
    private final BounceMarqueeTextView textPlatform;

    public MainPanelController(@NonNull MainActivity activity) {
        this.activity = activity;
        this.textPlatform = activity.findViewById(R.id.text_platform);
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
    }

    public void unbind() {
        MainLogConsole.unbind();
    }






}
