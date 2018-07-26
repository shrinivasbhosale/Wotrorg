package shree.firebaseandroid;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
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
import shree.firebaseandroid.Activities.QuarterAct;
import shree.firebaseandroid.LocalDatabase.DatabaseHandler;
import shree.firebaseandroid.adapter.ActivitiesListAdapter;
import shree.firebaseandroid.adapter.ProjectListAdapter;
import shree.firebaseandroid.fragments.FragmentProjects;
import shree.firebaseandroid.fragments.SubActivities;
import shree.firebaseandroid.utils.ProjectManager;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;

import static android.view.View.GONE;
import static shree.firebaseandroid.adapter.ProjectListAdapter.SelectedProjectname;

public class ProjectActivities extends AppCompatActivity {

    public static ArrayList<ActivityDetails> acttivitydata = new ArrayList<ActivityDetails>();
    boolean hidden = true;
    DisplayMetrics metrics;
    int width;
    ProgressDialog pd;
    public static ActivityDetails activityDetails;
    ActivitiesListAdapter activitiesListAdapter;
    ListView activitylistView;
    public static android.support.v4.app.FragmentManager mFragmentManager;
    public static String subactid = "", subactname = "", programactionareaid = "";
    SessionManager sessionManager;
    public static String projectname = "", icon = "", projectid = "";
    TextView activitycount;
    public static ImageButton imgback,imgsearch;
    public Context context;
    private ProjectManager projectsession;
    JSONArray results = null;
    JSONArray activityarray=null;
    String subActivityName="",creationTime="",activityid="",isDeleted="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_activities);
        activitylistView = (ListView) findViewById(R.id.activitylist);
        activitycount=(TextView)findViewById(R.id.actcount);
        context=getApplicationContext();
        projectsession = new ProjectManager(context);

