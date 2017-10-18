package product.dp.io.ab180blog.Memo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jaewanlee on 2017. 9. 11..
 */

public class MemoListAdapter extends RecyclerView.Adapter<MemoListAdapter.ViewHolder> {
    Context context;
    ArrayList<MemoListDatabase> memoDatabases;
    Activity activity;
    MemoListDatabase currentMemoListDatabase;

    public MemoListAdapter(Activity activity, Context context, ArrayList<MemoListDatabase> memoDatabases) {
        this.activity = activity;
        this.context = context;
        this.memoDatabases = memoDatabases;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(product.dp.io.ab180blog.R.layout.recyclerview_item_memo_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        currentMemoListDatabase = memoDatabases.get(position);
        holder.memoTitle_tv.setText(currentMemoListDatabase.getMemo_document_place_name());
        holder.memoCategory_iv.setImageResource(product.dp.io.ab180blog.R.drawable.ic_action_back);
        holder.createDate_tv.setText(new Date(currentMemoListDatabase.getMemo_createDate()).toString());
        holder.memoContent_tv.setText(currentMemoListDatabase.getMemo_content());
        holder.phoneCall_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentMemoListDatabase.getMemo_document_phone().equals("") || currentMemoListDatabase.getMemo_document_phone() == null)
                    Toast.makeText(activity, "연락처 정보가 등록되어 있지 않은 장소입니다", Toast.LENGTH_SHORT).show();
                else {
                    String tel = "tel:" + currentMemoListDatabase.getMemo_document_phone();
                    activity.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                }
                Toast.makeText(context, "phone call", Toast.LENGTH_SHORT).show();
            }
        });
        holder.blogWeb_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .addDefaultShareMenuItem()
                        .setShowTitle(true)
                        .build();

// This is optional but recommended
                CustomTabsHelper.addKeepAliveExtra(activity, customTabsIntent.intent);

// This is where the magic happens...
                CustomTabsHelper.openCustomTab(activity, customTabsIntent,
                        Uri.parse("https://github.com/saschpe/android-customtabs"),
                        new WebViewFallback());
            }
        });

        if (currentMemoListDatabase.getIsNew()) {
            holder.newMemo_iv.setVisibility(View.VISIBLE);
            currentMemoListDatabase.setIsNew(false);
        }

    }

    @Override
    public int getItemCount() {
        return memoDatabases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView memoTitle_tv;
        ImageView memoCategory_iv;
        TextView createDate_tv;
        TextView memoContent_tv;
        ImageButton phoneCall_ib;
        ImageButton blogWeb_ib;

        ImageView newMemo_iv;

        public ViewHolder(View itemView) {
            super(itemView);
            memoTitle_tv = (TextView) itemView.findViewById(product.dp.io.ab180blog.R.id.memoListRecy_title_TextView);
            memoCategory_iv = (ImageView) itemView.findViewById(product.dp.io.ab180blog.R.id.memoListRecy_category_ImageView);
            createDate_tv = (TextView) itemView.findViewById(product.dp.io.ab180blog.R.id.memoListRecy_date_TextView);
            memoContent_tv = (TextView) itemView.findViewById(product.dp.io.ab180blog.R.id.memoListRecy_memoContent_TextView);
            phoneCall_ib = (ImageButton) itemView.findViewById(product.dp.io.ab180blog.R.id.memoListRecy_call_ImageButton);
            blogWeb_ib = (ImageButton) itemView.findViewById(product.dp.io.ab180blog.R.id.memoListRecy_web_ImageButton);
            newMemo_iv = (ImageView) itemView.findViewById(product.dp.io.ab180blog.R.id.memoListRecy_newMemo_ImageView);

        }
    }

}
