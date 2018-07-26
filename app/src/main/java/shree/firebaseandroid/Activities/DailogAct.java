package shree.firebaseandroid.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.Dashboard;
import shree.firebaseandroid.MainActivity;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.adapter.SubActivitiesListAdapter;
import shree.firebaseandroid.fragments.FragmentProjects;
import shree.firebaseandroid.model.ChatModel;
import shree.firebaseandroid.model.UserModel;
import shree.firebaseandroid.utils.ProjectManager;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;

import static shree.firebaseandroid.ProjectActivities.projectname;

public class DailogAct extends Activity {

    ImageView cross;
    TextView tvuom,tvtargetunit,tvachievedunit,tvstartdate,tvenddate,tvstatus,tvdesc;
    Button btnstart,btnupdate;
    String impplanid,subtaskname,uom,targetunit,achievedunit,status,desc,startdate,enddate;
    AlertDialog alertDialog;
    public QuarterAct.SubActivityDetails subactivityDetails;
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydata = new ArrayList<QuarterAct.SubActivityDetails>();
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydataq2 = new ArrayList<QuarterAct.SubActivityDetails>();
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydataq3 = new ArrayList<QuarterAct.SubActivityDetails>();
    public static ArrayList<QuarterAct.SubActivityDetails> subacttivitydataq4 = new ArrayList<QuarterAct.SubActivityDetails>();
    JSONArray results = null;
    String activityname,UoM,unit,startDate,endDate,description,status123,quaterid,id,statusvalue="",achivedunits,managerid,managername,costyear,programQuqterUnitMappingID;
    String subactivityselectedpos;
    String selectedquater;
    String quom,qunit,qstrtdate,qeddate,qdesc,qstatus="",qachieved_units;
    SessionManager sessionManager;
    private ProjectManager projectsession;
    public Context context;
    ProgressDialog pd;
    private DatabaseReference mFirebaseDatabaseReference;
    private UserModel userModel;
    static final String CHAT_REFERENCE = "chatmodel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailog);
        cross=(ImageView)findViewById(R.id.imgcross);

        tvuom=(TextView) findViewById(R.id.tv_uom);
        tvtargetunit=(TextView) findViewById(R.id.tv_target_unit);
        tvachievedunit=(TextView) findViewById(R.id.tv_achieved_units);
        tvstartdate=(TextView) findViewById(R.id.tv_startdate);
        tvenddate=(TextView) findViewById(R.id.tv_enddate);
        tvstatus=(TextView) findViewById(R.id.tv_status);
        tvdesc=(TextView) findViewById(R.id.tv_subactdesc);
        btnstart=(Button)findViewById(R.id.btnstartact);
        btnupdate=(Button)findViewById(R.id.btnupdateact);

