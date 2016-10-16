package com.cop290.ashwattha.assn1.moodle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;



public class CourseOverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Course course;
    private Context mContext;
    NetworkOperations network;
    Gson gson;
    protected View rootView;
    TextView crse_name;


    public static class ImplementListener implements ResponseListener {
        WeakReference<CourseOverviewFragment> activity;

        ImplementListener(CourseOverviewFragment activity) {
            this.activity = new WeakReference(activity);
        }

        @Override
        public void onSuccess(String response, String URL) {
            CourseOverviewFragment activity = this.activity.get();
            JSONObject json;
            try {
                json = new JSONObject(response);
                if (URL.contains("courses/course.json/"))
                    activity.updateLayout(json);
            } catch (JSONException e) {
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            CourseOverviewFragment activity = this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }


    public CourseOverviewFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CourseOverviewFragment newInstance(String param1, String param2) {
        CourseOverviewFragment fragment = new CourseOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_course_overview, container, false);

        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
     @Override
    public void onResume(){
    super.onResume();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
  // function for requesting course detail
    public void requestCourse(String coursecode) {
        String url = "http://192.168.56.1:8000/courses/course.json/" + coursecode;

        if (network.NetworkCheck(mContext)) {
            network.sendStringrequest(url, new ImplementListener(this), mContext.getApplicationContext());
        }
    }

    public void raiseAlert(String Title, String Description) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(Description);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showSnackbar(String s) {

    }

    public void updateLayout(JSONObject response) {
        System.out.println(response.toString());
        try {
            if (response.getString("course").equals("{}")) {
                // no course detail
            } else {
                course = gson.fromJson(response.toString(), Course.class);

            }
        } catch (Exception e) {
            raiseAlert("Oops", "Server behaved unusually");
        }
    }

}
