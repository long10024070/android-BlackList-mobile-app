package com.example.blacklist.ui.Contact;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.R;
import com.example.blacklist.Telephone.BlackList;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    //Init
    List<ContactModel> arrayList;
    private Context context ;

    //Create constructor
    public ContactAdapter(List<ContactModel> arrayList, Context ctx) {
        this.arrayList = arrayList;
        this.context = ctx ;
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Init contact model
        ContactModel model = arrayList.get(position);

        //Set name
        holder.tvName.setText(model.getName());
        //Set number
        holder.tvNumber.setText(model.getNumber());

        Boolean isBlackList = BlackList.getInstance(context).inBlackList(generalizePhoneNumber(model.getNumber()));
        holder.sBlackList.setChecked(isBlackList);

        holder.sBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(holder.itemView.getContext(),holder.sBlackList.isChecked() ? "True" : "False", Toast.LENGTH_SHORT).show();
                if (holder.sBlackList.isChecked()) {
                    BlackList.getInstance(context).putBlockedNumber(generalizePhoneNumber(model.getNumber()));
                    Toast.makeText(holder.itemView.getContext(),"Block successfully!", Toast.LENGTH_SHORT).show();
                }
                else {
                    BlackList.getInstance(context).deleteBlockedNumber(generalizePhoneNumber(model.getNumber()));
                    Toast.makeText(holder.itemView.getContext(),"Unblock successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String generalizePhoneNumber (String number) {
        StringBuilder myNumber = new StringBuilder() ;
        for (int i = 0 ; i < number.length() ; ++i) {
            if ((number.charAt(i) >= '0') && (number.charAt(i) <='9')) {
                myNumber.append(number.charAt(i)) ;
                //Log.v("CharTest" , myNumber.toString()) ;
            }
        }
        return myNumber.toString() ;
    }

    @Override
    public int getItemCount() {

        if (arrayList != null) {
            return arrayList.size() ;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvNumber;
        private Switch sBlackList ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
            sBlackList = itemView.findViewById(R.id.blackListSwitch) ;
        }
    }
}
