package com.beautycare.discount.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautycare.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

/**
 * Created by Eric.Lau on 16/3/7.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements View.OnClickListener{

    private LayoutInflater mLayoutInflater;
    private List<ItemBean> mDatas;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    private DisplayImageOptions options;


    public MyAdapter(Context context,List<ItemBean> list) {
        this.mDatas = list;
        mLayoutInflater = LayoutInflater.from(context);


        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory()
                .cacheOnDisc()
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_card_discount,parent,false);
        MyViewHolder viewHolder= new MyViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ItemBean itemBean = mDatas.get(position);
//        holder.imageView.setImageResource(itemBean.photo);
//        holder.title.setText(itemBean.title);
//        holder.content.setText(itemBean.content);

        holder.title.setText(itemBean.getTitle());
        holder.content.setText(itemBean.getContent());
        ImageLoader.getInstance().displayImage(itemBean.getURL(),holder.imageView,options);

        //将mData保存在Tag中以便onClick使用
        holder.itemView.setTag(itemBean);
    }


    @Override
    public int getItemCount() {
       return mDatas.size();
    }


    public void addAll(List<ItemBean> list) {
        int lastIndex = this.mDatas.size();
        if (this.mDatas.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
            notifyDataSetChanged();
        }
    }

    /*
    *
    * 设置recyclerview的点击接口回调   Start
    * 等
    *
    * */


    //RecyclerView并没有clicklistener 所以要自己写一个接口定义OnItemClickListener
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, ItemBean data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(ItemBean)v.getTag());
        }
    }
    /*
    *
    * End
    *
    * */



    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title,content;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.item_photo);
            title = (TextView)itemView.findViewById(R.id.item_title);
            content = (TextView)itemView.findViewById(R.id.item_desc);
        }
    }
}
