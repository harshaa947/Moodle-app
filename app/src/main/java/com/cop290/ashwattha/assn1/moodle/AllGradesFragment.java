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


public class AllGradesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Context mContext;
    // TODO: Rename and change types of parameters
    NetworkOperations network ;
    Gson gson ;

    //ArrayList of grades(Type) is used to show all the grades
    // to the user. It is done by using Recycler view;

    private ArrayList<Grade> Grades;
    protected RecyclerView mRecyclerView;       //a recylecveiw object
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    // Implement listener implementation for this class
    public static class ImplementListener implements ResponseListener{
        WeakReference<AllGradesFragment> activity;
        ImplementListener(AllGradesFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            AllGradesFragment  activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("default/grades.json"))
                    activity.updateLayout(json);
            }
            catch(JSONException e){
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            AllGradesFragment activity=this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }

    //Adapter for this recycler view is AllGradesAdapter which
    //creates the Textview and put the respective Element
    // of grade Obect in it.

    static class AllGradesAdapter extends RecyclerView.Adapter<AllGradesAdapter.ViewHolder> {
        private static final String TAG = "AllGradesAdapter";

        private ArrayList<Grade> mDataSet;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */


        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView grad_name;
            TextView grad_weightage;
            TextView grad_absolute;
            TextView grad_score;
            TextView grad_course;
            public ViewHolder(View v) {
                super(v);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getPosition() + " clicked.");
                    }
                });

                //Cards of grades are use to show the user the Grades of Courses
                CardView all_grades_card = (CardView) v.findViewById(R.id.all_grades_card);
                grad_course = (TextView) all_grades_card.findViewById(R.id.grad_course);
                grad_name = (TextView) all_grades_card.findViewById(R.id.grad_name);
                grad_score = (TextView) all_grades_card.findViewById(R.id.grad_score);
                grad_weightage= (TextView)all_grades_card.findViewById(R.id.grad_weightage);
                grad_absolute = (TextView) all_grades_card.findViewById(R.id.grad_absolute);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.all_grades_card, viewGroup, false);

            return new ViewHolder(v);
        }

        AllGradesAdapter(ArrayList<Grade> DataSet) {
            mDataSet = DataSet;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
        // OnBindVeiwholder Sets the required text in the plces assigned in AllGradesadapter
        //function.
   @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            viewHolder.grad_name.setText(Html.fromHtml("<b>"+"\t"+mDataSet.get(i).name+"</b>"));
            viewHolder.grad_score.setText(Html.fromHtml("<b>Score: </b>"+mDataSet.get(i).score+"/"+mDataSet.get(i).out_of));
            viewHolder.grad_weightage.setText(Html.fromHtml("<b>Weightage :</b>" + mDataSet.get(i).weightage));
            float absolute = Float.parseFloat(mDataSet.get(i).score)*Float.parseFloat(mDataSet.get(i).weightage)/Float.parseFloat(mDataSet.get(i).out_of);

            viewHolder.grad_absolute.setText(Html.fromHtml("<b>Absolute : </b>" + Float.toString(absolute)));
            viewHolder.grad_course.setText("\t"+mDataSet.get(i).registered_course_id);
            // check for commentsss

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

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
    //Request for the Grades
    @Override
    public void onResume(){
    super.onResume();
      requestGrades();
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

    public AllGradesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AllGradesFragment newInstance() {
        AllGradesFragment fragment = new AllGradesFragment();
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
        rootView = inflater.inflate(R.layout.fragment_all_grades, container, false);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    // function called for requesting grades
    public void  requestGrades(){
        String url = "http://192.168.56.1:8000/default/grades.json";

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
    // update layout after request
    public void updateLayout( JSONObject response){
        System.out.println(response.toString());
        try {
            if (response.getString("grades").equals("[]")){
              // No grades
            }
            else {
                AllGrades allGrades = gson.fromJson(response.toString(),AllGrades.class);
                Grades = allGrades.grades;
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_grades_recycler_view) ;
                mLayoutManager = new LinearLayoutManager(getActivity());
                AllGradesAdapter adapter = new AllGradesAdapter(Grades);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
    }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
   }
}
