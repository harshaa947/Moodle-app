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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Context mContext;
    // TODO: Rename and change types of parameters
    NetworkOperations network ;
    Gson gson ;
    private ArrayList<Notification> notifications;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;

    public static class ImplementListener implements ResponseListener {
        WeakReference<NotificationsFragment> activity;

        ImplementListener(NotificationsFragment activity) {
            this.activity = new WeakReference(activity);
        }

        @Override
        public void onSuccess(String response, String URL) {
            NotificationsFragment activity = this.activity.get();
            JSONObject json;
            try {
                json = new JSONObject(response);
                if (URL.contains("default/notifications.json"))
                    activity.updateLayout(json);
            } catch (JSONException e) {
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error) {
            NotificationsFragment activity = this.activity.get();

            activity.raiseAlert("Oops", "Server behaved unusually. Unable to process request");

        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public NotificationsFragment() {
        // Required empty public constructor
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
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        // rootView.setTag(TAG);
        return rootView;
    }

    static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        private static final String TAG = "NotificationAdapter";

        private ArrayList<Notification> mDataSet;

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

        NotificationAdapter(ArrayList<Notification> DataSet) {
            mDataSet = DataSet;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.notification.setText(Html.fromHtml(mDataSet.get(i).description));
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
    @Override
        public void onResume(){
            super.onResume();
             requestNotification();
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

    // requesting notifications
    public void  requestNotification(){
        String url = "http://192.168.56.1:8000/default/notifications.json";

        if(network.NetworkCheck(mContext)){
            network.sendStringrequest(url,new ImplementListener(this),mContext.getApplicationContext());
        }
    }
    // raise alert
    public void raiseAlert(String Title, String Description){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(Description);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // function to show snackbar
    public void showSnackbar(String s){

    }

    // updates layout after getting notifications
    public void updateLayout( JSONObject response){
        System.out.println(response.toString());
        try {
            if (response.getString("notifications").equals("[]")){
                // no new notification
            }
            else {
                Notifications notificationsObject = gson.fromJson(response.toString(),Notifications.class);
                notifications = notificationsObject.notifications;
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.notification_list_recycler_view) ;
                mLayoutManager = new LinearLayoutManager(getActivity());
                NotificationAdapter adapter = new NotificationAdapter(notifications);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
        }
        catch(Exception e){
            raiseAlert("Oops", "Server behaved unusually");
        }
    }
}
