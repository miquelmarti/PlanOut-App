package com.android.planout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.planout.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private ListView categoryListView;
    private BaseAdapter categoryAdapter;
    private ArrayList<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryList = new ArrayList<String>();
        categoryList.add("Sports");
        categoryList.add("Food");
        categoryList.add("Shows");
        categoryList.add("Party");
        categoryList.add("Music");

        List<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
        List<Integer> drawableIdList = new ArrayList<Integer>();
        drawableIdList.add(R.drawable.icon_sports);
        drawableIdList.add(R.drawable.icon_food);
        drawableIdList.add(R.drawable.icon_shows);
        drawableIdList.add(R.drawable.icon_party);
        drawableIdList.add(R.drawable.icon_music);

        for (int i = 0; i < categoryList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", categoryList.get(i));
            map.put("image", drawableIdList.get(i));
            list_map.add(map);
        }

        String[] from = {"name", "image"};
        int[] to = {R.id.text_category, R.id.image_category};
        categoryAdapter = new SimpleAdapter(this, list_map, R.layout.row_customized_category, from, to);
        categoryListView = (ListView) findViewById(R.id.category_listView);
        categoryListView.setAdapter(categoryAdapter);
        categoryListView.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){

            case R.id.action_settings:
                return true;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PlansCategoryActivity.class);
        intent.putExtra("categoryId", position);

        startActivity(intent);
    }
}
