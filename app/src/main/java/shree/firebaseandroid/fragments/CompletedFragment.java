package shree.firebaseandroid.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.Login;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.adapter.ActivitiesListAdapter;
import shree.firebaseandroid.adapter.CompletedTaskAdapter;
import shree.firebaseandroid.adapter.TaskAdapter;
import shree.firebaseandroid.utils.ProjectManager;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedFragment extends Fragment {

    public static ListView tasklist;
    View view;
    SessionManager sessionManager;
    ProjectManager projectsession;

    ProgressDialog pd;
    public static ArrayList<CompletedFragment.AllTasks> allTasks = new ArrayList<CompletedFragment.AllTasks>();
    public static CompletedFragment.AllTasks taskdetails;
    JSONArray results = null;
    String subActivityname, description,status, startdate;
    public static CompletedTaskAdapter taskAdapter;
    Context context;
    JSONArray alltaskarray=null;

    public CompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_completed, container, false);

        tasklist=(ListView)view.findViewById(R.id.tasklistcompleted);

        context=getContext();
        sessionManager=new SessionManager(getContext());
        projectsession = new ProjectManager(context);

        if (!Util.verificaConexao(getActivity())){
            Util.initToast(getActivity(),"You do not have an internet connection");
            offlineAllTask();
        }else{
            getAllTasks(sessionManager.getUserId().toString());
        }

        return view;
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.projects_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if(allTasks.contains(query)){
                    taskAdapter.getFilter().filter(query);
                }else{
                    Toast.makeText(getActivity(), "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0)
                {
                    getAllTasks(sessionManager.getUserId().toString());
                }
                else
                {
                    taskAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }




    private void getAllTasks(String s) {

        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(getActivity());
        //pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(getActivity(), "/LoginAndroidService/GetAllTask?userId=" + s, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        pd.hide();

                        try {
                            results = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        allTasks.clear();
                        if (results.length() > 0) {
                            projectsession.setCOMPLETEDTASKRESULT(results.toString());
                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject object = results.getJSONObject(i);
                                    subActivityname = object.getString("subActivityName");
                                    description = object.getString("description");
                                    status = object.getString("status");
                                    startdate = object.getString("lastModificationTime");

                                    String date1 = startdate;
                                    if(startdate.length()>4) {

                                        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                                        Date newDate = null;
                                        try {
                                            newDate = spf.parse(date1);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        spf = new SimpleDateFormat("dd-MM-yyyy");
                                        date1 = spf.format(newDate);
                                    }
                                    else
                                    {
                                        date1="";
                                    }
                                    taskdetails = new AllTasks(subActivityname,description,status,date1);
                                    if(status.equals("3")||status.equals("4")) {
                                        allTasks.add(taskdetails);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Project is not assinged yet", Toast.LENGTH_SHORT).show();
                        }

                        if (allTasks.size() > 0) {

                            taskAdapter= new CompletedTaskAdapter(getActivity(),allTasks);
                            CompletedFragment.tasklist.setAdapter(taskAdapter);
                        }

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("error", "onFailure : " + statusCode);
                        offlineAllTask();
                    }


                    @Override
                    public void onStart() {
                        pd.show();
                    }

                    @Override
                    public void onFinish() {
                        if(pd.isShowing())
                        {
                            pd.hide();
                        }
                    }
                });
    }

    public void offlineAllTask()
    {

        SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
        String activitydata=pref.getString("completedtaskresult","");

        try {
            alltaskarray = new JSONArray(activitydata);
        } catch (Throwable t) {

        }
        allTasks.clear();

        if (alltaskarray.length() > 0) {

            for (int i = 0; i < alltaskarray.length(); i++) {
                try {
                    JSONObject object = alltaskarray.getJSONObject(i);
                    subActivityname = object.getString("subActivityName");
                    description = object.getString("description");
                    status = object.getString("status");
                    startdate = object.getString("lastModificationTime");

                    String date1 = startdate;
                    if(startdate.length()>4) {

                        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                        Date newDate = null;
                        try {
                            newDate = spf.parse(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        spf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = spf.format(newDate);
                    }
                    else
                    {
                        date1="";
                    }
                    taskdetails = new AllTasks(subActivityname,description,status,date1);
                    if(status.equals("3")) {
                        allTasks.add(taskdetails);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Project is not assinged yet", Toast.LENGTH_SHORT).show();
        }

        if (allTasks.size() > 0) {
            taskAdapter= new CompletedTaskAdapter(getActivity(),allTasks);
            CompletedFragment.tasklist.setAdapter(taskAdapter);
        }
    }


    public static class AllTasks {

        String subActivityname, description,status, startdate;

        public AllTasks() {
        }

        public AllTasks(String subActivityname, String description, String status, String startdate) {
            this.subActivityname = subActivityname;
            this.description = description;
            this.status = status;
            this.startdate = startdate;
        }

        public String getSubActivityname() {
            return subActivityname;
        }

        public void setSubActivityname(String subActivityname) {
            this.subActivityname = subActivityname;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStartdate() {
            return startdate;
        }

        public void setStartdate(String startdate) {
            this.startdate = startdate;
        }
    }


}
