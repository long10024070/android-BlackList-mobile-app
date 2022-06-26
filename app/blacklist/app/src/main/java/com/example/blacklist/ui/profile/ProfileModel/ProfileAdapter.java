package com.example.blacklist.ui.profile.ProfileModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.R;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListAdapter;
import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListItem;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<ProfileItem> mProfileList ;
    private Context context ;

    public ProfileAdapter(List<ProfileItem> mProfileList, Context ctx) {
        this.mProfileList = mProfileList;
        this.context = ctx;
    }

    @NonNull
    @Override
    public ProfileAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent , false) ;
        return new ProfileAdapter.ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ProfileViewHolder holder, int position) {
        ProfileItem profileItem = mProfileList.get(holder.getAbsoluteAdapterPosition()) ;
        if (profileItem == null) {return;}

        holder.subcribeItem.setText(profileItem.getSubcribeItem());
        holder.subcribeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlackList.getInstance(context).unsubcribeUser(profileItem.getSubcribeItem());
                mProfileList.remove(holder.getAbsoluteAdapterPosition());
                notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                notifyItemRangeChanged(holder.getAbsoluteAdapterPosition(), getItemCount());
                Toast.makeText(holder.itemView.getContext(),"Remove successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mProfileList != null) {return mProfileList.size() ;}
        return 0;
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {
        private TextView subcribeItem ;
        private ImageView subcribeDelete ;
        private EditText subcribeEnterItem ;
        private Button addButon ;
        private Switch isSubDefault ;

        public ProfileViewHolder (@NonNull View itemView) {
            super(itemView) ;
            subcribeItem = itemView.findViewById(R.id.subcribeItem) ;
            subcribeDelete = itemView.findViewById(R.id.deleteSubcribe) ;
            subcribeEnterItem = itemView.findViewById(R.id.subcribeEditText) ;
            addButon = itemView.findViewById(R.id.addButton) ;
        }
    }
}
