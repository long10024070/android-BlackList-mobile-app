package com.example.blacklist.ui.myBlackList.MyBlackListModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.R;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.ui.CallLogModel.CallLogAdapter;
import com.example.blacklist.ui.CallLogModel.CallLogItem;
import com.example.blacklist.ui.myBlackList.MyBlackListViewModel;

import java.util.List;

public class MyBlackListAdapter extends RecyclerView.Adapter <MyBlackListAdapter.MyBlackListViewHolder> {
    private List<MyBlackListItem> mMyBlackList ;
    private Context context;

    public MyBlackListAdapter(List<MyBlackListItem> mMyBlackList, Context ctx) {
        this.mMyBlackList = mMyBlackList;
        this.context = ctx;
    }

    @NonNull
    @Override
    public MyBlackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_black_list_item, parent , false) ;
        return new MyBlackListAdapter.MyBlackListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBlackListViewHolder holder, int position) {
        MyBlackListItem myBlackListItem = mMyBlackList.get(holder.getAbsoluteAdapterPosition()) ;
        if (myBlackListItem == null) {return;}
        holder.tvPhoneNumber.setText(myBlackListItem.getPhoneNumber());
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(holder.itemView.getContext(),mMyBlackList.get(holder.getAbsoluteAdapterPosition()).getPhoneNumber(), Toast.LENGTH_SHORT).show();
                BlackList.getInstance(context).deleteBlockedNumber(mMyBlackList.get(holder.getAbsoluteAdapterPosition()).getPhoneNumber());
                mMyBlackList.remove(holder.getAbsoluteAdapterPosition());
                notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                notifyItemRangeChanged(holder.getAbsoluteAdapterPosition(), getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMyBlackList != null) {return mMyBlackList.size() ;}
        return 0;
    }

    public class MyBlackListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPhoneNumber ;
        private ImageView imgDelete ;

        public MyBlackListViewHolder (@NonNull View itemView) {
            super(itemView) ;
            tvPhoneNumber = itemView.findViewById(R.id.myBlackListNumber) ;
            imgDelete = itemView.findViewById(R.id.imgDeleteBlackList) ;
        }
    }
}