        /*acttitle = (TextView) findViewById(R.id.txtprojectname);
        imgback = (ImageButton) findViewById(R.id.imgback);*/
       // imgsearch=(ImageButton) findViewById(R.id.ibsearch);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaract);
        setSupportActionBar(toolbar);*/
        /*imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                projectname = null;
            } else {
                projectname = extras.getString("project_name");
                icon = extras.getString("project_icon");
                projectid = extras.getString("project_id");

            }
        } else {
            projectname = (String) savedInstanceState.getSerializable("project_name");
        }
        getSupportActionBar().setTitle(SelectedProjectname);
        //acttitle.setText(projectname);

        activitylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                subactname = acttivitydata.get(position).getSubActivityName().toString();
                subactid = acttivitydata.get(position).getId().toString();
                programactionareaid = acttivitydata.get(position).getIsDeleted().toString();
                mFragmentManager = getSupportFragmentManager();
                //openFragment(new SubActivities());
                Intent i=new Intent(ProjectActivities.this, QuarterAct.class);
                i.putExtra("project_name",projectname);
                startActivity(i);
            }
        });


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        sessionManager = new SessionManager(this);


        if (!Util.verificaConexao(this)){
            Util.initToast(this,"You do not have an internet connection");
            offlineActivities();
        }else{
            getProjectActivities(sessionManager.getUserId(), projectid);
        }


    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), Dashboard.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.projects_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (acttivitydata.contains(query)) {
                    activitiesListAdapter.getFilter().filter(query);
                } else {
                    Toast.makeText(ProjectActivities.this, "No Match found", Toast.LENGTH_LONG).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() == 0) {
                    getProjectActivities(sessionManager.getUserId(), projectid);
                } else {
                    activitiesListAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
        return true;
    }

    public int GetDipsFromPixel(float pixels) {   // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public static void openFragment(Fragment mFragment)
    {
        mFragmentManager.beginTransaction().replace(R.id.listofitems1, mFragment).commit();
    }


    private void getProjectActivities(String s,String projectid) {
        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(this);
        //pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "text/plain"));

        URLConnection.get(this, "/LoginAndroidService/GetActivityByprogramIdForAndroidUser?programId=" +projectid+ "&userId=" + s, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        pd.hide();
                        acttivitydata.clear();
                        try {
                            results = response.getJSONArray("result");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (results.length() > 0) {
                            projectsession.setPROJECTACTIVITYRESULT(results.toString());

                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject object = results.getJSONObject(i);
                                    subActivityName = object.getString("subActivityName");
                                    creationTime = object.getString("creationTime");
                                    activityid= object.getString("id");
                                    isDeleted=object.getString("prgActionAreaID");

                                    activityDetails= new ActivityDetails(subActivityName,creationTime,activityid,isDeleted);

                                    acttivitydata.add(activityDetails);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(ProjectActivities.this, "Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                        }

                        if (acttivitydata.size() > 0) {
                            activitiesListAdapter = new ActivitiesListAdapter(ProjectActivities.this, acttivitydata);
                            activitylistView.setAdapter(activitiesListAdapter);
                            activitycount.setVisibility(View.VISIBLE);
                            activitycount.setText("("+acttivitydata.size()+")");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("error", "onFailure : " + statusCode);

                        SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
                        String activitydata=pref.getString("projectactivityresult","");

                        try {
                            activityarray = new JSONArray(activitydata);
                        } catch (Throwable t) {

                        }
                        acttivitydata.clear();

                        if (activityarray.length() > 0) {

                            for (int i = 0; i < activityarray.length(); i++) {
                                try {
                                    JSONObject object = activityarray.getJSONObject(i);
                                    subActivityName = object.getString("subActivityName");
                                    creationTime = object.getString("creationTime");
                                    activityid= object.getString("id");
                                    isDeleted=object.getString("prgActionAreaID");

                                    activityDetails= new ActivityDetails(subActivityName,creationTime,activityid,isDeleted);

                                    acttivitydata.add(activityDetails);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(ProjectActivities.this, "Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                        }
                        if (acttivitydata.size() > 0) {
                            activitiesListAdapter = new ActivitiesListAdapter(ProjectActivities.this, acttivitydata);
                            activitylistView.setAdapter(activitiesListAdapter);
                            activitycount.setVisibility(View.VISIBLE);
                            activitycount.setText("("+acttivitydata.size()+")");
                        }
                    }

                    @Override
                    public void onStart() {
                        //pd.show();
                    }

                    @Override
                    public void onFinish() {
                        if (pd.isShowing()) {
                            pd.hide();
                        }
                    }
                });
    }


    public void offlineActivities()
    {
        SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
        String activitydata=pref.getString("projectactivityresult","");

        try {
            activityarray = new JSONArray(activitydata);
        } catch (Throwable t) {

        }
        acttivitydata.clear();

        if (activityarray.length() > 0) {

            for (int i = 0; i < activityarray.length(); i++) {
                try {
                    JSONObject object = activityarray.getJSONObject(i);
                    subActivityName = object.getString("subActivityName");
                    creationTime = object.getString("creationTime");
                    activityid= object.getString("id");
                    isDeleted=object.getString("prgActionAreaID");

                    activityDetails= new ActivityDetails(subActivityName,creationTime,activityid,isDeleted);

                    acttivitydata.add(activityDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(ProjectActivities.this, "Activities are not assinged yet", Toast.LENGTH_SHORT).show();
        }
        if (acttivitydata.size() > 0) {
            activitiesListAdapter = new ActivitiesListAdapter(ProjectActivities.this, acttivitydata);
            activitylistView.setAdapter(activitiesListAdapter);
            activitycount.setVisibility(View.VISIBLE);
            activitycount.setText("("+acttivitydata.size()+")");
        }
    }
    public static class ActivityDetails {

        String subActivityName;
        String creationTime;
        String id;
        String isDeleted;

        public ActivityDetails(String subActivityName, String creationTime, String id, String isDeleted) {
            this.subActivityName = subActivityName;
            this.creationTime = creationTime;
            this.id = id;
            this.isDeleted = isDeleted;
        }

        public String getSubActivityName() {
            return subActivityName;
        }

        public void setSubActivityName(String subActivityName) {
            this.subActivityName = subActivityName;
        }

        public String getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(String creationTime) {
            this.creationTime = creationTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            this.isDeleted = isDeleted;
        }
    }
}