        context=getApplicationContext();
        sessionManager=new SessionManager(getApplicationContext());
        projectsession = new ProjectManager(context);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -100;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;;
        params.y = -50;
        this.getWindow().setAttributes(params);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                impplanid = null;
            } else {
                impplanid = extras.getString("implementationplanchecklistid");
                selectedquater=extras.getString("selectedquater");
                subactivityselectedpos=extras.getString("sublistidpos");
                subtaskname=extras.getString("taskname");
            }
        } else {
            impplanid = (String) savedInstanceState.getSerializable("implementationplanchecklistid");
        }

        if (!Util.verificaConexao(this)){
            Util.initToast(this,"You do not have an internet connection");
            offlineSubActivities();
        }else{
            getSubActivities(sessionManager.getUserId());
        }

        userModel = new UserModel(sessionManager.getName().toString(),sessionManager.getCreatorId().toString() , "5tJKB04rhUNjFQuNjpFxlQI6Gin1" );


        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSubActivity(impplanid,qstatus);
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                double tunit=Double.parseDouble(qunit);
                double aunit=Double.parseDouble(qachieved_units);

                if(aunit>=tunit)
                {
                    finish();
                    Toast.makeText(DailogAct.this, "This activity is Completed!", Toast.LENGTH_SHORT).show();
                }
                else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DailogAct.this);

                    alertDialogBuilder.setCancelable(false);
                    LayoutInflater li = LayoutInflater.from(DailogAct.this);
                    View promptsView = li.inflate(R.layout.otp_prompts, null);
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText description;

                    description = (EditText) promptsView.findViewById(R.id.descinfo);

                    final Button otpbtnok = (Button) promptsView.findViewById(R.id.btncomplete);
                    final Button otpbtncancle = (Button) promptsView.findViewById(R.id.btnupdate);

                    // create alert dialog
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.setTitle("Update Achieved Units");
                    View view=li.inflate(R.layout.prompttitle, null);
                    alertDialog.setCustomTitle(view);


                    otpbtnok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                      alertDialog.dismiss();
                        }
                    });


                    otpbtncancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(description.getText().toString().equals(""))
                            {
                                description.setError("Please Enter units!");
                            }
                            else
                            {
                                Double achiunit=Double.parseDouble(description.getText().toString());

                                if(achiunit>Double.parseDouble(qunit)-Double.parseDouble(qachieved_units))
                                {
                                    description.setError("Please Enter units less than target unit!");
                                }
                                else {
                                    //Update units on server
                                    UpdateUnit(impplanid, description.getText().toString());
                                }
                            }

                        }
                    });

                    // show it
                    alertDialog.show();

                }
            }
        });
    }

    private void UpdateUnit(final String impid, final String aunits) {

        URLConnection.put("/LoginAndroidService/UpdateSubActivityUnits?ImplementationPlanCheckListID=" + impid + "&AchieveUnit=" + aunits, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if (response.getString("result").equals("InProgress")||response.getString("result").equals("Completed")||response.getString("result").equals("Overdue")) {

                        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        ChatModel model = new ChatModel(userModel,""+subtaskname.toUpperCase()+"\nAchieved Units :- "+aunits.toUpperCase()+"\nStatus :- "+response.getString("result").toString(), Calendar.getInstance().getTime().getTime()+"",null);
                        mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE).push().setValue(model);
                        //getunitupdates(impid);
                        Toast.makeText(DailogAct.this, "Units Updated.", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getunitupdates(String impid) {


        URLConnection.put("/LoginAndroidService/UpdatesUnit?ImplementationPlanCheckListID=" + impid , null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(DailogAct.this, "Units Updated.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }

    private void startSubActivity(String planid, String state) {

        String startstatus="";

        if(state.equals("NotStarted"))
        {
            startstatus="2";
        }
        else if(state.equals("update"))
        {
            startstatus="3";
        }

        URLConnection.put("/LoginAndroidService/UpdateStatusOfSubActivityForAndroid?ImplementationPlanCheckListID=" + planid + "&status=" + startstatus, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if(response.getString("result").equals("InProgress"))
                    {
                        finish();
                        String taskname=subtaskname.toUpperCase();

                        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        ChatModel model = new ChatModel(userModel,""+taskname+"\nStatus :- Task Started.", Calendar.getInstance().getTime().getTime()+"",null);
                        mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE).push().setValue(model);

                        //getunitupdates(impplanid);
                        //Toast.makeText(DailogAct.this,"Task Started.",Toast.LENGTH_SHORT).show();
                       // finish();
                    }
                    else if(response.getString("result").equals("Completed"))
                    {
                        finish();
                        Toast.makeText(DailogAct.this,"Task Completed.",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //Get SubActivity Details..
    private void getSubActivities(String userid) {
        List<Header> headers = new ArrayList<Header>();

        /*pd.setTitle(getString(R.string.connecting));*/
        pd = new ProgressDialog(DailogAct.this);
        pd.setMessage(getString(R.string.wait));

        String tid = "1";
        String prgid= ProjectActivities.programactionareaid;
        int actid=Integer.parseInt(FragmentProjects.projectid);

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(DailogAct.this, "/LoginAndroidService/GetSubActivityForAndri?userId=" +userid+ "&TenantId=" + tid+"&programId="+actid+"&PrgActionAreaActivityMappingID="+prgid , headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        pd.hide();
                        subacttivitydata.clear();
                        subacttivitydataq2.clear();
                        subacttivitydataq3.clear();
                        subacttivitydataq4.clear();
                        try {

                            if (response.getString("result").equals(null)) {
                                Toast.makeText(DailogAct.this, "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                            }else {
                                results = response.getJSONArray("result");

                                if (results.length() > 0) {

                                    projectsession.setDAILOGACTRESULT(results.toString());

                                    for (int i = 0; i < results.length(); i++) {
                                        try {
                                            JSONObject object = results.getJSONObject(i);
                                            activityname = object.getString("subActivityname");
                                            UoM=object.getString("unitofmeasure");
                                            unit=object.getString("unit");
                                            startDate=object.getString("startDate");
                                            endDate=object.getString("endDate");
                                            description=object.getString("description");
                                            status123=object.getString("status");
                                            quaterid=object.getString("quarterID");
                                            id=object.getString("id");
                                            achivedunits=object.getString("achieveUnits");
                                            managerid=object.getString("managerId");
                                            managername=object.getString("managerName");
                                            costyear=object.getString("costEstimationYear");
                                            programQuqterUnitMappingID=object.getString("programQuqterUnitMappingID");


                                            if(status123.equals("2"))
                                            {
                                                statusvalue="InProgress";
                                            }
                                            else if(status123.equals("3"))
                                            {
                                                statusvalue="Completed";
                                            }
                                            else if(status123.equals("4"))
                                            {
                                                statusvalue="Overdue";
                                            }
                                            else{
                                                statusvalue="NotStarted";
                                            }

                                            if(quaterid.equals("1")) {
                                                subactivityDetails= new QuarterAct.SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydata.add(subactivityDetails);
                                            }else if(quaterid.equals("2"))
                                            {
                                                subactivityDetails= new QuarterAct.SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydataq2.add(subactivityDetails);
                                            }else if(quaterid.equals("3"))
                                            {
                                                subactivityDetails= new QuarterAct.SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydataq3.add(subactivityDetails);
                                            }else if (quaterid.equals("4"))
                                            {
                                                subactivityDetails= new QuarterAct.SubActivityDetails(activityname,UoM,unit,startDate,endDate,description,statusvalue,quaterid,id,achivedunits,managerid,managername,costyear,programQuqterUnitMappingID);
                                                subacttivitydataq4.add(subactivityDetails);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Toast.makeText(DailogAct.this, "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                                }

                            }

                            int pos=Integer.parseInt(subactivityselectedpos);
                            if(selectedquater.equals("1")) {
                                quom = subacttivitydata.get(pos).getUoM().toString();
                                qunit = subacttivitydata.get(pos).getUnit().toString();
                                qachieved_units=subacttivitydata.get(pos).getAchivedunits().toString();
                                qstatus=subacttivitydata.get(pos).getStatus().toString();
                                qstrtdate = subacttivitydata.get(pos).getStartDate().toString();
                                qeddate = subacttivitydata.get(pos).getEndDate().toString();
                                qdesc =subacttivitydata.get(pos).getDescription().toString();
                            }
                            else if(selectedquater.equals("2")){
                                quom = subacttivitydataq2.get(pos).getUoM().toString();
                                qunit = subacttivitydataq2.get(pos).getUnit().toString();
                                qachieved_units=subacttivitydataq2.get(pos).getAchivedunits().toString();
                                qstatus=subacttivitydataq2.get(pos).getStatus().toString();
                                qstrtdate = subacttivitydataq2.get(pos).getStartDate().toString();
                                qeddate =subacttivitydataq2.get(pos).getEndDate().toString();
                                qdesc =subacttivitydataq2.get(pos).getDescription().toString();
                            }
                            else if(selectedquater.equals("3"))
                            {
                                quom = subacttivitydataq3.get(pos).getUoM().toString();
                                qunit = subacttivitydataq3.get(pos).getUnit().toString();
                                qachieved_units=subacttivitydataq3.get(pos).getAchivedunits().toString();
                                qstatus=subacttivitydataq3.get(pos).getStatus().toString();
                                qstrtdate =subacttivitydataq3.get(pos).getStartDate().toString();
                                qeddate = subacttivitydataq3.get(pos).getEndDate().toString();
                                qdesc = subacttivitydataq3.get(pos).getDescription().toString();
                            }
                            else if(selectedquater.equals("4"))
                            {
                                quom = subacttivitydataq4.get(pos).getUoM().toString();
                                qunit = subacttivitydataq4.get(pos).getUnit().toString();
                                qachieved_units=subacttivitydataq4.get(pos).getAchivedunits().toString();
                                qstatus=subacttivitydataq4.get(pos).getStatus().toString();
                                qstrtdate = subacttivitydataq4.get(pos).getStartDate().toString();
                                qeddate = subacttivitydataq4.get(pos).getEndDate().toString();
                                qdesc = subacttivitydataq4.get(pos).getDescription().toString();
                            }
                            tvuom.setText(quom);
                            tvtargetunit.setText(qunit);
                            tvachievedunit.setText(qachieved_units);
                            tvstatus.setText(qstatus);
                            tvstartdate.setText(convertdate(qstrtdate));
                            tvenddate.setText(convertdate(qeddate));
                            tvdesc.setText(qdesc);

                            if(qstatus.equals("NotStarted"))
                            {
                                btnupdate.setVisibility(View.GONE);
                                btnstart.setVisibility(View.VISIBLE);
                            }
                            else if(qstatus.equals("Completed")||qstatus.equals("Overdue")||qstatus.equals("InProgress"))
                            {
                                btnstart.setVisibility(View.GONE);
                                btnupdate.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("error", "onFailure : " + statusCode);
                          //  offlineSubActivities();
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

    private String convertdate(String startdate) {
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

        return date1;
    }

    // To get Offline subActivity details
    public void offlineSubActivities() {

        SharedPreferences pref = context.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
        String subactivitydata=pref.getString("dailogactresult","");
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
                            UoM = object.getString("unitofmeasure");
                            unit = object.getString("unit");
                            startDate = object.getString("startDate");
                            endDate = object.getString("endDate");
                            description = object.getString("description");
                            status123 = object.getString("status");
                            quaterid = object.getString("quarterID");
                            id = object.getString("id");
                            achivedunits = object.getString("achieveUnits");
                            managerid = object.getString("managerId");
                            managername = object.getString("managerName");
                            costyear = object.getString("costEstimationYear");
                            programQuqterUnitMappingID = object.getString("programQuqterUnitMappingID");


                            if (status123.equals("2")) {
                                statusvalue = "InProgress";
                            } else if (status123.equals("3")) {
                                statusvalue = "Completed";
                            } else if (status123.equals("4")) {
                                statusvalue = "Overdue";
                            } else {
                                statusvalue = "NotStarted";
                            }

                            if (quaterid.equals("1")) {
                                subactivityDetails = new QuarterAct.SubActivityDetails(activityname, UoM, unit, startDate, endDate, description, statusvalue, quaterid, id, achivedunits, managerid, managername, costyear, programQuqterUnitMappingID);
                                subacttivitydata.add(subactivityDetails);
                            } else if (quaterid.equals("2")) {
                                subactivityDetails = new QuarterAct.SubActivityDetails(activityname, UoM, unit, startDate, endDate, description, statusvalue, quaterid, id, achivedunits, managerid, managername, costyear, programQuqterUnitMappingID);
                                subacttivitydataq2.add(subactivityDetails);
                            } else if (quaterid.equals("3")) {
                                subactivityDetails = new QuarterAct.SubActivityDetails(activityname, UoM, unit, startDate, endDate, description, statusvalue, quaterid, id, achivedunits, managerid, managername, costyear, programQuqterUnitMappingID);
                                subacttivitydataq3.add(subactivityDetails);
                            } else if (quaterid.equals("4")) {
                                subactivityDetails = new QuarterAct.SubActivityDetails(activityname, UoM, unit, startDate, endDate, description, statusvalue, quaterid, id, achivedunits, managerid, managername, costyear, programQuqterUnitMappingID);
                                subacttivitydataq4.add(subactivityDetails);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(DailogAct.this, "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
                }

            int pos = Integer.parseInt(subactivityselectedpos);
            if (selectedquater.equals("1")) {
                quom = subacttivitydata.get(pos).getUoM().toString();
                qunit = subacttivitydata.get(pos).getUnit().toString();
                qachieved_units = subacttivitydata.get(pos).getAchivedunits().toString();
                qstatus = subacttivitydata.get(pos).getStatus().toString();
                qstrtdate = subacttivitydata.get(pos).getStartDate().toString();
                qeddate = subacttivitydata.get(pos).getEndDate().toString();
                qdesc = subacttivitydata.get(pos).getDescription().toString();
            } else if (selectedquater.equals("2")) {
                quom = subacttivitydataq2.get(pos).getUoM().toString();
                qunit = subacttivitydataq2.get(pos).getUnit().toString();
                qachieved_units = subacttivitydataq2.get(pos).getAchivedunits().toString();
                qstatus = subacttivitydataq2.get(pos).getStatus().toString();
                qstrtdate = subacttivitydataq2.get(pos).getStartDate().toString();
                qeddate = subacttivitydataq2.get(pos).getEndDate().toString();
                qdesc = subacttivitydataq2.get(pos).getDescription().toString();
            } else if (selectedquater.equals("3")) {
                quom = subacttivitydataq3.get(pos).getUoM().toString();
                qunit = subacttivitydataq3.get(pos).getUnit().toString();
                qachieved_units = subacttivitydataq3.get(pos).getAchivedunits().toString();
                qstatus = subacttivitydataq3.get(pos).getStatus().toString();
                qstrtdate = subacttivitydataq3.get(pos).getStartDate().toString();
                qeddate = subacttivitydataq3.get(pos).getEndDate().toString();
                qdesc = subacttivitydataq3.get(pos).getDescription().toString();
            } else if (selectedquater.equals("4")) {
                quom = subacttivitydataq4.get(pos).getUoM().toString();
                qunit = subacttivitydataq4.get(pos).getUnit().toString();
                qachieved_units =subacttivitydataq4.get(pos).getAchivedunits().toString();
                qstatus = subacttivitydataq4.get(pos).getStatus().toString();
                qstrtdate = subacttivitydataq4.get(pos).getStartDate().toString();
                qeddate =subacttivitydataq4.get(pos).getEndDate().toString();
                qdesc = subacttivitydataq4.get(pos).getDescription().toString();
            }
            tvuom.setText(quom);
            tvtargetunit.setText(qunit);
            tvachievedunit.setText(qachieved_units);
            tvstatus.setText(qstatus);
            tvstartdate.setText(convertdate(qstrtdate));
            tvenddate.setText(convertdate(qeddate));
            tvdesc.setText(qdesc);

            if (qstatus.equals("NotStarted")) {
                btnupdate.setVisibility(View.GONE);
                btnstart.setVisibility(View.VISIBLE);
            } else if (qstatus.equals("Completed") || qstatus.equals("Overdue") || qstatus.equals("InProgress")) {
                btnstart.setVisibility(View.GONE);
                btnupdate.setVisibility(View.VISIBLE);
            }
    }

}
