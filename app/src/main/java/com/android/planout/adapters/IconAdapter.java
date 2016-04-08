package com.android.planout.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.planout.R;

import java.util.ArrayList;

/**
 * Created by Agusti on 29/5/15.
 */
public class IconAdapter extends BaseAdapter {

    private Context mContext;
    //Instead of this data we could only use Plan class with all this data inside
    private ArrayList<String> mText;
    private ArrayList<Drawable> mImageList;

    public IconAdapter(Context context, ArrayList<String> mText, ArrayList<Drawable> mImageList){
        this.mText = mText;
        this.mImageList = mImageList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mText.size();
    }

    @Override
    public Object getItem(int position) {
        return mText.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;

        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_customized_category, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) ((LinearLayout)convertView).getChildAt(0);
            viewHolder.text = (TextView) ((LinearLayout)convertView).getChildAt(1);
            convertView.setTag(viewHolder);
        }



        viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.image.setImageDrawable(mImageList.get(position));
        viewHolder.text.setText(mText.get(position));

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView text;
    }



}
