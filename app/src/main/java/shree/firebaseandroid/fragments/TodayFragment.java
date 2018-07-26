package shree.firebaseandroid.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.R;
import shree.firebaseandroid.adapter.CompletedTaskAdapter;
import shree.firebaseandroid.adapter.TaskAdapter;
import shree.firebaseandroid.adapter.TodayTaskAdapter;
import shree.firebaseandroid.utils.ProjectManager;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {


    public static ListView tasklist;
    ProgressDialog pd;
    public static ArrayList<AllTasks> allTasks = new ArrayList<AllTasks>();
    public static AllTasks taskdetails;

    TodayTaskAdapter taskAdapter;
    SessionManager sessionManager;
    ProjectManager projectsession;
    Context context;
    JSONArray alltaskarray=null;
    JSONArray results = null;
    String subActivityname, description,status, startdate,enddate;

    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_today, container, false);

        tasklist=(ListView)view.findViewById(R.id.tasklisttoday);

        context=getContext();
        sessionManager=new SessionManager(context);
        projectsession = new ProjectManager(context);

        if (!Util.verificaConexao(getActivity())){
            Util.initToast(getActivity(),"You do not have an internet connection");
            offlineAllTask();
        }else{
            getAllTasks(sessionManager.getUserId().toString());
        }


        return view;
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
                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject object = results.getJSONObject(i);
                                    subActivityname = object.getString("subActivityName");
                                    description = object.getString("description");
                                    status = object.getString("status");
                                    startdate = object.getString("startDate");
                                    enddate=object.getString("endDate");

                                    SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                                    Date newDate= null;
                                    try {
                                        newDate = spf.parse(startdate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    spf= new SimpleDateFormat("dd-MM-yyyy");
                                    startdate = spf.format(newDate);

                                    SimpleDateFormat dcom = new SimpleDateFormat("dd-MM-yyyy");
/*
                                    Date testDate=dcom.parse(getCurrentDate());
                                    Date sdate=dcom.parse(startdate);
                                    Date edate=dcom.parse(enddate);
                                   boolean result=testDate.after(dcom.format(sdate)) && testDate.before(dcom.format(edate));
                                   dcom.parse(getCurrentDate()).after(sdate)&&dcom.parse(getCurrentDate()).before(edate)*/

                                    if(dcom.parse(startdate).compareTo(dcom.parse(getCurrentDate())) == 0) {
                                        taskdetails = new AllTasks(subActivityname, description, status, startdate);
                                        allTasks.add(taskdetails);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //Toast.makeText(getActivity(), "Task is not assinged yet", Toast.LENGTH_SHORT).show();
                        }

                        if (allTasks.size() > 0) {

                            taskAdapter= new TodayTaskAdapter(getActivity(),allTasks);
                            TodayFragment.tasklist.setAdapter(taskAdapter);
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

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy ");
        String strDate = "" + mdformat.format(calendar.getTime());
        return strDate;
    }

/*
    boolean isWithinRange(Date testDate,String) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }*/

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
                    startdate = object.getString("startDate");

                    String date1=startdate;
                    SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                    Date newDate= null;
                    try {
                        newDate = spf.parse(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spf= new SimpleDateFormat("dd-MM-yyyy");
                    date1 = spf.format(newDate);

                    String currentdate=getCurrentDate();

                    SimpleDateFormat dcom = new SimpleDateFormat("dd-MM-yyyy");
                    Date d1 = dcom.parse(date1);
                    Date d2 = dcom.parse(currentdate);

                    if(d1.compareTo(d2) == 0) {
                        taskdetails = new AllTasks(subActivityname, description, status, date1);
                        allTasks.add(taskdetails);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Project is not assinged yet", Toast.LENGTH_SHORT).show();
        }

        if (allTasks.size() > 0) {
            taskAdapter= new TodayTaskAdapter(getActivity(),allTasks);
            TodayFragment.tasklist.setAdapter(taskAdapter);
        }
    }



    public class AllTasks {


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
