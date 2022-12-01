package online.nonamekill.module.import_progress.adapter;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import online.nonamekill.android.module.import_progress.R;
import online.nonamekill.module.import_progress.data.MessageData;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<MessageData> list = new ArrayList<>();
    private final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
    private final List<MessageHolder> messageHolderList= new LinkedList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_text_view_layout, viewGroup, false);
        MessageHolder messageHolder = new MessageHolder(view);
        messageHolderList.add(messageHolder);
        return messageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MessageHolder holder = (MessageHolder) viewHolder;
        MessageData data = list.get(position);

        if (!TextUtils.isEmpty(data.getType())) {
            String ip = data.getMessage();
            SpannableString spannable = new SpannableString(ip);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor(data.getType())), 0, ip.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.getTextView().setText(spannable);
            holder.getTextView().setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            holder.getTextView().setText(data.getMessage());
        }

        holder.getDataTextView().setText(data.getThreadDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addMessage(String msg) {
        MessageData data = new MessageData(msg);
        addMessage(data);
    }

    public void addMessage(MessageData data) {
        data.setThreadDate(Thread.currentThread().getName() + " - "+dateFormat.format(new Date()));
        list.add(data);
        notifyItemChanged(list.indexOf(data));
    }

    public List<MessageData> getList() {
        return list;
    }

    public List<MessageHolder> getMessageHolderList(){
        return messageHolderList;
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final TextView dataTextView;


        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.message_text_view);
            dataTextView = itemView.findViewById(R.id.message_date_text_view);
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getDataTextView() {
            return dataTextView;
        }
    }

}
