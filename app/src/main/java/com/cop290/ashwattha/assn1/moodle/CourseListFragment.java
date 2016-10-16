package com.cop290.ashwattha.assn1.moodle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;



public class CourseListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Context mContext;
    // TODO: Rename and change types of parameters
    NetworkOperations network ;
    Gson gson ;
    private ArrayList<Course> courses;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    
    public static class ImplementListener implements ResponseListener{
        WeakReference<CourseListFragment> activity;
        ImplementListener(CourseListFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
           CourseListFragment  activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("courses/list.json"))
                    activity.updateLayout(json);
            }
            catch(JSONException e){
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            CourseListFragment activity=this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }

    static class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {
        private static final String TAG = "CourseListAdapter";

        private ArrayList<Course> mDataSet;
        private MyHome mActivity;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */


        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView course_code_card;
            TextView course_name_card;
            TextView course_credits_card;
            private ArrayList<Course> mDataSet;
            private MyHome mActivity;
            public ViewHolder(View v,ArrayList<Course> DataSet,MyHome activity) {
                super(v);
                mDataSet = DataSet;
                mActivity = activity;
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String course_code = mDataSet.get(getPosition()).code;
                        mActivity.callCourseFragment(course_code);
                    }
                });
                CardView course_list = (CardView) v.findViewById(R.id.course_card);
                course_code_card = (TextView) course_list.findViewById(R.id.course_code_card);
                course_name_card = (TextView) course_list.findViewById(R.id.course_name_card);
                course_credits_card = (TextView) course_list.findViewById(R.id.course_credits_card);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.course_card, viewGroup, false);

            return new ViewHolder(v,mDataSet,mActivity);
        }

        CourseListAdapter(ArrayList<Course> DataSet,MyHome activity) {
            mDataSet = DataSet;
            mActivity = activity;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.course_name_card.setText(mDataSet.get(i).name);
            viewHolder.course_code_card.setText(mDataSet.get(i).code.toUpperCase());
            viewHolder.course_credits_card.setText(Html.fromHtml("<b> Credits: </b>"+mDataSet.get(i).credits +"\t \t"+"<b>" +
                    "l-t-p : </b>" + mDataSet.get(i).l_t_p ));
            // check for commentsss

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }
    
    
    
    public void updateLayout( JSONObject response){
        System.out.println(response.toString());
        try{
            if (response.getString("courses").equals("[]")){

            }
            else {
                Courses courselist=gson.fromJson(response.toString(),Courses.class);
                courses = courselist.courses;
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.course_list_recycler_view) ;
                mLayoutManager = new LinearLayoutManager(getActivity());
                CourseListAdapter adapter = new CourseListAdapter(courses,(MyHome)getActivity());
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
        }
        catch(Exception e){
                 raiseAlert("Oops", "Server behaved unusually");
        }
    }
    //function for requesting  courselist
    public void requestCourses(){
            String url = "http://192.168.56.1:8000/courses/list.json";
        if(network.NetworkCheck(mContext)){
            network.sendStringrequest(url,new ImplementListener(this),mContext.getApplicationContext());
        }
        else{
            showSnackbar("Connect to the internet");
        }
    }

    public CourseListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CourseListFragment newInstance() {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        network = new NetworkOperations();
        gson = new Gson();
        mContext = this.getActivity();
       
    }
    
        @Override
        public void onResume(){
            super.onResume();
             requestCourses();
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_course_list, container, false);
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

    
    public void raiseAlert(String Title, String Description){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(Description);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showSnackbar(String s){

    }

}
