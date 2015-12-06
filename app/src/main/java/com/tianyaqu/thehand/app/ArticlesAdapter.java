package com.tianyaqu.thehand.app;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tianyaqu.thehand.app.database.ArticleEntryModel;
import com.tianyaqu.thehand.app.database.Query;
import com.tianyaqu.thehand.app.utils.CommonUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 2015/11/17.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String mName;
    Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<ArticleEntryModel> articleEntryModels;

    private OnArticleSelectedListener mArticleSelectedListener;

    public ArticlesAdapter(Context ctx, String name) {
        mContext = ctx;
        mName = name;
        mLayoutInflater = LayoutInflater.from(ctx);
    }

    public void loadArticles(){
        articleEntryModels = Query.XSection(mName);
        Log.d(this.getClass().getName(),"actual load" + articleEntryModels.size());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(i == Constants.ITEM_NORMAL) {
            View view = mLayoutInflater.inflate(R.layout.card_item_normal, viewGroup, false);
            return new ItemNormalViewHolder(view);
        }else{
            View view = mLayoutInflater.inflate(R.layout.card_item_with_date,viewGroup,false);
            return new ItemDateViewHolder(view);
        }
    }

    public class ItemNormalViewHolder extends RecyclerView.ViewHolder {
        private TextView mText;
        //private ImageView mImage;
        private CardView mCardView;

        public ItemNormalViewHolder(View view) {
            super(view);
            mText = (TextView) view.findViewById(R.id.article_item_title);
            //mImage = (ImageView)view.findViewById(R.id.article_item_icon);
            if(view instanceof LinearLayout){
                mCardView = (CardView) view.findViewById(R.id.card_view);
            }else {
                mCardView = (CardView)view;
            }
        }
    }

    public class ItemDateViewHolder extends ItemNormalViewHolder{
        private TextView mDate;
        public ItemDateViewHolder(View view) {
            super(view);
            mDate = (TextView) view.findViewById(R.id.article_item_date);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final ArticleEntryModel cur = articleEntryModels.get(i);
        if(cur == null) {
            return;
        }

        final ItemNormalViewHolder holder = (ItemNormalViewHolder) viewHolder;
        holder.mText.setText(cur.title);
        Long date = cur.date;
        if(viewHolder instanceof ItemDateViewHolder){
            ItemDateViewHolder dateHolder = (ItemDateViewHolder) viewHolder;
            dateHolder.mDate.setText(CommonUtils.prettyDateStr(new Date(date)));
        }

        if(cur.readed){
            //TODO if to set click color?
            //holder.mCardView.setCardBackgroundColor(R.color.eco_red);
            //holder.mCardView.setCardBackgroundColor(R.color.cardview_clicked_color);
            //holder.mCardView.setForeground();
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArticleSelectedListener != null) {
                    mArticleSelectedListener.onArticleSelected(holder.mCardView,cur);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return articleEntryModels != null ?articleEntryModels.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return Constants.ITEM_WITH_DATE;

        long curDate = articleEntryModels.get(position).date;
        long preDate = articleEntryModels.get(position - 1).date;

        return (curDate == preDate) ? Constants.ITEM_NORMAL : Constants.ITEM_WITH_DATE;
    }

    /*

        @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ArticleEntry cur = mSection.getEntries().get(i);
        if(cur == null) {
            return;
        }

        ItemNormalViewHolder holder = (ItemNormalViewHolder) viewHolder;
        holder.mText.setText(cur.getName());
        if(viewHolder instanceof ItemDateViewHolder){
            ItemDateViewHolder dateHolder = (ItemDateViewHolder) viewHolder;
            dateHolder.mDate.setText(String.valueOf(cur.getDate()));
        }
        Log.d("put text", cur.getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArticleSelectedListener != null) {
                    mArticleSelectedListener.onArticleSelected(cur);
                }
            }
        });

    }

    @Overrid
    public int getItemCount() {
        return mSection != null ? mSection.getEntries().size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return Constants.ITEM_WITH_DATE;

        long curDate = mSection.getEntries().get(position).getDate();
        long preDate = mSection.getEntries().get(position - 1).getDate();

        return (curDate == preDate) ? Constants.ITEM_NORMAL : Constants.ITEM_WITH_DATE;
    }
        */

    public void setArticleSelectedListener(OnArticleSelectedListener mArticleSelectedListener) {
        this.mArticleSelectedListener = mArticleSelectedListener;
    }

    public interface OnArticleSelectedListener {
        void onArticleSelected(View v, ArticleEntryModel article);
    }
}
