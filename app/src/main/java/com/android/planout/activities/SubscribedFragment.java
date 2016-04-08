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
import android.widget.ListView;
import android.widget.TextView;

import com.android.planout.R;
import com.android.planout.adapters.PlansAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entity.Plan;
import entity.User_restAPI;

public class SubscribedFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView planListView;
    private BaseAdapter planAdapter;
    private ArrayList<Plan> planList;
    private TextView noSubscriptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscribed, container, false);

        planList = new ArrayList<Plan>();

        planAdapter = new PlansAdapter(rootView.getContext(), planList);
        planListView = (ListView) rootView.findViewById(R.id.subscribed_listview);
        planListView.setAdapter(planAdapter);
        planListView.setOnItemClickListener(this);

        noSubscriptions = (TextView) rootView.findViewById(R.id.subscribed_text);

        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.setAdapter(planList, planAdapter);
        mAsyncTask.setTextView(noSubscriptions);
        mAsyncTask.execute();

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), PlanActivity.class);
        intent.putExtra("plan", planList.get(position));
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.setAdapter(planList, planAdapter);
        mAsyncTask.setTextView(noSubscriptions);
        mAsyncTask.execute();
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, List<Plan>> {

        //Params: User
        //Progress: Void. No utilizamos progress bar
        //Results: boolean
        private List<Plan> planList;
        private BaseAdapter planAdapter;
        private TextView noSubscriptions;

        public void MyAsyncTask(){
            planList = new ArrayList<>();
        }

        public void setTextView(TextView textview){
            this.noSubscriptions = textview;
        }

        public void setAdapter(List<Plan> planList, BaseAdapter adapter){
            this.planList = planList;
            planAdapter = adapter;
        }

        @Override
        protected List<Plan> doInBackground(Void... parameter) {
            try {
                return User_restAPI.getPlansSubscribedTo(MainActivity.user);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Plan> result) {

            planList.clear();

            if(result!=null){
                if(result.size()!=0) {
                    noSubscriptions.setVisibility(View.GONE);

                    for (int i = 0; i < result.size(); i++) {
                        planList.add(result.get(result.size() - 1 - i));
                    }
                    planAdapter.notifyDataSetChanged();
                }else
                    noSubscriptions.setVisibility(View.VISIBLE);
            }else{
                noSubscriptions.setVisibility(View.VISIBLE);
            }

            //Toast.makeText(context, "Last plans actualitzats!", Toast.LENGTH_SHORT).show();
        }
    }
}
