package com.example.blacklist.ui.callLogModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.R;

import java.util.List;

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
        holder.tvPhoneName.setText(callLog.getPhoneName());
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

        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhoneNumber = itemView.findViewById(R.id.callLogPhoneNumber) ;
            tvPhoneName = itemView.findViewById(R.id.callLogPhoneName) ;
        }
    }
}
