//package online.nonamekill.android.container;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//import online.nonamekill.android.R;
//
//public class ModuleListAdapter extends RecyclerView.Adapter<ModuleListAdapter.ViewHolder> {
//
//    private final ArrayList<String> mModuleList = new ArrayList<>();
//
//    public ModuleListAdapter(ArrayList<String> list) {
//        mModuleList.clear();
//        mModuleList.addAll(list);
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_list_item, null);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String moduleName = mModuleList.get(position);
//        holder.mTextView.setText(moduleName);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mModuleList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private View mRootView = null;
//        private TextView mTextView = null;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            mRootView = itemView.findViewById(R.id.module_list_item);
//            mTextView = itemView.findViewById(R.id.module_name);
//        }
//    }
//}
