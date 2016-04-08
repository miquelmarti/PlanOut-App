package com.android.planout.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.planout.R;
import com.android.planout.activities.MainActivity;

import java.util.ArrayList;

import entity.Plan;

/**
 * Created by Agusti on 29/5/15.
 */
public class PlansAdapter extends BaseAdapter {

    private Context mContext;
    //Instead of this data we could only use Plan class with all this data inside
    private ArrayList<Plan> planList;
    private int resId;

    public PlansAdapter(Context context, ArrayList<Plan> plans){
        this.planList = plans;
        mContext = context;
    }

    @Override
    public int getCount() {
        return planList.size();
    }

    @Override
    public Plan getItem(int position) {
        return planList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;

        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_customized_plans, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) ((LinearLayout)convertView).getChildAt(0);
            viewHolder.title  = (TextView) ((LinearLayout)((LinearLayout) convertView).getChildAt(1)).getChildAt(0);
            viewHolder.description= (TextView)((LinearLayout)((LinearLayout)((LinearLayout)convertView).getChildAt(1)).getChildAt(1)).getChildAt(0);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder)convertView.getTag();
        if(planList.get(position).getIconId().equalsIgnoreCase("other")){
            switch(planList.get(position).getCategoryId()){
                case MainActivity.CATEGORY_MUSIC:
                    resId = R.drawable.icon_music;
                    break;
                case MainActivity.CATEGORY_FOOD:
                    resId = R.drawable.icon_food;
                    break;
                case MainActivity.CATEGORY_SPORTS:
                    resId = R.drawable.icon_sports;
                    break;
                case MainActivity.CATEGORY_SHOWS:
                    resId = R.drawable.icon_shows;
                    break;
                case MainActivity.CATEGORY_PARTY:
                    resId = R.drawable.icon_party;
                    break;
            }
        }else {
            resId = parent.getResources().getIdentifier("icon_" + planList.get(position).getIconId().toLowerCase(), "drawable", parent.getContext().getPackageName());
        }
        viewHolder.image.setImageDrawable(parent.getResources().getDrawable(resId));
        viewHolder.title.setText(planList.get(position).getTitle());
        viewHolder.description.setText(planList.get(position).getDescription());

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
        TextView description;
    }



}
