package com.example.jaewanlee.mapmemo.Memo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jaewanlee on 2017. 9. 11..
 */

public class MemoListShareAdapter  extends RecyclerView.Adapter<MemoListShareAdapter.ViewHolder>{
    Context context;
    ArrayList<MemoListDatabase> memoDatabases;

    public MemoListShareAdapter(Context context,ArrayList<MemoListDatabase> memoDatabases){
        this.context=context;
        this.memoDatabases=memoDatabases;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_share, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MemoDatabase memoDatabase=memoDatabases.get(position).getMemoDatabase();

        holder.memoTitle_tv.setText(memoDatabase.getMemo_document_place_name());
        holder.memoCategory_iv.setImageResource(R.drawable.ic_action_back);
        holder.createDate_tv.setText(new Date(memoDatabase.getMemo_createDate()).toString());
        holder.memoContent_tv.setText(memoDatabase.getMemo_content());
        holder.isSharable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.isSharable.isChecked()){
                    memoDatabases.get(position).setIsChecked(true);
                    Toast.makeText(context, "체크", Toast.LENGTH_SHORT).show();
                }
                else{
                    memoDatabases.get(position).setIsChecked(false);
                    Toast.makeText(context, "안체크", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memoDatabases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView memoTitle_tv;
        ImageView memoCategory_iv;
        TextView createDate_tv;
        TextView memoContent_tv;
        CheckBox isSharable;

        public ViewHolder(final View itemView) {
            super(itemView);
            memoTitle_tv = (TextView) itemView.findViewById(R.id.memoListshare_title_TextView);
            memoCategory_iv = (ImageView) itemView.findViewById(R.id.memoListshare_category_ImageView);
            createDate_tv = (TextView) itemView.findViewById(R.id.memoListshare_date_TextView);
            memoContent_tv = (TextView) itemView.findViewById(R.id.memoListshare_memoContent_TextView);
            isSharable=(CheckBox) itemView.findViewById(R.id.memoListshare_share_CheckButton);

        }
    }
}
