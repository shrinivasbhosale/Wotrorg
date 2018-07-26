package shree.firebaseandroid.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import shree.firebaseandroid.Dashboard;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.adapter.ProjectListAdapter;
import shree.firebaseandroid.adapter.ViewExpenseAdapter;
import shree.firebaseandroid.fragments.FragmentProjects;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;

import static java.security.AccessController.getContext;
import static shree.firebaseandroid.Login.creationTime;
import static shree.firebaseandroid.fragments.FragmentProjects.projectDetails;
import static shree.firebaseandroid.fragments.SubActivityDetails.context;

public class ViewExpense extends AppCompatActivity {

    ListView listViewExpense;
    String projectname="",userid="",tenantid="1",programid="",quaterid="",prgActionAreaActivityMappingID="",implementationPlanCheckListID="";
    public SessionManager session;
    public Context context;
    ProgressDialog pd;
    public static ArrayList<ViewExpense.ViewExpenseDetails> viewExpensedata = new ArrayList<ViewExpense.ViewExpenseDetails>();
    public static ViewExpense.ViewExpenseDetails viewExpenseDetails;
    public static ViewExpenseAdapter ViewExpenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listViewExpense = (ListView) findViewById(R.id.expenselistview);
        context=getApplicationContext();
        session = new SessionManager(context);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                projectname= null;
            } else {
                projectname= extras.getString("ProjectTitle");
                quaterid=extras.getString("selectedquaterid");
                implementationPlanCheckListID=extras.getString("subactid");
            }
        } else {
            projectname= (String) savedInstanceState.getSerializable("ProjectTitle");
        }
        setTitle(""+projectname);


        tenantid="1";
        programid= ProjectActivities.projectid;
        prgActionAreaActivityMappingID=ProjectActivities.programactionareaid;
        userid=session.getUserId().toString();


        getallexpenses(tenantid,programid,prgActionAreaActivityMappingID,quaterid,implementationPlanCheckListID,userid);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }


    private void getallexpenses(String tenantid, String programid, String prgActionAreaActivityMappingID, String quaterid, String implementationPlanCheckListID, String userid) {

        List<Header> headers = new ArrayList<Header>();


        pd = new ProgressDialog(ViewExpense.this);
        //pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(this, "/LoginAndroidService/GetExpenseDetails?TenantId="+tenantid+"&ProgramID="+programid+"&PrgActionAreaActivityMappingID="+prgActionAreaActivityMappingID+"&QuarterId="+quaterid+"&ImplementationPlanCheckListID="+implementationPlanCheckListID+"&CreatorUserId="+userid, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        JSONArray results = null;
                        JSONArray imagearray = null;
                        String expyear, expTitle,exptypeid,exptypename,expdate,expremark,expunit,expamount,expuomid,expuomidname,expsubActivityName,expstatus,expmanagerID,expmanagerName;
                        String image="";
                        try {
                            results = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewExpensedata.clear();
                        if (results.length() > 0) {
                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject object = results.getJSONObject(i);
                                    expyear=object.getString("expensesYear");
                                    expTitle = object.getString("expenseTitle");
                                    exptypeid = object.getString("expensesTypeID");
                                    exptypename = object.getString("expensesTypeName");
                                    expdate=object.getString("expenseDate");

                                    expremark = object.getString("remark");
                                    expunit = object.getString("unit");
                                    expamount=object.getString("amount");
                                    expuomid = object.getString("unitOfMeasuresID");
                                    expuomidname = object.getString("unitOfMeasuresIDName");
                                    expsubActivityName=object.getString("subActivityName");
                                    expstatus=object.getString("status");
                                    expmanagerID=object.getString("managerID");
                                    expmanagerName=object.getString("managerName");

                                    imagearray =object.getJSONArray("expensesimage");
                                    if(imagearray.length()>0)
                                    {
                                        for (int j = 0; j < imagearray.length(); j++) {

                                            JSONObject arrayobject=imagearray.getJSONObject(j);
                                            image=arrayobject.getString("image");
                                        }

                                    }

                                    String expensedate=getdateinformat(expdate);

                                    viewExpenseDetails = new ViewExpenseDetails(expyear, expTitle,exptypeid,exptypename,expensedate,expremark,expunit,expamount,expuomid,expuomidname,expsubActivityName,expstatus,expmanagerID,expmanagerName,image);
                                    viewExpensedata.add(viewExpenseDetails);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(ViewExpense.this, "Expenses Not Available!", Toast.LENGTH_SHORT).show();
                        }
                        if (viewExpensedata.size() > 0) {
                            ViewExpenseAdapter = new ViewExpenseAdapter(ViewExpense.this, viewExpensedata);
                            listViewExpense.setAdapter(ViewExpenseAdapter);
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
                        if(pd.isShowing())
                        {
                            pd.hide();
                        }
                    }
                });
    }

    private String getdateinformat(String expdate) {

        String date1=expdate;
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


    public static class ViewExpenseDetails {

        String expyear, expTitle,exptypeid,exptypename,expdate,expremark,expunit,expamount,expuomid,expuomidname,expsubActivityName,expstatus,expmanagerID,expmanagerName;
        String eximage;
        public ViewExpenseDetails() {
        }

        public ViewExpenseDetails(String expyear, String expTitle, String exptypeid, String exptypename, String expdate, String expremark, String expunit, String expamount, String expuomid, String expuomidname, String expsubActivityName, String expstatus, String expmanagerID, String expmanagerName,String expimage) {
            this.expyear = expyear;
            this.expTitle = expTitle;
            this.exptypeid = exptypeid;
            this.exptypename = exptypename;
            this.expdate = expdate;
            this.expremark = expremark;
            this.expunit = expunit;
            this.expamount = expamount;
            this.expuomid = expuomid;
            this.expuomidname = expuomidname;
            this.expsubActivityName = expsubActivityName;
            this.expstatus = expstatus;
            this.expmanagerID = expmanagerID;
            this.expmanagerName = expmanagerName;
            this.eximage=expimage;
        }

        public String getEximage() {
            return eximage;
        }

        public void setEximage(String eximage) {
            this.eximage = eximage;
        }

        public String getExpyear() {
            return expyear;
        }

        public void setExpyear(String expyear) {
            this.expyear = expyear;
        }

        public String getExpTitle() {
            return expTitle;
        }

        public void setExpTitle(String expTitle) {
            this.expTitle = expTitle;
        }

        public String getExptypeid() {
            return exptypeid;
        }

        public void setExptypeid(String exptypeid) {
            this.exptypeid = exptypeid;
        }

        public String getExptypename() {
            return exptypename;
        }

        public void setExptypename(String exptypename) {
            this.exptypename = exptypename;
        }

        public String getExpdate() {
            return expdate;
        }

        public void setExpdate(String expdate) {
            this.expdate = expdate;
        }

        public String getExpremark() {
            return expremark;
        }

        public void setExpremark(String expremark) {
            this.expremark = expremark;
        }

        public String getExpunit() {
            return expunit;
        }

        public void setExpunit(String expunit) {
            this.expunit = expunit;
        }

        public String getExpamount() {
            return expamount;
        }

        public void setExpamount(String expamount) {
            this.expamount = expamount;
        }

        public String getExpuomid() {
            return expuomid;
        }

        public void setExpuomid(String expuomid) {
            this.expuomid = expuomid;
        }

        public String getExpuomidname() {
            return expuomidname;
        }

        public void setExpuomidname(String expuomidname) {
            this.expuomidname = expuomidname;
        }

        public String getExpsubActivityName() {
            return expsubActivityName;
        }

        public void setExpsubActivityName(String expsubActivityName) {
            this.expsubActivityName = expsubActivityName;
        }

        public String getExpstatus() {
            return expstatus;
        }

        public void setExpstatus(String expstatus) {
            this.expstatus = expstatus;
        }

        public String getExpmanagerID() {
            return expmanagerID;
        }

        public void setExpmanagerID(String expmanagerID) {
            this.expmanagerID = expmanagerID;
        }

        public String getExpmanagerName() {
            return expmanagerName;
        }

        public void setExpmanagerName(String expmanagerName) {
            this.expmanagerName = expmanagerName;
        }
    }
}
