package com.yiman.ad.appswitch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiman.ad.adbid.R;

import java.util.List;

public class AppSwitchAdapter extends RecyclerView.Adapter<AppSwitchAdapter.ViewHolder> {

    public interface OnSelectionChanged {
        void onChanged();
    }

    private final List<String> appIds;
    private final OnSelectionChanged listener;
    private int selectedIndex = -1;

    public AppSwitchAdapter(@NonNull List<String> appIds,
                            @NonNull String currentAppId,
                            @NonNull OnSelectionChanged listener) {
        this.appIds = appIds;
        this.listener = listener;
        for (int i = 0; i < appIds.size(); i++) {
            if (currentAppId.equals(appIds.get(i))) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex < 0 && !appIds.isEmpty()) {
            selectedIndex = 0;
        }
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_select, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String appId = appIds.get(position);
        holder.textName.setText(holder.itemView.getContext().getString(R.string.app_switch_item_title, appId));
        boolean selected = position == selectedIndex;
        holder.checkBox.setChecked(selected);
        holder.itemView.setSelected(selected);

        holder.itemView.setOnClickListener(v -> {
            if (selectedIndex == position) {
                return;
            }
            int old = selectedIndex;
            selectedIndex = position;
            if (old >= 0) {
                notifyItemChanged(old);
            }
            notifyItemChanged(selectedIndex);
            listener.onChanged();
        });
        holder.checkBox.setOnClickListener(v -> holder.itemView.performClick());
    }

    @Override public int getItemCount() {
        return appIds.size();
    }

    public String getSelectedAppId() {
        if (selectedIndex < 0 || selectedIndex >= appIds.size()) {
            return null;
        }
        return appIds.get(selectedIndex);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            checkBox = itemView.findViewById(R.id.ckb_select);
        }
    }
}
