package com.yiman.ad.adbid.platform;

import static com.yiman.ad.adbid.platform.PlatformManager.save;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adbid.sdk.AdbidSdkConfiguration;
import com.yiman.ad.adbid.R;

import java.util.ArrayList;
import java.util.List;

public class BottomSelectDialog extends Dialog {
    private RecyclerView mRecyclerView;
    private SelectAdapter mAdapter;
    private Button mBtnConfirm;
    private CheckBox mCbSelectAll;
    private OnConfirmClickListener mListener;
    private List<ItemModel> mItemList;

    public BottomSelectDialog(@NonNull Context context, OnConfirmClickListener listener) {
        super(context, R.style.BottomDialog); // 使用一个底部弹窗的样式
        this.mListener = listener;
        mItemList = PlatformManager.getDialogList();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_select);
        initView();
        setListener();

    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mCbSelectAll = findViewById(R.id.cb_select_all);

        boolean isSelectAll = true;
        for (ItemModel itemModel : mItemList) {
            if (!itemModel.isSelected()) {
                isSelectAll = false;
                break;
            }
        }
        mCbSelectAll.setChecked(isSelectAll);
        // 设置RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SelectAdapter(mItemList, mCbSelectAll);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setListener() {
        mBtnConfirm.setOnClickListener(v -> {
            if (mListener != null) {
                save(mItemList);
                mListener.onConfirmClick();
            }
            dismiss();
        });
    }


    public interface OnConfirmClickListener {
        void onConfirmClick();
    }

    /**
     * 显示弹窗，并设置窗口的显示动画和位置（从底部弹出）
     */
    @Override public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM); // 底部显示
            window.setWindowAnimations(R.style.DialogAnimation_FromBottom); // 设置进入退出动画
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
