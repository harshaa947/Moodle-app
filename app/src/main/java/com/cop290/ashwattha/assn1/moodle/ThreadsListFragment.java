package com.cop290.ashwattha.assn1.moodle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
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



public class ThreadsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Context mContext;
    NetworkOperations network ;
    Gson gson ;
    private ArrayList<Thread> Threads;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    protected TextView thread_title;
    protected TextView thread_description;
    protected AppCompatImageButton add_button;

    public static class ImplementListener implements ResponseListener{
        WeakReference<ThreadsListFragment> activity;
        ImplementListener(ThreadsListFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            ThreadsListFragment  activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("courses/course.json/")&&URL.contains("threads"))
                    activity.updateLayout(json);
                else if(URL.contains("threads/new.json"))
                    activity.checkpostsuccess(json);
            }
            catch(JSONException e){
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            ThreadsListFragment activity=this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }

    static class ThreadsListAdapter extends RecyclerView.Adapter<ThreadsListAdapter.ViewHolder> {
        private static final String TAG = "ThreadsListAdapter";

        private ArrayList<Thread> mDataSet;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */


        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView thread_name;
            TextView thread_created_at;

            public ViewHolder(View v) {
                super(v);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getPosition() + " clicked.");
                    }
                });
                CardView thread_card = (CardView) v.findViewById(R.id.thread_card);
                thread_name = (TextView) thread_card.findViewById(R.id.thread_name_card);
                thread_created_at = (TextView) thread_card.findViewById(R.id.thread_created_at_card);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.thread_card, viewGroup, false);

            return new ViewHolder(v);
        }

        ThreadsListAdapter(ArrayList<Thread> DataSet) {
            mDataSet = DataSet;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.thread_name.setText(Html.fromHtml("<b> Title: </b>\t" + mDataSet.get(i).title));
            viewHolder.thread_created_at.setText(Html.fromHtml("<i>Created At :</i>"+mDataSet.get(i).created_at));
            // check for commentsss

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }


    public ThreadsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThreadsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThreadsListFragment newInstance(String param1, String param2) {
        ThreadsListFragment fragment = new ThreadsListFragment();
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
        rootView = inflater.inflate(R.layout.fragment_threads_list, container, false);
        thread_title = (TextView)rootView.findViewById(R.id.add_thread_title);
        thread_description = (TextView)rootView.findViewById(R.id.add_thread_description);
        add_button = (AppCompatImageButton) rootView.findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postThread(thread_title.getText().toString(), thread_description.getText().toString(), getArguments().getString("course_code"));
                thread_title.setText("");
                thread_description.setText("");
                requestCourseThread(getArguments().getString("course_code"));

            }

        });
        return rootView;
        
    }

     @Override 
     public void onResume(){
     super.onResume();
     requestCourseThread(getArguments().getString("course_code"));
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


  // requesting list of threads
    public void  requestCourseThread(String coursecode){
        String url = "http://192.168.56.1:8000/courses/course.json/"+coursecode+"/threads";

        if(network.NetworkCheck(mContext)){
            network.sendStringrequest(url,new ImplementListener(this),mContext.getApplicationContext());
        }
    }
    // posting thread
    public void postThread(String title , String description , String coursecode){
        String url = "http://192.168.56.1:8000/threads/new.json?title="+title+"&description="+description+"&course_code="+coursecode;
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

    // updating layout after getting list for threads
    public void updateLayout( JSONObject response){
        System.out.println(response.toString());
        try {
            if (response.getString("course_threads").equals("[]")){
                // no course thread
            }
            else {
                Threadcourse threadcourse = gson.fromJson(response.toString(),Threadcourse.class);
                Threads = threadcourse.course_threads;
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.threads_list_recycler_view) ;
                mLayoutManager = new LinearLayoutManager(getActivity());
                ThreadsListAdapter adapter = new ThreadsListAdapter(Threads);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            
            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }

    public void checkpostsuccess(JSONObject response){
        System.out.println(response.toString());
        try{
            if (response.getString("success").equals("true")){
                //post successful
                String threadid = response.getString("thread_id");

            }
            else{

            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
}
