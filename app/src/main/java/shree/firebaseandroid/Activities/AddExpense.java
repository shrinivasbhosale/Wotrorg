package shree.firebaseandroid.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.BuildConfig;
import shree.firebaseandroid.MainActivity;
import shree.firebaseandroid.Manifest;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.fragments.FragmentProjects;
import shree.firebaseandroid.fragments.SubActivities;
import shree.firebaseandroid.model.ChatModel;
import shree.firebaseandroid.model.MapModel;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;

import static shree.firebaseandroid.adapter.ProjectListAdapter.SelectedProjectname;
import static shree.firebaseandroid.fragments.SubActivityDetails.MONTHS;


public class AddExpense extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static int RESULT_LOAD_IMAGE = 1;
    ProgressDialog pd;
    Spinner expenseType, uom;
    List<String> typeList, uomList;
    int Flag = 0, flag1 = 0;
    String projectname = "", quaterid = "", implementationPlanCheckListID = "",sublistidpos="", subactname = "";
    EditText exptitle,expamt, expunit, expremark,expsubactname;
    TextView dateview;
    Button btnaddexpense;

    String expensesYear,expenseTitle,expensesTypeName,expenseDate,remark,image,
            unitOfMeasuresIDName,subActivityName,managerName,expimage;

    int tenantId,programID,prgActionAreaActivityMappingID,quarterId,expensesTypeID,unit,amount,
            unitOfMeasuresID,mapSubActivityIteamsToImplementionPlanID,programQuqterUnitMappingID,
            implementationPlanCheckListID2,status,managerID,creatorUserId;

    public static ArrayList<UnitsOfMeasure> unitsdata = new ArrayList<UnitsOfMeasure>();
    public static ArrayList<TypesOfExpense> Expensesdata = new ArrayList<TypesOfExpense>();
    SessionManager session;
    ImageButton btncamera,btngallary;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView clickimage;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_GALLARY_PERMISSION_CODE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        exptitle = (EditText) findViewById(R.id.etexpensetitle);
        expamt = (EditText) findViewById(R.id.etamount);
        expunit = (EditText) findViewById(R.id.etunits);
        expremark = (EditText) findViewById(R.id.etremarks);
        expsubactname=(EditText)findViewById(R.id.etexpsubactname);
        dateview = (TextView) findViewById(R.id.etdatepicker);
        btncamera=(ImageButton)findViewById(R.id.btncamera);
        btngallary=(ImageButton)findViewById(R.id.btngalary);
        btnaddexpense = (Button) findViewById(R.id.btn_send_expense);
        clickimage=(ImageView)findViewById(R.id.clickimage);
        btncamera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }

        });

        btngallary.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_GALLARY_PERMISSION_CODE);
                } else {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }

            }
        });

        pd = new ProgressDialog(AddExpense.this);
        pd.setMessage(getString(R.string.wait));

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        expenseType = findViewById(R.id.Etype);
        uom = findViewById(R.id.spnruom);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                projectname = null;
            } else {
                projectname = extras.getString("ProjectTitle");
                quaterid = extras.getString("selectedquaterid");
                implementationPlanCheckListID = extras.getString("subactid");
                subactname = extras.getString("subactname");
                sublistidpos=extras.getString("sublistidpos");
            }
        } else {
            projectname = (String) savedInstanceState.getSerializable("ProjectTitle");
        }
        setTitle("" + SelectedProjectname);
        expsubactname.setText(subactname);

        getAllUnitofMeasures();

        getAllTypesOfExpense();
        // Spinner click listener
        expenseType.setOnItemSelectedListener(this);
        uom.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        typeList = new ArrayList<String>();
        uomList = new ArrayList<String>();
        uomList.add(0, "Select Unit Of Measure");
        typeList.add(0, "Select Expense Type");

        // attaching data adapter to spinner

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        expenseType.setAdapter(dataAdapter2);
        expenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                expensesTypeID=position+1;
                expensesTypeName=item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(AddExpense.this, android.R.layout.simple_spinner_item, uomList);
        // Drop down layout style - list view with radio button
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        uom.setAdapter(dataAdapter3);

        uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                unitOfMeasuresID=position+1;
                unitOfMeasuresIDName=item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ImageButton datepicker = (ImageButton) findViewById(R.id.imgcalender);
        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpense.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        dateview.setText(Integer.toString(dayOfMonth) + " " + MONTHS[month] + " " + Integer.toString(year));
                        dateview.setTextColor(getResources().getColor(R.color.colorblack));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnaddexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int result=getallvalues();

                if(result==1) {
                   /* JSONObject imageinfo = new JSONObject();
                    JSONArray arr = new JSONArray();

                    for (int i = 0; i < 1; i++) {
                        try {
                            imageinfo.put("image", expimage);
                        } catch (Exception e) {
                        }
                        arr.put(imageinfo);
                    }*/

                    JSONObject main = new JSONObject();
                    try {

                        JSONObject imageinfo = new JSONObject();
                        JSONArray arr = new JSONArray();

                        for (int i = 0; i < 1; i++) {
                            try {
                                imageinfo.put("image", expimage);
                            } catch (Exception e) {
                            }
                            arr.put(imageinfo);
                        }

                        main.put("tenantId", tenantId);
                        main.put("programID", programID);
                        main.put("expensesYear", "2018-2019");
                        main.put("prgActionAreaActivityMappingID", prgActionAreaActivityMappingID);
                        main.put("quarterId", quarterId);
                        main.put("expenseTitle", expenseTitle);
                        main.put("expensesTypeID", expensesTypeID);
                        main.put("expensesTypeName", "string");
                        main.put("expenseDate", expenseDate);
                        main.put("remark", "string");
                        main.put("unit", unit);
                        main.put("amount", amount);
                        main.put("image", "string");
                        main.put("unitOfMeasuresID", unitOfMeasuresID);
                        main.put("unitOfMeasuresIDName", unitOfMeasuresIDName);
                        main.put("subActivityName", subActivityName);
                        main.put("mapSubActivityIteamsToImplementionPlanID", mapSubActivityIteamsToImplementionPlanID);
                        main.put("programQuqterUnitMappingID", programQuqterUnitMappingID);
                        main.put("implementationPlanCheckListID", implementationPlanCheckListID2);
                        main.put("status", status);
                        main.put("managerID", managerID);
                        main.put("managerName", managerName);
                        main.put("expensesimage", arr);
                        main.put("creatorUserId", creatorUserId);
                    } catch (Exception e) {
                    }

                    new CreateNewExpense().execute(String.valueOf(main));
                }

            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            clickimage.setImageBitmap(photo);

            expimage = encodeFromString(photo);
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            clickimage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Bitmap bitmap = ((BitmapDrawable)clickimage.getDrawable()).getBitmap();
            expimage=encodeFromString(bitmap);
        }
    }

    public static String encodeFromString(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private int getallvalues() {

        int pos=Integer.parseInt(sublistidpos);

        if(quaterid.equals("1")) {
            expensesYear = QuarterAct.subacttivitydata.get(pos).getCostyear().toString();
            programQuqterUnitMappingID = Integer.parseInt(QuarterAct.subacttivitydata.get(pos).getProgramQuqterUnitMappingID());
            status = getstatusvalue(QuarterAct.subacttivitydata.get(pos).getStatus().toString());
            managerID = Integer.parseInt(QuarterAct.subacttivitydata.get(pos).getManagerid().toString());
            managerName = QuarterAct.subacttivitydata.get(pos).getManagername().toString();
        }
        else if(quaterid.equals("2")){
            expensesYear = QuarterAct.subacttivitydataq2.get(pos).getCostyear().toString();
            programQuqterUnitMappingID = Integer.parseInt(QuarterAct.subacttivitydataq2.get(pos).getProgramQuqterUnitMappingID());
            status = getstatusvalue(QuarterAct.subacttivitydataq2.get(pos).getStatus().toString());
            managerID = Integer.parseInt(QuarterAct.subacttivitydataq2.get(pos).getManagerid().toString());
            managerName = QuarterAct.subacttivitydataq2.get(pos).getManagername().toString();
        }
        else if(quaterid.equals("3"))
        {
            expensesYear = QuarterAct.subacttivitydataq3.get(pos).getCostyear().toString();
            programQuqterUnitMappingID =Integer.parseInt(QuarterAct.subacttivitydataq3.get(pos).getProgramQuqterUnitMappingID());
            status = getstatusvalue(QuarterAct.subacttivitydataq3.get(pos).getStatus().toString());
            managerID = Integer.parseInt(QuarterAct.subacttivitydataq3.get(pos).getManagerid().toString());
            managerName = QuarterAct.subacttivitydataq3.get(pos).getManagername().toString();
        }
        else if(quaterid.equals("4"))
        {
            expensesYear = QuarterAct.subacttivitydataq4.get(pos).getCostyear().toString();
            programQuqterUnitMappingID =Integer.parseInt(QuarterAct.subacttivitydataq4.get(pos).getProgramQuqterUnitMappingID());
            status = getstatusvalue(QuarterAct.subacttivitydataq4.get(pos).getStatus().toString());
            managerID =Integer.parseInt( QuarterAct.subacttivitydataq4.get(pos).getManagerid().toString());
            managerName = QuarterAct.subacttivitydataq4.get(pos).getManagername().toString();
        }

         tenantId = 1;
         programID = Integer.parseInt(FragmentProjects.projectid);
         prgActionAreaActivityMappingID = Integer.parseInt(ProjectActivities.programactionareaid);
         quarterId = Integer.parseInt(quaterid);
         if(exptitle.getText().toString().equals(""))
         {
             exptitle.setError("Please add title!");
         }
         else if(expamt.getText().toString().equals(""))
         {
             expamt.setError("Please add amount!");
         }
         else if(expunit.getText().toString().equals(""))
         {
             expunit.setError("Please add unit!");
         }
         else if(expremark.getText().toString().equals(""))
         {
             expremark.setError("Please add Remark!");
         }
         else
         {
             expenseTitle = exptitle.getText().toString();
             expenseDate = dateview.getText().toString();
             remark = expremark.getText().toString();
             amount = Integer.parseInt(expamt.getText().toString());
             unit = Integer.parseInt(expunit.getText().toString());
             image = "";
             subActivityName = subactname.toString();
             mapSubActivityIteamsToImplementionPlanID =0;
             implementationPlanCheckListID2 = Integer.parseInt(implementationPlanCheckListID);
             session=new SessionManager(AddExpense.this);
             creatorUserId=Integer.parseInt(session.getUserId());
             return 1;
         }

         return 0;

    }

    public int getstatusvalue(String statusstring)
    {
        if(statusstring.equals("Completed"))
        {
            return 3;
        }
        else if(statusstring.equals("Overdue"))
        {
            return 4;
        }
        else if(statusstring.equals("InProgress"))
        {
            return 2;
        }
        else
        {
            return 1;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getAllUnitofMeasures() {

        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(this);
        // pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "application/json"));
        URLConnection.get(AddExpense.this, "/LoginAndroidService/GetAllUnitOfMeasureforAndroid?TenantId=" + 1, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        pd.hide();
                        JSONArray results = null;
                        String id, unitname;
                        try {
                            results = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (flag1 == 0) {
                            unitsdata.clear();
                            if (results.length() > 0) {
                                for (int i = 0; i < results.length(); i++) {
                                    try {
                                        JSONObject object = results.getJSONObject(i);
                                        id = object.getString("id");
                                        unitname = object.getString("name");
                                        UnitsOfMeasure unitsOfMeasure = new UnitsOfMeasure(id, unitname);
                                        unitsdata.add(unitsOfMeasure);
                                        flag1 = 1;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(AddExpense.this, "Project is not assinged yet", Toast.LENGTH_SHORT).show();
                            }
                            if (unitsdata.size() > 0) {

                                uomList.clear();
                                for (int i = 0; i < unitsdata.size(); i++) {
                                    uomList.add(unitsdata.get(i).getUnitsname());
                                }


                            }
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

    public class UnitsOfMeasure {

        String id;
        String unitsname;

        public UnitsOfMeasure() {
        }

        public UnitsOfMeasure(String id, String unitsname) {
            this.id = id;
            this.unitsname = unitsname;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUnitsname() {
            return unitsname;
        }

        public void setUnitsname(String unitsname) {
            this.unitsname = unitsname;
        }
    }

    private void getAllTypesOfExpense() {

        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(this);
        pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "application/json"));
        URLConnection.get(AddExpense.this, "/LoginAndroidService/GetAllExpensesTypeforAndroid?TenantId=" + 1, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        pd.hide();
                        JSONArray results = null;
                        String id, Tid, expensename, programid;
                        try {

                            results = response.getJSONArray("result");

                            if (Flag == 0) {
                                Expensesdata.clear();
                                if (results.length() > 0) {
                                    for (int i = 0; i < results.length(); i++) {

                                        JSONObject object = results.getJSONObject(i);
                                        Tid = object.getString("tenantId");
                                        expensename = object.getString("typeName");
                                        id = object.getString("id");
                                        programid = object.getString("programId");
                                        TypesOfExpense typesOfExpense = new TypesOfExpense(id, expensename, Tid, programid);
                                        Expensesdata.add(typesOfExpense);
                                        Flag = 1;
                                    }

                                    if (Expensesdata.size() > 0) {

                                        typeList.clear();
                                        for (int i = 0; i < Expensesdata.size(); i++) {
                                            typeList.add(Expensesdata.get(i).getExpensename());
                                        }
                                    }

                                } else {
                                    Toast.makeText(AddExpense.this, "Project is not assinged yet", Toast.LENGTH_SHORT).show();
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

    public class TypesOfExpense {

        String id;
        String expensename;
        String tenantid;
        String programid;

        public TypesOfExpense() {
        }

        public TypesOfExpense(String id, String expensename, String tenantid, String programid) {
            this.id = id;
            this.expensename = expensename;
            this.tenantid = tenantid;
            this.programid = programid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getExpensename() {
            return expensename;
        }

        public void setExpensename(String expensename) {
            this.expensename = expensename;
        }

        public String getTenantid() {
            return tenantid;
        }

        public void setTenantid(String tenantid) {
            this.tenantid = tenantid;
        }

        public String getProgramid() {
            return programid;
        }

        public void setProgramid(String programid) {
            this.programid = programid;
        }
    }

    class CreateNewExpense extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String JsonResponse) {
            JSONObject obj=null;
            try {

                obj = new JSONObject(JsonResponse);

                Log.d("My App", obj.toString());

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + JsonResponse + "\"");
            }

            try {
                if(obj.getString("result").equals("SAVE")) {
                    pd.hide();

                   /* new AwesomeSuccessDialog(this)
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.app_name)
                            .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                            .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                            .setCancelable(true)
                            .setPositiveButtonText(getString(R.string.dialog_yes_button))
                            .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                            .setPositiveButtonTextColor(R.color.white)
                            .setNegativeButtonText(getString(R.string.dialog_no_button))
                            .setNegativeButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                            .setNegativeButtonTextColor(R.color.white)
                            .setPositiveButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    //click
                                }
                            })
                            .setNegativeButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    //click
                                }
                            })
                            .show();*/
                   finish();
                    Toast.makeText(AddExpense.this, "Expense Submited.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(AddExpense.this,"Expense Not Submited !",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://wotrqa.azurewebsites.net/api/services/app/LoginAndroidService/CreateORUpdateExpensesForAndroid");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
//response data
                Log.i("TAG",JsonResponse);

//send to post execute
                return JsonResponse;

               // return null;

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAG", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.show();
        }
    }
}
