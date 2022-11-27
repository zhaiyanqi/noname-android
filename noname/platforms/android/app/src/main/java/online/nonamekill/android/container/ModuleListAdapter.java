package online.nonamekill.android.container;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import online.nonamekill.android.R;
import online.nonamekill.android.listener.OnModuleItemListener;
import online.nonamekill.android.view.ModuleItemView;

public class ModuleListAdapter extends RecyclerView.Adapter<ModuleListAdapter.ViewHolder> {

    private final ArrayList<String> mModuleList = new ArrayList<>();
    private int mSelectedIndex = -1;
    private OnModuleItemListener mModuleItemListener = null;

    public ModuleListAdapter(ArrayList<String> list, OnModuleItemListener listener) {
        mModuleList.clear();
        mModuleList.addAll(list);

        mModuleItemListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String moduleName = mModuleList.get(position);
        holder.mTextView.setText(moduleName);
        holder.mTextView.setOnClickListener((view) -> {
            setSelect(position);
            mModuleItemListener.onModuleClicked(mModuleList.get(position));
        });
        holder.mTextView.setSelect(mSelectedIndex == position);
    }

    private void setSelect(int idx) {
        if (mSelectedIndex != idx) {
            int last = mSelectedIndex;
            mSelectedIndex = idx;
            notifyItemChanged(idx);

            if (last != -1) {
                notifyItemChanged(last);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mModuleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View mRootView = null;
        private ModuleItemView mTextView = null;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mRootView = itemView.findViewById(R.id.module_list_item);
            mTextView = itemView.findViewById(R.id.module_name);
        }
    }
}
