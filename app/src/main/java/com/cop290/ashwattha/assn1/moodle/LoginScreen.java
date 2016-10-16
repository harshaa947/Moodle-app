package com.cop290.ashwattha.assn1.moodle;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class LoginScreen extends AppCompatActivity {
    NetworkOperations network;


    public static class ImplementListener implements ResponseListener{
        WeakReference<LoginScreen> activity;
        ImplementListener(LoginScreen activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            LoginScreen activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("login.json"))
                    activity.userauthentication(json);

            }
            catch(JSONException e){
               activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            LoginScreen activity=this.activity.get();

            activity.raiseAlert("Oops1", "Server behaved unusually");

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__screen);
        network = new NetworkOperations();
        FloatingActionButton fab_login = (FloatingActionButton) findViewById(R.id.fab_login);
        fab_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }

        });

        TextView login_screen_Text1 = (TextView)findViewById(R.id.login_screen_Text1);
        TextView login_screen_Text2 = (TextView)findViewById(R.id.login_screen_Text2);
        TextView login_screen_Text3 = (TextView)findViewById(R.id.login_screen_Text3);
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "font/the-unseen.ttf");
        login_screen_Text1.setTypeface(typeface1);
        login_screen_Text2.setTypeface(Typeface.createFromAsset(getAssets(), "font/Charcoal.otf"));
        login_screen_Text3.setTypeface(typeface1);
    }

    // function for authentication of user (check response )
    public void userauthentication(JSONObject response){
        try {
            if (response.getString("success").equals("true")){
                Intent intent = new Intent(LoginScreen.this, MyHome.class);
                intent.putExtra("user",response.getString("user"));
                startActivity(intent);

            }
            else{
              showSnackbar("Invalid Password or username");
            }
        } catch (Exception e) {
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
    // function request for login
    public void login(){

       EditText username =(EditText) findViewById(R.id.Username_EditText);
       EditText password = (EditText) findViewById(R.id.Password_EditText);
        String url = "http://192.168.56.1:8000/default/login.json?userid=";
        url+= username.getText() + "&password=" + password.getText();
        if(network.NetworkCheck(this)){
            network.sendStringrequest(url,new ImplementListener(this),getApplicationContext());
        }
        else{
         showSnackbar("Connect to the internet");
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



}
