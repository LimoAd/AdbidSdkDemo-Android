package com.yiman.ad.appswitch;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yiman.ad.adbid.R;

import java.util.List;

public class AppSwitchDialog extends Dialog {

    public interface OnConfirmListener {
        void onConfirm(@NonNull String appId);
    }

    private final List<String> appIds;
    private final String currentAppId;
    private final OnConfirmListener listener;

    private RecyclerView recyclerView;
    private Button btnConfirm;
    private TextView tvSelectedSummary;
    private AppSwitchAdapter adapter;

    public AppSwitchDialog(@NonNull Context context,
                           @NonNull List<String> appIds,
                           @NonNull String currentAppId,
                           @NonNull OnConfirmListener listener) {
        super(context, R.style.BottomDialog);
        this.appIds = appIds;
        this.currentAppId = currentAppId;
        this.listener = listener;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_app_select);
        initView();
        setListener();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvSelectedSummary = findViewById(R.id.tv_selected_summary);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppSwitchAdapter(appIds, currentAppId, this::updateSelectedSummary);
        recyclerView.setAdapter(adapter);
        updateSelectedSummary();
    }

    private void setListener() {
        btnConfirm.setOnClickListener(v -> {
            String selected = adapter.getSelectedAppId();
            if (selected != null) {
                listener.onConfirm(selected);
            }
            dismiss();
        });
    }

    private void updateSelectedSummary() {
        String selected = adapter.getSelectedAppId();
        if (selected == null) {
            tvSelectedSummary.setText(getContext().getString(R.string.app_switch_dialog_summary_empty));
        } else {
            tvSelectedSummary.setText(getContext().getString(R.string.app_switch_dialog_summary,
                    selected, appIds.size()));
        }
    }

    @Override public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.DialogAnimation_FromBottom);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            window.setDimAmount(0.28f);
        }
    }
}
