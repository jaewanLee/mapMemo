package com.example.jaewanlee.mapmemo.LockScreen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jaewanlee.mapmemo.Database.MemoDatabase;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.TranscHash;

import java.util.ArrayList;

/**
 * Created by jaewanlee on 2017. 8. 10..
 */

public class LockScreenAdapter extends RecyclerView.Adapter<LockScreenAdapter.ViewHolder> {
    Context context;
    ArrayList<MemoDatabase> memoDatabases=new ArrayList<>();

    public LockScreenAdapter(Context context) {
        this.context = context;
    }

    public void addData(MemoDatabase memoDatabase) {
        this.memoDatabases.add(memoDatabase);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_lock_screen,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemoDatabase memoDatabase = memoDatabases.get(position);
        holder.title.setText(memoDatabase.getMemo_document_place_name());
        holder.category.setText(TranscHash.categoryHash.get(memoDatabase.getMemo_document_category_group_code()));
        if (memoDatabase.getMemo_document_road_address_name().equals(""))
            holder.addr.setText(memoDatabase.getMemo_document_address_name());
        else holder.addr.setText(memoDatabase.getMemo_document_road_address_name());
        holder.phone.setText(memoDatabase.getMemo_document_phone());
        holder.memo.setText(memoDatabase.getMemo_content());
    }

    @Override
    public int getItemCount() {
        if (memoDatabases == null)
            return 0;
        else return memoDatabases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView title;
        public TextView category;
        public TextView addr;
        public TextView phone;
        public TextView memo;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            title = (TextView) itemView.findViewById(R.id.lockscreen_near_memo_cardview_name_text);
            category = (TextView) itemView.findViewById(R.id.lockscreen_near_memo_cardview_category_text);
            addr = (TextView) itemView.findViewById(R.id.lockscreen_near_memo_cardview_addr_text);
            phone = (TextView) itemView.findViewById(R.id.lockscreen_near_memo_cardview_phone_text);
            memo = (TextView) itemView.findViewById(R.id.lockscreen_near_memo_cardview_memo_text);
        }

    }
}
