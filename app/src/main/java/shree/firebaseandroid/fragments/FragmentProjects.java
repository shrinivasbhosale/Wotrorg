package shree.firebaseandroid.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.app.SearchManager;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
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
import shree.firebaseandroid.LocalDatabase.DatabaseHandler;
import shree.firebaseandroid.Login;
import shree.firebaseandroid.ProjectActivities;

import shree.firebaseandroid.R;
import shree.firebaseandroid.adapter.ProjectListAdapter;
import shree.firebaseandroid.utils.ProjectManager;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;


/**
 * A simple {@link //Fragment} subclass.
 */
public class FragmentProjects extends android.support.v4.app.Fragment {

    View view;
    ProgressDialog pd;


    public static ArrayList<ProjectDetails> projectdata = new ArrayList<ProjectDetails>();

    public static ProjectDetails projectDetails;
    public static ProjectListAdapter projectListAdapter;
    ListView listViewprojects;
    public static String projectid="",projectsname="";
    public SessionManager session;
    public Context context;
    private ProjectManager projectsession;
    int net=1;
    JSONArray results = null;
    String programName, shortDescription, creationTime,programid,sd;

    public FragmentProjects() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_projects, container, false);
        setHasOptionsMenu(true);
        listViewprojects = (ListView) view.findViewById(R.id.projectListView);
        context=getContext();
        projectsession = new ProjectManager(context);
        session = new SessionManager(getActivity());

        if (!Util.verificaConexao(getActivity())){
            Util.initToast(getActivity(),"You do not have an internet connection");
            offlineprojectlist();
        }else{
            getProjectList(session.getUserId().toString());
        }



        listViewprojects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                projectsname=projectdata.get(position).getProgramName().toString();
                String projecticon="R.drawable.lnt_icon_96x96";
                projectid=projectdata.get(position).getProgramid().toString();

                Intent i =new Intent(getActivity(), ProjectActivities.class);
                i.putExtra("project_id",projectid);
                i.putExtra("project_name", projectsname);
                i.putExtra("project_icon", projecticon);
                startActivity(i);


            }
        });

        registerForContextMenu(listViewprojects);
        listViewprojects.setTextFilterEnabled(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {

        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.projects_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if(projectdata.contains(query)){
                    projectListAdapter.getFilter().filter(query);
                }else{
                    Toast.makeText(getActivity(), "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0)
                {
                    getProjectList(session.getUserId().toString());
                }
                else
                {
                    projectListAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
}



    private void getProjectList(String s) {
        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(getActivity());
        //pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(getActivity(), "/LoginAndroidService/GetProjectDetailsForAndroid?userId=" + s, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        pd.hide();
                        try {
                            results = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        projectdata.clear();
                        if (results.length() > 0) {

                            projectsession.setRESULT(results.toString());

                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject object = results.getJSONObject(i);
                                    programid=object.getString("id");
                                    programName = object.getString("programName");
                                    shortDescription = object.getString("shortDescription");
                                    creationTime = object.getString("programStartDate");

                                    if(shortDescription.equals("null"))
                                    {
                                        sd="Description Not Mentioned";
                                    }
                                    else
                                    {
                                        sd=shortDescription;
                                    }

                                    String date1=creationTime;
                                    SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

                                    Date newDate= null;
                                    try {
                                        newDate = spf.parse(date1);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    spf= new SimpleDateFormat("dd-MM-yyyy");
                                    date1 = spf.format(newDate);

                                    projectDetails = new ProjectDetails(programid,programName, sd, date1);
                                    projectdata.add(projectDetails);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Project is not assinged yet", Toast.LENGTH_SHORT).show();
                        }
                        if (projectdata.size() > 0) {
                            projectListAdapter = new ProjectListAdapter(getContext(), projectdata);
                            listViewprojects.setAdapter(projectListAdapter);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("error", "onFailure : " + statusCode);

                        SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
                        String fullprojectdata=pref.getString("result","");
                        JSONArray jsonArray=null;
                        try {
                            jsonArray = new JSONArray(fullprojectdata);
                        } catch (Throwable t) {

                        }
                        projectdata.clear();
                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    programid=object.getString("id");
                                    programName = object.getString("programName");
                                    shortDescription = object.getString("shortDescription");
                                    creationTime = object.getString("programStartDate");

                                    if(shortDescription.equals("null"))
                                    {
                                        sd="Description Not Mentioned";
                                    }
                                    else
                                    {
                                        sd=shortDescription;
                                    }

                                    String date1=creationTime;
                                    SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

                                    Date newDate= null;
                                    try {
                                        newDate = spf.parse(date1);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    spf= new SimpleDateFormat("dd-MM-yyyy");
                                    date1 = spf.format(newDate);

                                    projectDetails = new ProjectDetails(programid,programName, sd, date1);
                                    projectdata.add(projectDetails);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Project is not assinged yet", Toast.LENGTH_SHORT).show();
                        }
                        if (projectdata.size() > 0) {
                            projectListAdapter = new ProjectListAdapter(getContext(), projectdata);
                            listViewprojects.setAdapter(projectListAdapter);
                        }


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

    public void offlineprojectlist()
    {
        SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
        String fullprojectdata=pref.getString("result","");
        JSONArray jsonArray=null;
        try {
            jsonArray = new JSONArray(fullprojectdata);
        } catch (Throwable t) {

        }
        projectdata.clear();
        if (jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    programid=object.getString("id");
                    programName = object.getString("programName");
                    shortDescription = object.getString("shortDescription");
                    creationTime = object.getString("programStartDate");

                    if(shortDescription.equals("null"))
                    {
                        sd="Description Not Mentioned";
                    }
                    else
                    {
                        sd=shortDescription;
                    }

                    String date1=creationTime;
                    SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

                    Date newDate= null;
                    try {
                        newDate = spf.parse(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spf= new SimpleDateFormat("dd-MM-yyyy");
                    date1 = spf.format(newDate);

                    projectDetails = new ProjectDetails(programid,programName, sd, date1);
                    projectdata.add(projectDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Project is not assinged yet", Toast.LENGTH_SHORT).show();
        }
        if (projectdata.size() > 0) {
            projectListAdapter = new ProjectListAdapter(getContext(), projectdata);
            listViewprojects.setAdapter(projectListAdapter);
        }
    }

    public static class ProjectDetails {
        String programName;
        String shortDescription;
        String longDescription;
        String donorID;
        String programSanctionedYear;
        String programStartDate;
        String prgramEndDate;
        String managerID;
        String locationID;
        String isDeleted;
        String deleterUserId;
        String deletionTime;
        String lastModificationTime;
        String lastModifierUserId;
        String creationTime;
        String creatorUserId;
        String programid;

        public ProjectDetails() {
        }

        public ProjectDetails(String programid,String programName, String shortDescription, String creationTime) {
            this.programid=programid;
            this.programName = programName;
            this.shortDescription = shortDescription;
            this.creationTime = creationTime;
        }

        public String getProgramid() {
            return programid;
        }

        public void setProgramid(String programid) {
            this.programid = programid;
        }

        public String getProgramName() {
            return programName;
        }

        public void setProgramName(String programName) {
            this.programName = programName;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public String getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(String creationTime) {
            this.creationTime = creationTime;
        }
    }
}


