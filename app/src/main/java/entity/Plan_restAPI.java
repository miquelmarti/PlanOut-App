package entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Plan_restAPI {

    private static final String WEBRESOURCES_URI = "http://192.168.1.124:8080/PlanOutServer/webresources/";

    public static void main(String[] args) throws ParseException, IOException {

        User user = new User();
        user.setGoogleid("104093645481411876105");
//        user.setGoogleid("user2");        

        Plan plan = new Plan();
        plan.setId(26);// 
        plan.setTitle("Android project3");
        plan.setCategoryId(2);
        plan.setDescription("This is a BETTER plan! Let's have MORE fun!!!");
        plan.setIconId("cinema");
        plan.setOwner(user);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE);
        Date myDate = format.parse("2015-06-10T00:00:00");
        plan.setDate(myDate);
        plan.setLatitude(41.2454);
        plan.setLongitude(2.54844);

//        newPlan(plan);
        List<Plan> plans = getPlansById(user);
//        List<Plan> plans = getPlansByCategory(2);
//        List<Plan> plans = getAllPlans();
//        List<User> users = getSubscribedUsers(plan);
//        System.out.println(users.toString());
        System.out.println(plans.toString());
//        newSubscription(plan,user);
//        removeSubscription(plan, user);
    }

    public static void newPlan(Plan plan) throws MalformedURLException, IOException {

        URL url = new URL(WEBRESOURCES_URI + "entity.plan/");
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("POST");
//        ucon.setDoInput(true);
        ucon.setDoOutput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");
        ucon.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");

        PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").create();
        String json = gson.toJson(plan);
        System.out.println(json);
        out.println(json);
        out.flush();
        ucon.connect();

        ucon.getResponseCode();

    }

    public static List<Plan> getPlansById(User user) throws MalformedURLException, IOException {
        URL url = new URL(WEBRESOURCES_URI + "entity.plan/entity.user/" + user.getGoogleid());
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("GET");
        ucon.setDoInput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");
        ucon.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");

        ucon.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));

//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").create();

        Plan[] planArray = gson.fromJson(in, Plan[].class);
        List<Plan> plans = Arrays.asList(planArray);
//        for (Plan p : plans) {
//            p.setSubscriptionCollection(getSubscriptionsByPlan(p));
//        }
        return plans;

    }

    public static List<Plan> getPlansByCategory(Integer category) throws MalformedURLException, IOException {
        URL url = new URL(WEBRESOURCES_URI + "entity.plan/category/" + category);
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("GET");
        ucon.setDoInput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");
        ucon.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");

        ucon.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").create();

        Plan[] planArray = gson.fromJson(in, Plan[].class);
        List<Plan> plans = Arrays.asList(planArray);

        return plans;

    }

    public static List<Plan> getAllPlans() throws MalformedURLException, IOException {
        URL url = new URL(WEBRESOURCES_URI + "entity.plan");
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("GET");
        ucon.setDoInput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");
        ucon.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");

        ucon.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));

//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").create();

        Plan[] planArray = gson.fromJson(in, Plan[].class);
        List<Plan> plans = Arrays.asList(planArray);
        /*for (Plan p : plans) {
            p.setSubscriptionCollection(getSubscriptionsByPlan(p));
        }*/
        return plans;

    }

    private static List<Subscription> getSubscriptionsByPlan(Plan plan) throws MalformedURLException, IOException {
        URL url = new URL(WEBRESOURCES_URI + "entity.subscription/entity.plan/" + plan.getId());
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("GET");
        ucon.setDoInput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");
        ucon.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").create();
        Subscription[] subsArray = gson.fromJson(in, Subscription[].class);
        List<Subscription> subs = Arrays.asList(subsArray);
        return subs;
    }

    public static List<User> getSubscribedUsers(Plan plan) throws IOException {
        List<Subscription> subs = getSubscriptionsByPlan(plan);
        List<User> users = new ArrayList<>();
        for (Subscription s : subs) {
            users.add(s.getUser());
        }
        return users;
    }

    public static void newSubscription(Plan plan, User user) throws MalformedURLException, IOException {
        Subscription s = new Subscription();
        s.setPlan(plan);
        s.setUser(user);

        URL url = new URL(WEBRESOURCES_URI + "entity.subscription/");
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("POST");
        ucon.setDoOutput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");

        PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").create();
        String json = gson.toJson(s);
//        System.out.println(json);
        out.println(json);
        out.flush();
        ucon.connect();
        ucon.getResponseCode();

    }

    public static void removeSubscription(Plan plan, User user) throws MalformedURLException, IOException {

        URL url = new URL(WEBRESOURCES_URI + "entity.subscription/" + user.getGoogleid() + "/" + plan.getId());
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("DELETE");
        ucon.setDoInput(true);

        ucon.connect();

        ucon.getResponseCode();

    }

}
