package com.yiman.ad.adbid.platform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiman.ad.adbid.R;

import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder>{
    private final List<ItemModel> mData;
    private final CheckBox mCheckBoxSelectAll; // 用于与“全选”复选框联动

    public SelectAdapter(List<ItemModel> data, CheckBox selectAllCheckBox) {
        this.mData = data;
        this.mCheckBoxSelectAll = selectAllCheckBox;
        setupSelectAllListener();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paltform_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = mData.get(position);
        holder.tvName.setText(item.getName());
        holder.cbItem.setChecked(item.isSelected());

        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            updateSelectAllCheckBoxState();
        });
    }

    @Override public int getItemCount() {
        return mData.size();
    }

    private void updateSelectAllCheckBoxState() {
        mCheckBoxSelectAll.setOnCheckedChangeListener(null);
        if (mData.isEmpty()) {
            mCheckBoxSelectAll.setChecked(false);
            return;
        }
        for (ItemModel item : mData) {
            if (!item.isSelected()) {
                mCheckBoxSelectAll.setChecked(false);
                setupSelectAllListener();
                return;
            }
        }
        mCheckBoxSelectAll.setChecked(true);
        setupSelectAllListener();
    }


    private void setupSelectAllListener() {
        mCheckBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (ItemModel item : mData) {
                item.setSelected(isChecked);
            }
            notifyDataSetChanged();
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbItem;
        TextView tvName;
        ViewHolder(View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.ckb_select);
            tvName = itemView.findViewById(R.id.text_name);
        }
    }
}
