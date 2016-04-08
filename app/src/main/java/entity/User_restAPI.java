package entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User_restAPI {

    private static final String WEBRESOURCES_URI = "http://192.168.1.124:8080/PlanOutServer/webresources/";

    public static void main(String[] args) throws ParseException, IOException {

        User user = new User();
        user.setGoogleid("user15");
        user.setMail("hola@gmail.com");
        user.setName("miquel marti rabadan");
        user.setUrl("https://scontent-mad1-1.xx.fbcdn.net/hphotos-xtf1/v/t1.0-9/11139424_10206930105879752_8676663704220302021_n.jpg?oh=2688788856f955f6c55ea1ac0e983261&oe=56352BD5");
        System.out.println(((Boolean) signIn(user)).toString());

//        user = retrieveUser("user2");
//        List<Plan> plans = getPlansSubscribedTo(user);
//        List<Plan> plans = getMyOwnedPlans(user);

//        System.out.println(plans.toString());

    }

    public static boolean signIn(User user) {
        if (!isUser(user)) {
            createUser(user);
            return true;
        } else {
            return false;
        }
    }

    private static void createUser(User user) {
        try {
            URL url = new URL(WEBRESOURCES_URI + "entity.user/");
            HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

            ucon.setRequestMethod("POST");
            ucon.setDoOutput(true);
            ucon.setRequestProperty("Content-Type", "application/json");
            ucon.setRequestProperty("Accept", "application/json");

            PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
            Gson gson = new Gson();
            String json = gson.toJson(user);
            out.println(json);
            out.flush();
            ucon.connect();

            ucon.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isUser(User user) {
        try {
            URL url = new URL(WEBRESOURCES_URI + "entity.user/");
            HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

            ucon.setRequestMethod("GET");
            ucon.setDoOutput(true);
            ucon.setRequestProperty("Content-Type", "application/json");
            ucon.setRequestProperty("Accept", "application/json");

            ucon.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
            Gson gson = new Gson();
            ArrayList<User> users = new ArrayList<>(Arrays.asList(gson.fromJson(in, User[].class)));
            return users.contains(user);

        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            return false;
        }

    }

    public static User retrieveUser(String id) throws MalformedURLException, IOException {

        URL url = new URL(WEBRESOURCES_URI + "entity.user/" + id);
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("GET");
        ucon.setDoInput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");
        ucon.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
        Gson gson = new Gson();
        User user = gson.fromJson(in, User.class);
        return user;
    }

    private static List<Subscription> getSubscriptionsByUser(User user) throws MalformedURLException, IOException {
        URL url = new URL(WEBRESOURCES_URI + "entity.subscription/entity.user/" + user.getGoogleid());
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

    public static List<Plan> getPlansSubscribedTo(User user) throws IOException {
        List<Subscription> subs = getSubscriptionsByUser(user);
        List<Plan> plans = new ArrayList<>();
        for (Subscription s : subs) {
            plans.add(s.getPlan());
        }
        return plans;
    }

    public static List<Plan> getMyOwnedPlans(User user) throws MalformedURLException, IOException {
        URL url = new URL(WEBRESOURCES_URI + "entity.plan/entity.user/" + user.getGoogleid());
        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

        ucon.setRequestMethod("GET");
        ucon.setDoInput(true);
        ucon.setRequestProperty("Content-Type", "application/json");
        ucon.setRequestProperty("Accept", "application/json");
        ucon.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").create();
        Plan[] plansArray = gson.fromJson(in, Plan[].class);
        List<Plan> plans = Arrays.asList(plansArray);
        return plans;
    }
}
