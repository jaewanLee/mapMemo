package product.dp.io.mapmo.MemoList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.mapmo.AddMemoView.AddMemoActivity;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.Util.Constant;
import product.dp.io.mapmo.Util.TranscHash;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

/**
 * Created by jaewanlee on 2017. 9. 11..
 */

public class MemoListAdapter extends RecyclerView.Adapter<MemoListAdapter.ViewHolder> {
    Context context;
    ArrayList<MemoListDatabase> memoDatabases;
    Activity activity;
    MemoListDatabase currentMemoListDatabase;
    MemoListAdapter instance;

    public MemoListAdapter(Activity activity, Context context, ArrayList<MemoListDatabase> memoDatabases) {
        this.activity = activity;
        this.context = context;
        this.memoDatabases = memoDatabases;
        instance=this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(product.dp.io.mapmo.R.layout.recyclerview_item_memo_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        currentMemoListDatabase = memoDatabases.get(position);
        holder.memoTitle_tv.setText(currentMemoListDatabase.getMemo_document_place_name());
        String category_name=currentMemoListDatabase.getMemo_document_category_group_code();
        holder.memoCategory_tv.setText(TranscHash.rawToreFinedCategory(category_name));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
        Date currentTime = new Date(currentMemoListDatabase.getMemo_createDate());
        String dTime = formatter.format(currentTime);
        holder.createDate_tv.setText(dTime);
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
            }
        });
        holder.blogWeb_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = currentMemoListDatabase.getMemo_document_place_url();
                if (url.equals("") || url == null) {
                    Toast.makeText(context, "홈페이지가 없는 장소입니다", Toast.LENGTH_SHORT).show();
                } else {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .addDefaultShareMenuItem()
                            .setShowTitle(true)
                            .build();

                    CustomTabsHelper.addKeepAliveExtra(activity, customTabsIntent.intent);

                    CustomTabsHelper.openCustomTab(activity, customTabsIntent,
                            Uri.parse(url),
                            new WebViewFallback());
                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 아이템 선택시
                //TODO 여기뿐만 아니라 메모관련 내용들 중에서 만일 변경될경우 해당 내용들 변경하는거 해줘야함
                Toast.makeText(context, "걍클릭", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity.getApplicationContext(), AddMemoActivity.class);
                intent.putExtra("memo_no", currentMemoListDatabase.getMemo_no());
                intent.putExtra("Tag", Constant.MARKER_TAG_SAVED);
                activity.startActivityForResult(intent, Constant.ADD_MEMO_INTENT);

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(context, "롱클릭", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("title");
                builder.setMessage("이거 삭제할꺼냐");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Realm realm = Realm.getDefaultInstance();
                        final RealmResults<MemoDatabase> realmResults = realm.where(MemoDatabase.class).equalTo("memo_no", currentMemoListDatabase.getMemo_no()).findAll();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmResults.deleteAllFromRealm();
                            }
                        });
                        //TODO adapter 바꾸기
                        instance.memoDatabases.remove(position);
                        instance.notifyItemRemoved(position);

                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        if (currentMemoListDatabase.getIsNew()) {
            holder.newMemo_iv.setVisibility(View.VISIBLE);
            currentMemoListDatabase.setIsNew(false);
        }

    }

    public void setMemoDatabases(ArrayList<MemoListDatabase> memoDatabases) {
        this.memoDatabases = memoDatabases;
    }

    @Override
    public int getItemCount() {
        return memoDatabases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView memoTitle_tv;
        TextView memoContent_tv;
        TextView memoCategory_tv;
        TextView createDate_tv;
        ImageButton phoneCall_ib;
        ImageButton blogWeb_ib;
        ImageView newMemo_iv;

        public ViewHolder(View itemView) {
            super(itemView);
            memoTitle_tv = (TextView) itemView.findViewById(product.dp.io.mapmo.R.id.memoListRecy_title_TextView);
            memoCategory_tv = (TextView) itemView.findViewById(product.dp.io.mapmo.R.id.memoListRecy_category_ImageView);
            createDate_tv = (TextView) itemView.findViewById(product.dp.io.mapmo.R.id.memoListRecy_date_TextView);
            memoContent_tv = (TextView) itemView.findViewById(product.dp.io.mapmo.R.id.memoListRecy_memoContent_TextView);
            phoneCall_ib = (ImageButton) itemView.findViewById(product.dp.io.mapmo.R.id.memoListRecy_call_ImageButton);
            blogWeb_ib = (ImageButton) itemView.findViewById(product.dp.io.mapmo.R.id.memoListRecy_web_ImageButton);
            newMemo_iv = (ImageView) itemView.findViewById(product.dp.io.mapmo.R.id.memoListRecy_newMemo_ImageView);

        }
    }

}
