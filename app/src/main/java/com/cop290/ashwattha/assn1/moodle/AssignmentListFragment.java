package com.cop290.ashwattha.assn1.moodle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class AssignmentListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    NetworkOperations network ;
    Gson gson ;
    private ArrayList<Assignment> Assignments;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    
    public static class ImplementListener implements ResponseListener{
        WeakReference<AssignmentListFragment> activity;
        ImplementListener(AssignmentListFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            AssignmentListFragment  activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("courses/course.json/")&&URL.contains("assignments"))
                    activity.updateLayout(json);
            }
            catch(JSONException e){
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            AssignmentListFragment activity=this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }

    static class AssignmentListAdapter extends RecyclerView.Adapter<AssignmentListAdapter.ViewHolder> {
        private static final String TAG = "AssignmentListAdapter";

        private ArrayList<Assignment> mDataSet;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */


        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView assignment_name;
            TextView assignment_deadline;

            public ViewHolder(View v) {
                super(v);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getPosition() + " clicked.");
                    }
                });
                CardView assignment_card = (CardView) v.findViewById(R.id.assignment_card);
                assignment_name = (TextView) assignment_card.findViewById(R.id.assignment_name_card);
                assignment_deadline = (TextView) assignment_card.findViewById(R.id.assignment_deadline_card);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.assignment_card, viewGroup, false);

            return new ViewHolder(v);
        }

        AssignmentListAdapter(ArrayList<Assignment> DataSet) {
            mDataSet = DataSet;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


         @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.assignment_name.setText(Html.fromHtml("<em> \t" + mDataSet.get(i).name + "</em>"));
            viewHolder.assignment_deadline.setText(Html.fromHtml("<b>Deadline : </b>"+mDataSet.get(i).deadline));
            // check for commentsss

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }
    
    public AssignmentListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignmentListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssignmentListFragment newInstance(String param1, String param2) {
        AssignmentListFragment fragment = new AssignmentListFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_assignment_list, container, false);
        return rootView;
    }
    @Override
    public void onResume(){
        super.onResume();
        requestAssignment(getArguments().getString("course_code"));
    }
    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.*/


    // request assignment for course
    public void  requestAssignment(String coursecode){
        String url = "http://192.168.56.1:8000/courses/course.json/"+coursecode+"/assignments";

        if(network.NetworkCheck(mContext)){
            network.sendStringrequest(url,new ImplementListener(this),mContext.getApplicationContext());
        }
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
    public void updateLayout( JSONObject response){
        System.out.println(response.toString());
        try {
            if (response.getString("assignments").equals("[]")){
                // no assignment detail
            }
            else {
                Assignmentcourse assignmentcourse = gson.fromJson(response.toString(),Assignmentcourse.class);
                Assignments = assignmentcourse.assignments;
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.assignment_list_recycler_view) ;
                mLayoutManager = new LinearLayoutManager(getActivity());
                AssignmentListAdapter adapter = new AssignmentListAdapter(Assignments);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
}
