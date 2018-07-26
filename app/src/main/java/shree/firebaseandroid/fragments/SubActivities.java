package shree.firebaseandroid.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.Dashboard;

import shree.firebaseandroid.Login;
import shree.firebaseandroid.MainActivity;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.adapter.ActivitiesListAdapter;
import shree.firebaseandroid.adapter.SubActivitiesListAdapter;
import shree.firebaseandroid.classes.CustomExpandableListAdapter1;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.ViewPagerAdapter;

import static shree.firebaseandroid.ProjectActivities.imgback;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubActivities extends Fragment{

    public SubActivities() {
        // Required empty public constructor
    }

   /* View view;
    ProgressDialog pd;
    SessionManager sessionManager;
    public static SubActivityDetails subactivityDetails;
    public static ArrayList<SubActivityDetails> subacttivitydata = new ArrayList<SubActivityDetails>();
    public static ArrayList<SubActivityDetails> subacttivitydataq2 = new ArrayList<SubActivityDetails>();
    public static ArrayList<SubActivityDetails> subacttivitydataq3 = new ArrayList<SubActivityDetails>();
    public static ArrayList<SubActivityDetails> subacttivitydataq4 = new ArrayList<SubActivityDetails>();

    SubActivitiesListAdapter subactivitiesListAdapter;
    ListView subactivitylistView;
    TextView acttitle,taskstatus;
    //This is our tablayout used show all task related tabs.
    private TabLayout tabLayout;
    //This is our viewPager used to swap between tabs of task.
    private ViewPager viewPager;
    FragmentTransaction transaction=null;
    public static String selectedquater;
    dummy frmdummy;
    public Context context;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sub_activities, container, false);
 /*
        context=getContext();
        subactivitylistView=(ListView) view.findViewById(R.id.subactivitylist);

        sessionManager=new SessionManager(getContext());
        getSubActivities(sessionManager.getUserId());
        acttitle=(TextView)view.findViewById(R.id.txtacttitle);

        acttitle.setText(ProjectActivities.subactname);
        taskstatus=(TextView)view.findViewById(R.id.subactstatus);

        //Initializing viewPager
        viewPager = (ViewPager) view.findViewById(R.id.quarterviewpager);
        viewPager.setOffscreenPageLimit(4);

        //Initializing the tablayout
        tabLayout = (TabLayout) view.findViewById(R.id.quartertablayout);
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
                    getSubActivities(sessionManager.getUserId());
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.quarterlist, new dummy());
                    transaction.commit();
                }
                else if(position==1)
                {
                    selectedquater="2";
                    getSubActivities(sessionManager.getUserId());
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    transaction = getFragmentManager().beginTransaction();
                     transaction.replace(R.id.quarterlist, new dummy());
                    transaction.commit();
                }
                else if(position==2)
                {
                    selectedquater="3";
                    getSubActivities(sessionManager.getUserId());
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.quarterlist, new dummy());
                    transaction.commit();
                }
                else if(position==3)
                {
                    selectedquater="4";
                    getSubActivities(sessionManager.getUserId());
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.quarterlist, new dummy());
                    transaction.commit();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setupViewPager(viewPager);

       *//* Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbarfrmback);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getTitle());*//*


        subactivitylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(selectedquater.equals("1")) {

                    String subactname = subacttivitydata.get(position).getSubActivityName().toString();
                    String subactpos = String.valueOf(position);
                    String subactid = subacttivitydata.get(position).getId().toString();
                    Intent i = new Intent(getActivity(), MainActivity.class);
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
                    Intent i = new Intent(getActivity(), MainActivity.class);
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
                    Intent i = new Intent(getActivity(), MainActivity.class);
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
                    Intent i = new Intent(getActivity(), MainActivity.class);
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
        transaction.replace(R.id.quarterlist, new dummy());
        selectedquater="1";
        transaction.commit();*/

        return view;
    }


    /*private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
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

        pd = new ProgressDialog(getActivity());
        //pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));
        String tid = "1";
        String prgid=ProjectActivities.programactionareaid;
        int actid=Integer.parseInt(FragmentProjects.projectid);

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(getActivity(), "/LoginAndroidService/GetSubActivityForAndri?userId=" +userid+ "&TenantId=" + tid+"&programId="+actid+"&PrgActionAreaActivityMappingID="+prgid , headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        subacttivitydata.clear();
                        subacttivitydataq2.clear();
                        subacttivitydataq3.clear();
                        subacttivitydataq4.clear();
                        pd.hide();
                        JSONArray results = null;
                        String activityname,UoM,unit,startDate,endDate,description,status,quaterid,id,statusvalue="",achivedunits,managerid,managername,costyear,programQuqterUnitMappingID;
                        try {

                            if (response.getString("result").equals(null)) {
                                Toast.makeText(getActivity(), "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                            }else {
                                results = response.getJSONArray("result");

                                if (results.length() > 0) {
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
                                    Toast.makeText(getActivity(), "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                                }

                                if (subacttivitydata.size() > 0 && selectedquater.equals("1")) {

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(getActivity(), subacttivitydata);
                                    subactivitylistView.setAdapter(subactivitiesListAdapter);
                                }else if (subacttivitydata.size() > 0 && selectedquater.equals("2")) {

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(getActivity(), subacttivitydataq2);
                                    subactivitylistView.setAdapter(subactivitiesListAdapter);
                                }else if (subacttivitydata.size() > 0 && selectedquater.equals("3")) {

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(getActivity(), subacttivitydataq3);
                                    subactivitylistView.setAdapter(subactivitiesListAdapter);

                                }else if (subacttivitydata.size() > 0 && selectedquater.equals("4")) {

                                    subactivitiesListAdapter = new SubActivitiesListAdapter(getActivity(), subacttivitydataq4);
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
    }*/
}
