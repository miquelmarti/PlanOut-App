package com.android.planout.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.android.planout.R;
import com.android.planout.adapters.PlansAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entity.Plan;
import entity.Plan_restAPI;

public class PlansCategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private ListView planListView;
    private BaseAdapter planAdapter;
    private ArrayList<Plan> planList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_category);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        Bundle bundle = getIntent().getExtras();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int categoryId = bundle.getInt("categoryId");
        String categoryTitle = "";

        switch (categoryId){
            case MainActivity.CATEGORY_FOOD:
                categoryTitle = "Food Category";
                break;

            case MainActivity.CATEGORY_MUSIC:
                categoryTitle = "Music Category";
                break;

            case MainActivity.CATEGORY_PARTY:
                categoryTitle = "Party Category";
                break;

            case MainActivity.CATEGORY_SHOWS:
                categoryTitle = "Shows Category";
                break;

            case MainActivity.CATEGORY_SPORTS:
                categoryTitle = "Sports Category";
                break;

        }

        getSupportActionBar().setTitle(categoryTitle);

        planList = new ArrayList<Plan>();

        planAdapter = new PlansAdapter(this, planList);
        planListView = (ListView) findViewById(R.id.plansbycat_listView);
        planListView.setAdapter(planAdapter);
        planListView.setOnItemClickListener(this);

        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.setAdapter(planList, planAdapter);
        mAsyncTask.setTextView((TextView) findViewById(R.id.noplan_text));
        mAsyncTask.setCategoryId(categoryId);
        mAsyncTask.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        Intent intent = new Intent(view.getContext(), PlanActivity.class);
        intent.putExtra("plan",planList.get(position));
        startActivity(intent);
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, List<Plan>> {

        //Params: User
        //Progress: Void. No utilizamos progress bar
        //Results: boolean
        private List<Plan> planList;
        private BaseAdapter planAdapter;
        private Context context;
        private int resId;
        private int categoryId;
        private TextView noPlans;

        public void MyAsyncTask(){
            planList = new ArrayList<>();
        }

        public void setContext(Context context){this.context = context;}

        public void setCategoryId(int id){ categoryId = id;}

        public void setTextView(TextView text){ noPlans = text; }

        public void setAdapter(List<Plan> planList, BaseAdapter adapter){
            this.planList = planList;
            planAdapter = adapter;
        }

        @Override
        protected List<Plan> doInBackground(Void... parameter) {
            try {
                return Plan_restAPI.getPlansByCategory(categoryId);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Plan> result) {

            if(result != null){
                if(result.size()!=0) {
                    noPlans.setVisibility(View.GONE);

                    for (int i = 0; i < result.size(); i++) {
                        planList.add(result.get(result.size() - 1 - i));
                    }
                    planAdapter.notifyDataSetChanged();
                }else
                    noPlans.setVisibility(View.VISIBLE);
            }else{
                noPlans.setVisibility(View.VISIBLE);
            }
        }
    }

}
