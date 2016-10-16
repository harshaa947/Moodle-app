package com.cop290.ashwattha.assn1.moodle;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MyHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NetworkOperations network ;
    Gson gson ;
    User user;
    public static class ImplementListener implements ResponseListener{
        WeakReference<MyHome> activity;
        ImplementListener(MyHome activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            MyHome activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);


                if (activity != null && !activity.isFinishing()){
                if(URL.contains("logout.json"))
                    activity.logoutw();

                if(URL.contains("courses/list.json"))
                    activity.updateLayout(json);
            }}
            catch(JSONException e){
                if (activity != null && !activity.isFinishing())
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            MyHome activity=this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        network = new NetworkOperations();
        gson = new Gson();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        user = gson.fromJson(getIntent().getStringExtra("user"),User.class);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*TextView name = (TextView) findViewById(R.id.user_name_textView);
        name.setText(user.first_name+" "+user.last_name);
        TextView email = (TextView) findViewById(R.id.email_textView);
        email.setText(user.email);*/
    }

    @Override
    protected void onResume(){
        CourseListFragment courseListFragment = CourseListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_relativelayout, courseListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

                super.onBackPressed();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my__home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch(id){
            case R.id.nav_courses : CourseListFragment courseListFragment = CourseListFragment.newInstance();
                                    transaction.replace(R.id.main_relativelayout, courseListFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    break;
            case R.id.nav_grades :  AllGradesFragment allgradesfragment = AllGradesFragment.newInstance();
                                    transaction.replace(R.id.main_relativelayout, allgradesfragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    break;
            case R.id.nav_notifications:    NotificationsFragment notificationsfragment = NotificationsFragment.newInstance();
                                            transaction.replace(R.id.main_relativelayout, notificationsfragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                            break;
            case R.id.nav_logout :  logout();
                                    finish();
                                    break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void logout(){
        String url = "http://192.168.56.1:8000/default/logout.json";
        if(network.NetworkCheck(this)){
            network.sendStringrequest(url,new ImplementListener(this),getApplicationContext());
        }
        else{
            showSnackbar("Connect to the internet");
        }
    }
    void logoutw(){

        finish();

    }
    // request list of courses
    public void requestCourses(){

        String url = "http://192.168.56.1:8000/courses/list.json";
        if(network.NetworkCheck(this)){
            network.sendStringrequest(url,new ImplementListener(this),this.getApplicationContext());
        }
        else{
            showSnackbar("Connect to the internet");
        }
    }
    // updates layout after getting course details
    public void updateLayout( JSONObject response){
        System.out.println(response.toString());
        try{
            if (response.getString("courses").equals("[]")){

            }
            else {
                Courses courselist=gson.fromJson(response.toString(),Courses.class);
            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
    public void raiseAlert(String Title, String Description){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(Description);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showSnackbar(String s){
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void callCourseFragment(String course_code){
        Bundle args = new Bundle();
        args.putString("course_code", course_code);
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_relativelayout, courseFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
