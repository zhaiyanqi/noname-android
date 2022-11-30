package online.nonamekill.common.versionAdapter;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import online.nonamekill.common.util.XPopupUtil;
import online.nonamekill.lib_common.R;

@RequiresApi(api = Build.VERSION_CODES.N)
public abstract class VersionListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final List<VersionData> list = new ArrayList<>();

    public Boolean isLocalExtension = false;

    public VersionListRecyclerAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_list_layout, parent, false);
        return new VersionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        VersionHolder holder = (VersionHolder) viewHolder;

        VersionData data = list.get(position);

        if(Objects.isNull(data)) return;

        holder.nameTextView.setText(data.getName());
        if (!TextUtils.isEmpty(data.getSize())) {
            holder.sizeTextView.setText(data.getSize());
        } else {
            holder.sizeTextView.setVisibility(View.GONE);
        }

        if(isLocalExtension){
            holder.isHideSwitchView.setChecked(data.getShow());
            holder.enableSwitchView.setChecked(data.getEnable());
            holder.enableSwitchParentView.setVisibility(View.VISIBLE);
            holder.isHideSwitchParentView.setVisibility(View.VISIBLE);
            holder.isHideSwitchView.setOnClickListener(v->onSwitch(v,data));
            holder.enableSwitchView.setOnClickListener(v->onSwitch(v,data));
            holder.pathTextView.setVisibility(View.GONE);
            holder.dateTextView.setVisibility(View.GONE);
        }

        holder.pathTextView.setText(data.getPath());
        holder.dateTextView.setText(data.getDate());

        holder.itemView.setSelected(isInitSelected(data));

        holder.itemView.setOnClickListener(v -> onItemClick(v, data));
    }

    protected abstract void onItemClick(View view, VersionData data);

    public abstract void onItemDelete(VersionData data);

    public abstract void replaceList(List<VersionData> l);

    protected void onSwitch(View view, VersionData data){

    }

    public boolean isInitSelected(VersionData data) {
        return false;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clearAll() {
        list.clear();
        notifyDataSetChanged();
    }

    // 清空选择
    public void unSelectAll() {
        for (VersionData data : list) {
            data.setSelected(false);
        }
    }

    public static class VersionHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView = null;
        private TextView sizeTextView = null;
        private TextView pathTextView = null;
        private TextView dateTextView = null;
        // 是否开启
        private SwitchCompat enableSwitchView = null;
        // 是否隐藏，就是关掉扩展了 extensions.remove
        private SwitchCompat isHideSwitchView = null;

        private View enableSwitchParentView = null;
        private View isHideSwitchParentView = null;

        public VersionHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.version_list_name);
            sizeTextView = itemView.findViewById(R.id.version_list_size);
            pathTextView = itemView.findViewById(R.id.version_list_path);
            dateTextView = itemView.findViewById(R.id.version_list_date);
            isHideSwitchView = itemView.findViewById(R.id.extension_list_show);
            enableSwitchView = itemView.findViewById(R.id.extension_list_enable);
            isHideSwitchParentView = itemView.findViewById(R.id.version_list_show);
            enableSwitchParentView = itemView.findViewById(R.id.version_list_enable);
        }
    }
}
