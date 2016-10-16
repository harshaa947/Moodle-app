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


public class GradesFragment extends Fragment {
    
    private Context mContext;
    NetworkOperations network ;
    Gson gson ;
    private ArrayList<Grade> Grades;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    public static class ImplementListener implements ResponseListener{
        WeakReference<GradesFragment> activity;
        ImplementListener(GradesFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            GradesFragment  activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("courses/course.json/")&&URL.contains("grades"))
                    activity.updateLayout(json);
            }
            catch(JSONException e){
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            GradesFragment activity=this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }
    // TODO: Rename and change types of parameters

    static class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.ViewHolder> {
        private static final String TAG = "GradesAdapter";

        private ArrayList<Grade> mDataSet;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */


        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView grad_name;
            TextView grad_weightage;
            TextView grad_absolute;
            TextView grad_score;
            public ViewHolder(View v) {
                super(v);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getPosition() + " clicked.");
                    }
                });
                CardView all_grades_card = (CardView) v.findViewById(R.id.grade_card);
                grad_name = (TextView) all_grades_card.findViewById(R.id.grad_name);
                grad_score = (TextView) all_grades_card.findViewById(R.id.grad_score);
                grad_weightage= (TextView)all_grades_card.findViewById(R.id.grad_weightage);
                grad_absolute = (TextView) all_grades_card.findViewById(R.id.grad_absolute);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grade_card, viewGroup, false);

            return new ViewHolder(v);
        }

        GradesAdapter(ArrayList<Grade> DataSet) {
            mDataSet = DataSet;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.grad_name.setText("\t"+mDataSet.get(i).name);
            viewHolder.grad_score.setText(Html.fromHtml("<b>Score : </b> <em>" + mDataSet.get(i).score + "/" + mDataSet.get(i).out_of) +"");
            viewHolder.grad_weightage.setText(Html.fromHtml("<b>Weightage : </b>" + mDataSet.get(i).weightage));
            float absolute = Float.parseFloat(mDataSet.get(i).score)*Float.parseFloat(mDataSet.get(i).weightage)/Float.parseFloat(mDataSet.get(i).out_of);
            viewHolder.grad_absolute.setText(Html.fromHtml("<b> Absolute : </b>" + Float.toString(absolute)));
            // check for commentsss

        }
        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }
    
    public GradesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GradesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradesFragment newInstance(String param1, String param2) {
        GradesFragment fragment = new GradesFragment();
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
             requestCourseGrade(getArguments().getString("course_code"));
        }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_grades, container, false);
        return rootView;
    }




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
    // function for requesting grades of a particular course
    public void  requestCourseGrade(String coursecode){
        String url = "http://192.168.56.1:8000/courses/course.json/"+coursecode+"/grades";

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
            if (response.getString("grades").equals("[]")){
                // no course grade
            }
            else {
                GradeCourse gradeCourse = gson.fromJson(response.toString(),GradeCourse.class);
                Grades = gradeCourse.grades;
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.grades_recycler_view) ;
                mLayoutManager = new LinearLayoutManager(getActivity());
                GradesAdapter adapter = new GradesAdapter(Grades);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
}
