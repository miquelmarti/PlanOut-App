package com.android.planout.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.planout.R;
import com.android.planout.adapters.PlansAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entity.Plan;
import entity.Plan_restAPI;


public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    ImageButton popup_button, browser_button, postplan_button;
    private ListView planListView;
    private BaseAdapter planAdapter;
    private ArrayList<Plan> planList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        planList = new ArrayList<Plan>();
        planAdapter = new PlansAdapter(rootView.getContext(), planList);
        planListView = (ListView) rootView.findViewById(R.id.lastplans_listview);
        planListView.setAdapter(planAdapter);
        planListView.setOnItemClickListener(this);

        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.setAdapter(planList, planAdapter);
        mAsyncTask.execute();

        //BROWSE PLANS
        browser_button = (ImageButton) rootView.findViewById(R.id.browact_button);
        MainActivity.buttonEffect(browser_button);
        browser_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        //POST PLAN
        postplan_button = (ImageButton) rootView.findViewById(R.id.newact_button);
        MainActivity.buttonEffect(postplan_button);
        postplan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PostPlanActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), PlanActivity.class);
        intent.putExtra("plan",planList.get(position));
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.setAdapter(planList, planAdapter);
        mAsyncTask.execute();
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, List<Plan>> {

        //Params: User
        //Progress: Void. No utilizamos progress bar
        //Results: boolean
        private BaseAdapter planAdapter;
        private List<Plan> planList;

        public void MyAsyncTask(){
            planList = new ArrayList<>();
        }




        public void setAdapter(List<Plan> planList, BaseAdapter adapter){
            this.planList = planList;
            this.planAdapter = adapter;
        }

        @Override
        protected List<Plan> doInBackground(Void... parameter) {
            try {
                return Plan_restAPI.getAllPlans();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Plan> result) {
            int max;

            planList.clear();

            if(result!=null) {
                if (result.size() > 5)
                    max = 5;
                else
                    max = result.size();

                for (int i = 0; i < max; i++) {
                    planList.add(result.get(result.size() - 1 - i));
                }

                planAdapter.notifyDataSetChanged();
            }

            //Toast.makeText(context, "Last plans actualitzats!", Toast.LENGTH_SHORT).show();
        }
    }
}