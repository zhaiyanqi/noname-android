package online.nonamekill.android.module.icon;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Outline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.ViewHolder> {

    private final ArrayList<IconInfo> mList = new ArrayList<>();
    private final IconClickListener mListener;

    public IconListAdapter(ArrayList<IconInfo> list, IconClickListener listener) {
        mList.clear();
        mList.addAll(list);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_icon_item, null);

        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.set
            }
        });


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IconInfo iconInfo = mList.get(position);

        holder.mIconImage.setImageResource(iconInfo.getIconId());
        holder.mIconImage.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(iconInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIconImage = null;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIconImage = itemView.findViewById(R.id.module_icon_item_image);
        }
    }

}
