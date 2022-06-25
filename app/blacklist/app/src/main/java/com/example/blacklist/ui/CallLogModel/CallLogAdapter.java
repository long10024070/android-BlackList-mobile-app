package com.example.blacklist.ui.CallLogModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>{

    private List<CallLogItem> mListCallLog ;

    public CallLogAdapter(List<CallLogItem> mListCallLog) {
        this.mListCallLog = mListCallLog;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent , false) ;
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        CallLogItem callLog = mListCallLog.get(position) ;
        if (callLog == null) {return;}
        holder.tvPhoneNumber.setText(callLog.getPhoneNumber());
        holder.tvPhoneName.setText(callLog.getPhoneName() );

        switch (callLog.getCallType()) {
            case "Incoming" :
                holder.imgCallType.setImageResource(R.drawable.call_received);
                break;
            case "Outgoing" :
                holder.imgCallType.setImageResource(R.drawable.call_make);
                break;
            case "Missed" :
                holder.imgCallType.setImageResource(R.drawable.call_missed);
                break;
            case "Blocked" :
                holder.imgCallType.setImageResource(R.drawable.call_blocked);
                break ;
            case "Rejected" :
                holder.imgCallType.setImageResource(R.drawable.call_rejected);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mListCallLog != null) {
            return mListCallLog.size() ;
        }
        return 0;
    }

    public class CallLogViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPhoneNumber ;
        private TextView tvPhoneName ;
        private CircleImageView imgCallType ;

        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhoneNumber = itemView.findViewById(R.id.callLogPhoneNumber) ;
            tvPhoneName = itemView.findViewById(R.id.callLogPhoneName) ;
            imgCallType = itemView.findViewById(R.id.imageCallType) ;
        }
    }
}
