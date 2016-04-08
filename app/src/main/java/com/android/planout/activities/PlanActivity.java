package com.android.planout.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.planout.R;
import com.android.planout.view.NotifyingScrollView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import entity.Plan;
import entity.Plan_restAPI;
import entity.User;

public class PlanActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private GoogleMap map;
    private Drawable mActionBarBackgroundDrawable;
    private SimpleDateFormat dateFormatter;
    private Plan plan;
    private NotifyingScrollView mScrollView;
    private ArrayAdapter<String> userAdapter;
    private ArrayList<String> userNames;
    private ListView usersSubscribed;
    private int resId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ImageView logo = (ImageView) toolbar.findViewById(R.id.toolbar_logo);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        TextView subtitle = (TextView) toolbar.findViewById(R.id.toolbar_subtitle);
        toolbar.setTitle("");

        Bundle bundle = getIntent().getExtras();
        plan = (Plan) bundle.get("plan");


        if(plan.getIconId().equalsIgnoreCase("other")){
            switch(plan.getCategoryId()){
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
            resId = getResources().getIdentifier("icon_" + plan.getIconId().toLowerCase(), "drawable", getPackageName());
        }


        logo.setImageResource(resId);
        title.setText(plan.getTitle());
        subtitle.setText("created by " + plan.getOwner().getName());

        setSupportActionBar(toolbar);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        ((TextView) findViewById(R.id.planactivity_date)).setText(dateFormatter.format(plan.getDate().getTime()));
        ((TextView) findViewById(R.id.planactivity_description)).setText(plan.getDescription());

        mActionBarBackgroundDrawable = getResources().getDrawable(R.color.ColorPrimary);
        mActionBarBackgroundDrawable.setAlpha(0);

        getSupportActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScrollView = ((NotifyingScrollView) findViewById(R.id.scroll_view));
        mScrollView.setOnScrollChangedListener(mOnScrollChangedListener);

        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        LatLng ll = new LatLng(plan.getLatitude(),plan.getLongitude());

        map.addMarker(new MarkerOptions().position(ll).title("Hello"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        userNames = new ArrayList<String>();

        userAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userNames);
        usersSubscribed = (ListView) findViewById(R.id.people_coming);
        usersSubscribed.setAdapter(userAdapter);

        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.setAdapter(userNames, userAdapter);
        mAsyncTask.execute(plan);

        ImageButton join = (ImageButton) findViewById(R.id.button_join);
        MainActivity.buttonEffect(join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Join button clicked", Toast.LENGTH_SHORT).show();
                MySubscriptionTask mySubscriptionTask = new MySubscriptionTask();
                mySubscriptionTask.subscribeOrUnsubscribe(true);
                mySubscriptionTask.setContext(getApplicationContext());
                mySubscriptionTask.execute(plan);

                MyAsyncTask mAsyncTask = new MyAsyncTask();
                mAsyncTask.setAdapter(userNames, userAdapter);
                mAsyncTask.execute(plan);
            }
        });

        ImageButton unjoin = (ImageButton) findViewById(R.id.button_unjoin);
        MainActivity.buttonEffect(unjoin);
        unjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Unjoin button clicked", Toast.LENGTH_SHORT).show();
                MySubscriptionTask mySubscriptionTask = new MySubscriptionTask();
                mySubscriptionTask.subscribeOrUnsubscribe(false);
                mySubscriptionTask.setContext(getApplicationContext());
                mySubscriptionTask.execute(plan);

                MyAsyncTask mAsyncTask = new MyAsyncTask();
                mAsyncTask.setAdapter(userNames, userAdapter);
                mAsyncTask.execute(plan);
            }
        });
    }

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = findViewById(R.id.image_header).getHeight() - getSupportActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };

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
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class MySubscriptionTask extends AsyncTask<Plan, Void, Void> {

        //Params: User
        //Progress: Void. No utilizamos progress bar
        //Results: boolean
        private Context context;
        //true we will subscribe, false we will unsubscribe
        private boolean subscribe;

        public void MyAsyncTask(){
        }

        public void setContext(Context context){ this.context = context;}

        public void subscribeOrUnsubscribe(boolean subscribe){
            this.subscribe = subscribe;
        }

        @Override
        protected Void doInBackground(Plan... parameter) {
            try {
                if(subscribe)
                    Plan_restAPI.newSubscription(parameter[0], MainActivity.user);
                else
                    Plan_restAPI.removeSubscription(parameter[0], MainActivity.user);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(context, "User subscribed/unsubscribed to the plan!", Toast.LENGTH_SHORT).show();
        }
    }

    private static class MyAsyncTask extends AsyncTask<Plan, Void, List<User>> {

        //Params: User
        //Progress: Void. No utilizamos progress bar
        //Results: boolean
        private ArrayAdapter<String> adapter;
        private ArrayList<String> userNames;
        public void MyAsyncTask(){
        }

        public void setAdapter(ArrayList<String> userNames, ArrayAdapter<String> userAdapter){
            this.adapter = userAdapter;
            this.userNames = userNames;
        }

        @Override
        protected List<User> doInBackground(Plan... parameter) {
            try {
                return Plan_restAPI.getSubscribedUsers(parameter[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<User> result) {
            userNames.clear();

            if(result.size()!=0) {

                for (int i = 0; i < result.size(); i++) {
                    userNames.add(result.get(i).getName());
                }

            }
            adapter.notifyDataSetChanged();

        }
    }

}
