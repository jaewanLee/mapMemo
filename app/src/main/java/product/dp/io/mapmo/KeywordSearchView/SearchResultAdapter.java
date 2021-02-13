package product.dp.io.mapmo.KeywordSearchView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import product.dp.io.mapmo.Map.KeywordSearchRepo;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Util.TranscHash;

/**
 * Created by jaewanlee on 2017. 8. 8..
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private Context context;
    private ArrayList<KeywordSearchRepo.KeywordDocuments> keywordDocuments;

    private int lastPosition = -1;

    public SearchResultAdapter(ArrayList items, Context context) {
        this.context = context;
        this.keywordDocuments = items;
    }
    public SearchResultAdapter(Context context){
        this.context=context;
        this.keywordDocuments=new ArrayList<>();
    }
    public void replaceDataSet(ArrayList items){
        this.keywordDocuments=items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(product.dp.io.mapmo.R.layout.cardview_item_keyword_search, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final KeywordSearchRepo.KeywordDocuments keywordDocument = keywordDocuments.get(position);

        holder.title.setText(keywordDocument.getPlace_name());
        if (keywordDocument.getCategory_group_code().equals("") || keywordDocument.getCategory_group_code() == null) {
            holder.category.setImageResource(R.drawable.ic_category_default);
            holder.category_name.setText("");
        } else {
            holder.category.setImageResource(TranscHash.rawToImageCategory(keywordDocument.getCategory_group_code()));
            holder.category_name.setText(TranscHash.rawToreFinedCategory(keywordDocument.getCategory_group_code()));
        }
        if (keywordDocument.getRoad_address_name() == "" || keywordDocument.getRoad_address_name() == null) {
            if (keywordDocument.getAddress_name() == "" || keywordDocument.getAddress_name() == null) {
                holder.addr.setText("업데이트 중");
            } else {
                holder.addr.setText(keywordDocument.getAddress_name());
            }
        } else {
            holder.addr.setText(keywordDocument.getRoad_address_name());
        }
    }


    @Override
    public int getItemCount() {
        return keywordDocuments.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView title;
        public ImageView category;
        public TextView addr;
        public TextView category_name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            title = (TextView) itemView.findViewById(product.dp.io.mapmo.R.id.keyword_search_result_cardview_name_text);
            category = (ImageView) itemView.findViewById(product.dp.io.mapmo.R.id.keyword_search_result_cardview_category_text);
            addr = (TextView) itemView.findViewById(product.dp.io.mapmo.R.id.keyword_search_result_cardview_addr_text);
            category_name=(TextView)itemView.findViewById(R.id.keyword_search_category_name_textView);
        }
    }


}
