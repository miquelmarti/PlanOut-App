package com.android.planout.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.planout.R;
import com.android.planout.adapters.IconAdapter;
import com.android.planout.view.NotifyingScrollView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import entity.Plan;
import entity.Plan_restAPI;

public class PostPlanActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Drawable mActionBarBackgroundDrawable;
    ImageButton postbutton;
    ArrayList<String> categoryList, topicList;
    ArrayList<Drawable> categoryDrawable, topicDrawable;
    BaseAdapter categoryAdapter, topicAdapter;
    Spinner categorySpinner, topicSpinner;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    TextView latitude, longitude;
    private Button locationButton;
    private DatePickerDialog dateDialog;
    private SimpleDateFormat dateFormatter;
    private NotifyingScrollView mScrollView;
    private MarkerOptions markerOptions;
    private LatLng latLng;
    private Plan plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_plan);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mActionBarBackgroundDrawable = getResources().getDrawable(R.color.ColorPrimary);
        mActionBarBackgroundDrawable.setAlpha(0);

        getSupportActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScrollView = ((NotifyingScrollView) findViewById(R.id.scroll_view));
        mScrollView.setOnScrollChangedListener(mOnScrollChangedListener);

        postbutton = (ImageButton) findViewById(R.id.post_button);
        MainActivity.buttonEffect(postbutton);
        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Post Plan!", Toast.LENGTH_SHORT).show();

                plan.setTitle(((EditText) findViewById(R.id.post_title)).getText().toString());
                plan.setDescription(((EditText) findViewById(R.id.post_description)).getText().toString());
                plan.setOwner(MainActivity.user);
                MyAsyncTask mAsyncTask = new MyAsyncTask();
                mAsyncTask.setContext(getApplicationContext());
                mAsyncTask.execute(plan);
                finish();
            }
        });

        initCategorySpinner();
        inittopicSpinner();

        plan = new Plan();

        //DatePicker
        final EditText editDate = (EditText) findViewById(R.id.edit_date);
        editDate.setInputType(InputType.TYPE_NULL);
        editDate.requestFocus();
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show();
            }
        });

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        Calendar newCalendar = Calendar.getInstance();
        dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editDate.setText(dateFormatter.format(newDate.getTime()));
                plan.setDate(newDate.getTime());
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();


        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);

        locationButton = (Button) findViewById(R.id.locationButton);
        MainActivity.buttonEffect(locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Getting location!", Toast.LENGTH_SHORT).show();
                if(latLng== null){
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        latitude.setText("Latitude: " + String.valueOf(mLastLocation.getLatitude()));
                        longitude.setText("Longitude: " + String.valueOf(mLastLocation.getLongitude()));
                        plan.setLatitude(mLastLocation.getLatitude());
                        plan.setLongitude(mLastLocation.getLongitude());
                    }
                }
                else{
                    latitude.setText("Latitude: " + String.valueOf(latLng.latitude));
                    longitude.setText("Longitude: " + String.valueOf(latLng.longitude));
                    plan.setLatitude(latLng.latitude);
                    plan.setLongitude(latLng.longitude);
                }
            }
        });

        // Getting reference to btn_find of the layout activity_main
        Button btn_find = (Button) findViewById(R.id.btn_find);

        // Defining button click event listener for the find button
        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location
                EditText etLocation = (EditText) findViewById(R.id.et_location);

                // Getting user input location
                String location = etLocation.getText().toString();

                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }

                hideKeyboard();
            }
        };

        // Setting button click event listener for the find button
        btn_find.setOnClickListener(findClickListener);

    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 1);
            } catch (Exception e) {
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }else {

                // Clears all the existing markers on the map
                map.clear();


                Address address = (Address) addresses.get(0);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                map.addMarker(markerOptions);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        }
    }

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = findViewById(R.id.image_header).getHeight() - getSupportActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };

    public void initCategorySpinner(){
        categoryList = new ArrayList<String>();
        categoryDrawable = new ArrayList<Drawable>();

        categoryList.add("Sports");
        categoryList.add("Food");
        categoryList.add("Shows");
        categoryList.add("Party");
        categoryList.add("Music");

        categoryDrawable.add(getResources().getDrawable(R.drawable.icon_sports));
        categoryDrawable.add(getResources().getDrawable(R.drawable.icon_food));
        categoryDrawable.add(getResources().getDrawable(R.drawable.icon_shows));
        categoryDrawable.add(getResources().getDrawable(R.drawable.icon_party));
        categoryDrawable.add(getResources().getDrawable(R.drawable.icon_music));

        categoryAdapter = new IconAdapter(this, categoryList, categoryDrawable);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Category selected: " + categoryList.get(position), Toast.LENGTH_SHORT).show();
                updatetopicSpinner(categoryList.get(position));
                plan.setCategoryId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //...
            }
        });
    }

    public void inittopicSpinner(){
        topicList = new ArrayList<String>();
        topicDrawable = new ArrayList<Drawable>();
        topicList.add("Café");
        topicDrawable.add(getResources().getDrawable(R.drawable.icon_cafe));
        topicList.add("Picnic");
        topicDrawable.add(getResources().getDrawable(R.drawable.icon_picnic));
        topicList.add("Restaurant");
        topicDrawable.add(getResources().getDrawable(R.drawable.icon_restaurant));
        topicList.add("Other");
        topicDrawable.add(getResources().getDrawable(R.drawable.icon_food));

        topicAdapter = new IconAdapter(this, topicList, topicDrawable);
        topicSpinner = (Spinner) findViewById(R.id.topicplan_spinner);
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "topic selected: " + topicList.get(position), Toast.LENGTH_SHORT).show();
                plan.setIconId(topicList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void updatetopicSpinner(String category){

        topicList.clear();
        topicDrawable.clear();

        if(category.equalsIgnoreCase("Music")){
            topicList.add("Classic");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_classic));
            topicList.add("Pop");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_pop));
            topicList.add("Rock");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_rock));
            topicList.add("Other");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_music));
        }else if(category.equalsIgnoreCase("Party")) {
            topicList.add("Anniversary");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_anniversary));
            topicList.add("Costumes");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_costumes));
            topicList.add("Drink");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_drink));
            topicList.add("Other");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_party));
        }else if(category.equalsIgnoreCase("Food")){
            topicList.add("Café");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_cafe));
            topicList.add("Picnic");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_picnic));
            topicList.add("Restaurant");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_restaurant));
            topicList.add("Other");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_food));
        }else if (category.equalsIgnoreCase("Shows")) {
            topicList.add("Cinema");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_cinema));
            topicList.add("Comedy");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_comedy));
            topicList.add("Museum");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_museum));
            topicList.add("Theatre");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_teathre));
            topicList.add("Other");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_shows));
        }else if(category.equalsIgnoreCase("Sports")){
            topicList.add("Basket");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_basket));
            topicList.add("Cycling");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_cycling));
            topicList.add("Football");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_football));
            topicList.add("Running");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_running));
            topicList.add("Tennis");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_tennis));
            topicList.add("Other");
            topicDrawable.add(getResources().getDrawable(R.drawable.icon_sports));
        }

        topicAdapter.notifyDataSetChanged();
    }

    private static class MyAsyncTask extends AsyncTask<Plan, Void, Void> {

        //Params: User
        //Progress: Void. No utilizamos progress bar
        //Results: boolean
        private Context context;


        public void MyAsyncTask() {

        }

        public void setContext(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(Plan... parameter) {
            try {
                Plan_restAPI.newPlan(parameter[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(context, "Plan created", Toast.LENGTH_SHORT).show();
        }
    }
}
