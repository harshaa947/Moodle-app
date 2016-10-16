package com.cop290.ashwattha.assn1.moodle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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


public class ThreadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Context mContext;
    NetworkOperations network ;
    Gson gson ;
    private ArrayList<Threadcomment> threadcomments;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    TextView thrd_name;
    TextView thrd_description;
    TextView thrd_created_at;
    TextView thrd_updated_at;

    public static class ImplementListener implements ResponseListener{
        WeakReference<ThreadFragment> activity;
        ImplementListener(ThreadFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            ThreadFragment  activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("courses/course.json/")&&URL.contains("grades"))
                    activity.updateLayout(json);
                else if(URL.contains("threads/post_comment.json"))
                    activity.checkpostsuccess(json);
            }
            catch(JSONException e){
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            ThreadFragment activity=this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }

    static class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
        private static final String TAG = "CommentAdapter";

        private ArrayList<Threadcomment> mDataSet;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */


        public static class ViewHolder extends RecyclerView.ViewHolder {

            /* String thread_comment ;
             TextView Thread_comment  ;
             thread_comment
             */
            public TextView notification;
            ///notification_list_recycler_view

            public ViewHolder(View v) {
                super(v);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getPosition() + " clicked.");
                    }
                });
                notification = (TextView) v.findViewById(R.id.textview_recycler_view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.textitem_recycler_view, viewGroup, false);

            return new ViewHolder(v);
        }

        CommentAdapter(ArrayList<Threadcomment> DataSet) {
            mDataSet = DataSet;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.notification.setText(mDataSet.get(i).description+"\n"+mDataSet.get(i).created_at);
            // check for commentsss

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }
    
    public ThreadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThreadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThreadFragment newInstance(String param1, String param2) {
        ThreadFragment fragment = new ThreadFragment();
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
        rootView = inflater.inflate(R.layout.fragment_thread, container, false);
        thrd_name = (TextView) rootView.findViewById(R.id.thrd_name);
        thrd_description = (TextView) rootView.findViewById(R.id.thrd_description);
        thrd_created_at = (TextView) rootView.findViewById(R.id.thrd_created_at);
        thrd_updated_at = (TextView) rootView.findViewById(R.id.thrd_updated_at);

        return rootView;
    }




    @Override
    public void onResume(){
        super.onResume();
        requestThread(getArguments().getString("threadid"));
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

    // request sent for thread detail
    public void  requestThread(String threadid){
        String url = "http://192.168.99.1:8000/threads/thread.json/"+threadid;

        if(network.NetworkCheck(mContext)){
            network.sendStringrequest(url,new ImplementListener(this),mContext.getApplicationContext());
        }
    }
    // comments sent for threads
    public void  commentThread(String threadid ,String description){
        String url = "http://192.168.99.1:8000/threads/post_comment.json?thread_id="+threadid+"&description="+description;

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
 // getting detail of threads
    public void updateLayout( JSONObject response){
        System.out.println(response.toString());
        try {
            if (response.getString("thread").equals("{}")){
                // no course thread
            }
            else {
                ThreadDetail threadDetail = gson.fromJson(response.toString(),ThreadDetail.class);
                Thread thread = threadDetail.thread;
                thrd_name.setText(thread.title);
                thrd_description.setText(thread.description);
                thrd_created_at.setText(thread.created_at);
                thrd_updated_at.setText(thread.updated_at);
                threadcomments = threadDetail.comments;
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.thread_comment_recycler_view) ;
                mLayoutManager = new LinearLayoutManager(getActivity());
                CommentAdapter adapter = new CommentAdapter(threadcomments);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
    // checks success after posting comments
    public void checkpostsuccess(JSONObject response){
        System.out.println(response.toString());
        try{
            if (response.getString("success").equals("true")){
                //post successful
            }
            else{

            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
}
