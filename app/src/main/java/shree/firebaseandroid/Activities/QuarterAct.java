package shree.firebaseandroid.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.app.Fragment;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.MainActivity;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.adapter.SubActivitiesListAdapter;
import shree.firebaseandroid.fragments.FragmentProjects;
import shree.firebaseandroid.fragments.SubActivities;
import shree.firebaseandroid.fragments.dummy;
import shree.firebaseandroid.utils.ProjectManager;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;
import shree.firebaseandroid.utils.ViewPagerAdapter;

import static shree.firebaseandroid.adapter.ProjectListAdapter.SelectedProjectname;

public class QuarterAct extends AppCompatActivity {


    View view;
    ProgressDialog pd;
    SessionManager sessionManager;
    public SubActivityDetails subactivityDetails;
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydata = new ArrayList<QuarterAct.SubActivityDetails>();
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydataq2 = new ArrayList<QuarterAct.SubActivityDetails>();
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydataq3 = new ArrayList<QuarterAct.SubActivityDetails>();
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydataq4 = new ArrayList<QuarterAct.SubActivityDetails>();

    SubActivitiesListAdapter subactivitiesListAdapter;
    ListView subactivitylistView;
    TextView acttitle,taskstatus;
    //This is our tablayout used show all task related tabs.
    private TabLayout tabLayout;
    //This is our viewPager used to swap between tabs of task.
    private ViewPager viewPager;
    FragmentTransaction transaction=null;
    public static String selectedquater="1",projectname;
    dummy frmdummy;
    public Context context;
    public static android.support.v4.app.FragmentManager mFragmentManager;
    private ProjectManager projectsession;
    JSONArray results = null;
    String activityname,UoM,unit,startDate,endDate,description,status,quaterid,id,statusvalue="",achivedunits,managerid,managername,costyear,programQuqterUnitMappingID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarter);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        context=getApplicationContext();
        mFragmentManager = getSupportFragmentManager();
        subactivitylistView=(ListView)findViewById(R.id.subactivitylist);

        sessionManager=new SessionManager(this);
        projectsession = new ProjectManager(context);

        if (!Util.verificaConexao(this)){
            Util.initToast(this,"You do not have an internet connection");
            offlineSubActivities();
        }else{
            getSubActivities(sessionManager.getUserId());
        }

        acttitle=(TextView)findViewById(R.id.txtacttitle);

       acttitle.setText(ProjectActivities.subactname);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                projectname = null;
            } else {
                projectname = extras.getString("project_name");
            }
        } else
        {
                projectname = (String) savedInstanceState.getSerializable("project_name");
        }


        taskstatus=(TextView)findViewById(R.id.subactstatus);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.quarterviewpager);
        viewPager.setOffscreenPageLimit(4);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.quartertablayout);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);
                if(position==0)
                {
                    selectedquater="1";

                    if (!Util.verificaConexao(QuarterAct.this)){
                        Util.initToast(QuarterAct.this,"You do not have an internet connection");
                        offlineSubActivities();
                    }else{
                        getSubActivities(sessionManager.getUserId());
                    }

                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                  /*  transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.quarterlist, new dummy());*/

                    //mFragmentManager.beginTransaction().replace(R.id.listofitems1, new dummy()).commit();
                   // transaction.commit();
                }
                else if(position==1)
                {
                    selectedquater="2";
                    if (!Util.verificaConexao(QuarterAct.this)){
                        Util.initToast(QuarterAct.this,"You do not have an internet connection");
                        offlineSubActivities();
                    }else{
                        getSubActivities(sessionManager.getUserId());
                    }

                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                  /*  transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.quarterlist, new dummy());
                    transaction.commit();*/
                }
                else if(position==2)
                {
                    selectedquater="3";
                    if (!Util.verificaConexao(QuarterAct.this)){
                        Util.initToast(QuarterAct.this,"You do not have an internet connection");
                        offlineSubActivities();
                    }else{
                        getSubActivities(sessionManager.getUserId());
                    }

                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    /*transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.quarterlist, new dummy());
                    transaction.commit();*/
                }
                else if(position==3)
                {
                    selectedquater="4";
                    if (!Util.verificaConexao(QuarterAct.this)){
                        Util.initToast(QuarterAct.this,"You do not have an internet connection");
                        offlineSubActivities();
                    }else{
                        getSubActivities(sessionManager.getUserId());
                    }

                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                   /* transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.quarterlist, new dummy());
                    transaction.commit();*/
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setupViewPager(viewPager);
        getSupportActionBar().setTitle(SelectedProjectname);


        subactivitylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(selectedquater.equals("1")) {

                    String subactname = subacttivitydata.get(position).getSubActivityName().toString();
                    String subactpos = String.valueOf(position);
                    String subactid = subacttivitydata.get(position).getId().toString();
                    Intent i = new Intent(QuarterAct.this, MainActivity.class);
                    i.putExtra("ProjectTitle", ProjectActivities.projectname);
                    i.putExtra("listTitle", ProjectActivities.subactname);
                    i.putExtra("sublistid", subactid);
                    i.putExtra("sublistidpos", subactpos);
                    i.putExtra("selectedquater", selectedquater);
                    i.putExtra("selectedsubactivity", subactname);
                    startActivity(i);
                }
                else if(selectedquater.equals("2"))
                {
                    String subactname = subacttivitydataq2.get(position).getSubActivityName().toString();
                    String subactpos = String.valueOf(position);
                    String subactid = subacttivitydataq2.get(position).getId().toString();
                    Intent i = new Intent(QuarterAct.this, MainActivity.class);
                    i.putExtra("ProjectTitle", ProjectActivities.projectname);
                    i.putExtra("listTitle", ProjectActivities.subactname);
                    i.putExtra("sublistid", subactid);
                    i.putExtra("sublistidpos", subactpos);
                    i.putExtra("selectedquater", selectedquater);
                    i.putExtra("selectedsubactivity", subactname);
                    startActivity(i);
                }
                else if(selectedquater.equals("3"))
                {
                    String subactname = subacttivitydataq3.get(position).getSubActivityName().toString();
                    String subactpos = String.valueOf(position);
                    String subactid = subacttivitydataq3.get(position).getId().toString();
                    Intent i = new Intent(QuarterAct.this, MainActivity.class);
                    i.putExtra("ProjectTitle", ProjectActivities.projectname);
                    i.putExtra("listTitle", ProjectActivities.subactname);
                    i.putExtra("sublistid", subactid);
                    i.putExtra("sublistidpos", subactpos);
                    i.putExtra("selectedquater", selectedquater);
                    i.putExtra("selectedsubactivity", subactname);
                    startActivity(i);
                }
                else if(selectedquater.equals("4"))
                {
                    String subactname = subacttivitydataq4.get(position).getSubActivityName().toString();
                    String subactpos = String.valueOf(position);
                    String subactid = subacttivitydataq4.get(position).getId().toString();
                    Intent i = new Intent(QuarterAct.this, MainActivity.class);
                    i.putExtra("ProjectTitle", ProjectActivities.projectname);
                    i.putExtra("listTitle", ProjectActivities.subactname);
                    i.putExtra("sublistid", subactid);
                    i.putExtra("sublistidpos", subactpos);
                    i.putExtra("selectedquater", selectedquater);
                    i.putExtra("selectedsubactivity", subactname);
                    startActivity(i);
                }
            }
        });
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
        transaction = getFragmentManager().beginTransaction();
      /*  transaction.replace(R.id.quarterlist, new dummy());*/
        selectedquater="1";
        transaction.commit();

    }


    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        frmdummy =new dummy();
        adapter.addFragment(frmdummy,"Quarter1");
        frmdummy =new dummy();
        adapter.addFragment(frmdummy,"Quarter2");
        frmdummy =new dummy();
        adapter.addFragment(frmdummy,"Quarter3");
        frmdummy =new dummy();
        adapter.addFragment(frmdummy,"Quarter4");
        viewPager.setAdapter(adapter);
    }



    private void getSubActivities(String userid) {
        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(QuarterAct.this);
        //pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));
        String tid = "1";
        String prgid=ProjectActivities.programactionareaid;
        int actid=Integer.parseInt(FragmentProjects.projectid);

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(QuarterAct.this, "/LoginAndroidService/GetSubActivityForAndri?userId=" +userid+ "&TenantId=" + tid+"&programId="+actid+"&PrgActionAreaActivityMappingID="+prgid , headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        subacttivitydata.clear();
                        subacttivitydataq2.clear();
                        subacttivitydataq3.clear();
                        subacttivitydataq4.clear();
                        pd.hide();

                        try {

                            if (response.getString("result").equals(null)) {
                                Toast.makeText(QuarterAct.this, "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                            }else {
                                results = response.getJSONArray("result");

                                if (results.length() > 0) {

                                    subacttivitydata.clear();
                                    subacttivitydataq2.clear();
                                    subacttivitydataq3.clear();
                                    subacttivitydataq4.clear();
                                    projectsession.setSUBACTIVITYRESULT(results.toString());
                                    for (int i = 0; i < results.length(); i++) {
                                        try {
                                            JSONObject object = results.getJSONObject(i);
                                            activityname = object.getString("subActivityname");
                                            UoM=object.getString("unitofmeasure");
                                            unit=object.getString("unit");
                                            startDate=object.getString("startDate");
                                            endDate=object.getString("endDate");
                                            description=object.getString("description");
                                            status=object.getString("status");
                                            quaterid=object.getString("quarterID");
                                            id=object.getString("id");
                                            achivedunits=object.getString("achieveUnits");
                                            managerid=object.getString("managerId");
                                            managername=object.getString("managerName");
                                            costyear=object.getString("costEstimationYear");
                                            programQuqterUnitMappingID=object.getString("programQuqterUnitMappingID");


                                            if(status.equals("2"))
                                            {
                                                statusvalue="InProgress";
                                            }
                                            else if(status.equals("3"))
                                            {
                                                statusvalue="Completed";
                                            }
                                            else if(status.equals("4"))
                                            {
                                                statusvalue="Overdue";
                                            }
                                            else{
                                                statusvalue="NotStarted";
                                            }

                                            if(quaterid.equals("1")) {
                                                subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydata.add(subactivityDetails);
                                            }else if(quaterid.equals("2"))
                                            {
                                                subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydataq2.add(subactivityDetails);
                                            }else if(quaterid.equals("3"))
                                            {
                                                subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydataq3.add(subactivityDetails);
                                            }else if (quaterid.equals("4"))
                                            {
                                                subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydataq4.add(subactivityDetails);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Toast.makeText(QuarterAct.this, "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                                }

                                if (selectedquater.equals("1")) {//subacttivitydata.size() > 0 &&

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydata);
                                    subactivitylistView.setAdapter(subactivitiesListAdapter);
                                }else if (selectedquater.equals("2")) {//subacttivitydataq2.size() > 0 &&

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq2);
                                    subactivitylistView.setAdapter(subactivitiesListAdapter);
                                }else if (selectedquater.equals("3")) {//subacttivitydataq3.size() > 0 &&

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq3);
                                    subactivitylistView.setAdapter(subactivitiesListAdapter);

                               /* }else if (subacttivitydataq4.size() > 0 && selectedquater.equals("4")) {*/
                                }else if (selectedquater.equals("4")) {

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq4);
                                    subactivitylistView.setAdapter(subactivitiesListAdapter);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("error", "onFailure : " + statusCode);

                        /*SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
                        String subactivitydata=pref.getString("subactivityresult","");
                        JSONArray subactivityarray=null;
                        try {
                            subactivityarray = new JSONArray(subactivitydata);
                        } catch (Throwable t) {

                        }
                        subacttivitydata.clear();
                        subacttivitydataq2.clear();
                        subacttivitydataq3.clear();
                        subacttivitydataq4.clear();

                        if (subactivityarray.length() > 0) {
                            for (int i = 0; i < subactivityarray.length(); i++) {
                                try {
                                    JSONObject object = subactivityarray.getJSONObject(i);
                                    activityname = object.getString("subActivityname");
                                    UoM=object.getString("unitofmeasure");
                                    unit=object.getString("unit");
                                    startDate=object.getString("startDate");
                                    endDate=object.getString("endDate");
                                    description=object.getString("description");
                                    status=object.getString("status");
                                    quaterid=object.getString("quarterID");
                                    id=object.getString("id");
                                    achivedunits=object.getString("achieveUnits");
                                    managerid=object.getString("managerId");
                                    managername=object.getString("managerName");
                                    costyear=object.getString("costEstimationYear");
                                    programQuqterUnitMappingID=object.getString("programQuqterUnitMappingID");


                                    if(status.equals("2"))
                                    {
                                        statusvalue="InProgress";
                                    }
                                    else if(status.equals("3"))
                                    {
                                        statusvalue="Completed";
                                    }
                                    else if(status.equals("4"))
                                    {
                                        statusvalue="Overdue";
                                    }
                                    else{
                                        statusvalue="NotStarted";
                                    }

                                    if(quaterid.equals("1")) {
                                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                        subacttivitydata.add(subactivityDetails);
                                    }else if(quaterid.equals("2"))
                                    {
                                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                        subacttivitydataq2.add(subactivityDetails);
                                    }else if(quaterid.equals("3"))
                                    {
                                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                        subacttivitydataq3.add(subactivityDetails);
                                    }else if (quaterid.equals("4"))
                                    {
                                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                        subacttivitydataq4.add(subactivityDetails);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(QuarterAct.this, "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                        }

                        if (subacttivitydata.size() > 0 && selectedquater.equals("1")) {

                            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydata);
                            subactivitylistView.setAdapter(subactivitiesListAdapter);
                        }else if (subacttivitydataq2.size() > 0 && selectedquater.equals("2")) {

                            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq2);
                            subactivitylistView.setAdapter(subactivitiesListAdapter);
                        }else if (subacttivitydataq3.size() > 0 && selectedquater.equals("3")) {

                            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq3);
                            subactivitylistView.setAdapter(subactivitiesListAdapter);

                        }else if (subacttivitydataq4.size() > 0 && selectedquater.equals("4")) {

                            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq4);
                            subactivitylistView.setAdapter(subactivitiesListAdapter);
                        }*/
                    }

                    @Override
                    public void onStart() {
                        pd.show();
                    }

                    @Override
                    public void onFinish() {
                        if (pd.isShowing()) {
                            pd.hide();
                        }
                    }
                });
    }


    public void offlineSubActivities()
    {
        SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
        String subactivitydata=pref.getString("subactivityresult","");
        JSONArray subactivityarray=null;
        try {
            subactivityarray = new JSONArray(subactivitydata);
        } catch (Throwable t) {

        }
        subacttivitydata.clear();
        subacttivitydataq2.clear();
        subacttivitydataq3.clear();
        subacttivitydataq4.clear();

        if (subactivityarray.length() > 0) {
            for (int i = 0; i < subactivityarray.length(); i++) {
                try {
                    JSONObject object = subactivityarray.getJSONObject(i);
                    activityname = object.getString("subActivityname");
                    UoM=object.getString("unitofmeasure");
                    unit=object.getString("unit");
                    startDate=object.getString("startDate");
                    endDate=object.getString("endDate");
                    description=object.getString("description");
                    status=object.getString("status");
                    quaterid=object.getString("quarterID");
                    id=object.getString("id");
                    achivedunits=object.getString("achieveUnits");
                    managerid=object.getString("managerId");
                    managername=object.getString("managerName");
                    costyear=object.getString("costEstimationYear");
                    programQuqterUnitMappingID=object.getString("programQuqterUnitMappingID");


                    if(status.equals("2"))
                    {
                        statusvalue="InProgress";
                    }
                    else if(status.equals("3"))
                    {
                        statusvalue="Completed";
                    }
                    else if(status.equals("4"))
                    {
                        statusvalue="Overdue";
                    }
                    else{
                        statusvalue="NotStarted";
                    }

                    if(quaterid.equals("1")) {
                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                        subacttivitydata.add(subactivityDetails);
                    }else if(quaterid.equals("2"))
                    {
                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                        subacttivitydataq2.add(subactivityDetails);
                    }else if(quaterid.equals("3"))
                    {
                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                        subacttivitydataq3.add(subactivityDetails);
                    }else if (quaterid.equals("4"))
                    {
                        subactivityDetails= new SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                        subacttivitydataq4.add(subactivityDetails);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(QuarterAct.this, "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
        }

        if (subacttivitydata.size() > 0 && selectedquater.equals("1")) {

            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydata);
            subactivitylistView.setAdapter(subactivitiesListAdapter);
        }else if (subacttivitydataq2.size() > 0 && selectedquater.equals("2")) {

            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq2);
            subactivitylistView.setAdapter(subactivitiesListAdapter);
        }else if (subacttivitydataq3.size() > 0 && selectedquater.equals("3")) {

            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq3);
            subactivitylistView.setAdapter(subactivitiesListAdapter);

        }else if (subacttivitydataq4.size() > 0 && selectedquater.equals("4")) {

            subactivitiesListAdapter = new SubActivitiesListAdapter(QuarterAct.this, subacttivitydataq4);
            subactivitylistView.setAdapter(subactivitiesListAdapter);
        }

    }
    public static class SubActivityDetails {

        String subActivityName;
        String uoM;
        String id;
        String unit;
        String startDate;
        String endDate;
        String description;
        String status;
        String creationTime;
        String achivedunits;
        String managerid;
        String managername;
        String costyear;
        String programQuqterUnitMappingID;

        public SubActivityDetails() {
        }

        public SubActivityDetails(String subActivityName, String uoM, String unit,String startDate,  String endDate, String description,String status,  String creationTime,String id,String achivedunits,String managerid,String managername,String costyear,String programQuqterUnitMappingID ) {

            this.subActivityName = subActivityName;
            this.uoM = uoM;
            this.unit = unit;
            this.startDate = startDate;
            this.endDate = endDate;
            this.description = description;
            this.status = status;
            this.creationTime = creationTime;
            this.id = id;
            this.achivedunits=achivedunits;
            this.managerid=managerid;
            this.managername=managername;
            this.costyear=costyear;
            this.programQuqterUnitMappingID=programQuqterUnitMappingID;
        }


        public String getSubActivityName() {
            return subActivityName;
        }

        public void setSubActivityName(String subActivityName) {
            this.subActivityName = subActivityName;
        }

        public String getUoM() {
            return uoM;
        }

        public void setUoM(String uoM) {
            uoM = uoM;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
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

        public String getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(String creationTime) {
            this.creationTime = creationTime;
        }

        public String getAchivedunits() {
            return achivedunits;
        }

        public String getManagerid() {
            return managerid;
        }

        public String getManagername() {
            return managername;
        }

        public String getCostyear() {
            return costyear;
        }

        public String getProgramQuqterUnitMappingID() {
            return programQuqterUnitMappingID;
        }

        public void setAchivedunits(String achivedunits) {
            this.achivedunits = achivedunits;
        }

        public void setManagerid(String managerid) {
            this.managerid = managerid;
        }

        public void setManagername(String managername) {
            this.managername = managername;
        }

        public void setCostyear(String costyear) {
            this.costyear = costyear;
        }

        public void setProgramQuqterUnitMappingID(String programQuqterUnitMappingID) {
            this.programQuqterUnitMappingID = programQuqterUnitMappingID;
        }
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

                if (subacttivitydata.contains(query)) {
                    subactivitiesListAdapter.getFilter().filter(query);
                } else {
                    Toast.makeText(QuarterAct.this, "No Match found", Toast.LENGTH_LONG).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() == 0) {
                    getSubActivities(sessionManager.getUserId());
                } else {
                    subactivitiesListAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });


        return true;
    }
}
