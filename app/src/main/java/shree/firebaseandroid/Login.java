package shree.firebaseandroid;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;

import static shree.firebaseandroid.fragments.SubActivityDetails.context;


public class Login extends Activity {

    int flag=0;
    boolean connected = false;
    EditText et_username,et_mobno;
    Button btnlogin;
    ProgressDialog pd;
    public static ArrayList<UserDetails> userdata = new ArrayList<UserDetails>();
    public static String id="", creationTime="", emailAddress="", isActive="", name="", surName, phoneNumber="", tenantId="", userName="", address="", userRole="", isDeleted="" ,locationName="", deleterUserId="", deletionTime="",lastModificationTime="", lastModifierUserId="", imagestring="";
    String uname,mnumber;
    private SessionManager session;
    UserDetails userDetails;
    public Context context;

    Boolean status=false;
    static String messageToSend,sendsms="nosms";
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        btnlogin=findViewById(R.id.btnNext);
        et_username=(EditText)findViewById(R.id.etusername);
        et_mobno=(EditText)findViewById(R.id.etmobileno);

        session = new SessionManager(this);
        context=Login.this;

        if(session.loggedin()){
            startActivity(new Intent(Login.this,Dashboard.class));
            finish();
    }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uname=et_username.getText().toString();
                mnumber=et_mobno.getText().toString();

                if(uname.equals(""))
                {
                    et_username.setError("Please Enter Username");
                }
                else if(mnumber.equals(""))
                {
                    et_mobno.setError("Please Enter Mobile no.");
                }
                else
                {
                        getUserDetails(uname, mnumber);
                   // verifyOTP(mnumber);

                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        final AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
        alertbox.setIcon(R.drawable.logo);
        alertbox.setTitle("You Want To Exit Programm");
        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // finish used for destroyed activity
                finish();
            }
        });

        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Nothing will be happened when clicked on no button
                // of Dialog
                arg0.dismiss();

            }
        });

        alertbox.show();
    }


    private void getUserDetails(final String un, final String mno) {

        final String etusername=un;
        final String etmobileno=mno;
        String tenantid="1";
        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(this);
        pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "application/json"));
        URLConnection.get(Login.this, "/LoginAndroidService/GetLoginInfoforAndroid?mobilenumber=" +etmobileno + "&name=" + etusername+"&TenantId="+tenantid, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        if(flag==0)
                        {
                        pd.hide();
                        JSONArray results = null;

                        try {
                            if (response.get("result").equals(null)) {
                                Toast.makeText(Login.this, "This user no available!", Toast.LENGTH_SHORT).show();
                            } else {
                                results = response.getJSONArray("result");
                                try {
                                    JSONObject jsonObject=results.getJSONObject(0);

                                     id=jsonObject.getString("id");
                                     name=jsonObject.getString("fullName");
                                     emailAddress=jsonObject.getString("emailAddress");
                                     phoneNumber=jsonObject.getString("phoneNumber");
                                     address=jsonObject.getString("address");
                                     userRole=jsonObject.getString("userRole");
                                     locationName=jsonObject.getString("locationName");
                                     isActive=jsonObject.getString("isActive");
                                     tenantId=jsonObject.getString("tenantId");
                                     creationTime=jsonObject.getString("creationTime");
                                     imagestring=jsonObject.getString("image");

                                     if(imagestring.length()<5) {
                                         imagestring = "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";
 }
             userDetails=new UserDetails(id,name,emailAddress,phoneNumber,address,userRole,                                locationName,isActive,tenantId,creationTime,imagestring);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }


                                if(mnumber.equals(phoneNumber))
                                {

                                    /*if(sendsms.equals("send"))
                                    {
                                        verifyOTP(phoneNumber);
                                    }

                                    if(sendsms.equals("nosms"))
                                    {
                                        sendsms="send";
                                    }*/

                                    session.setLoggedin(true);
                                    session.setMobile(userDetails.getPhoneNumber());
                                    session.setUserId(userDetails.getId());
                                    session.setEmailId(userDetails.getEmailAddress());
                                    session.setLocations(userDetails.getLocationName());
                                    session.setTenantId(userDetails.getTenantId());
                                    session.setAddress(userDetails.getAddress());
                                    session.setName(userDetails.getName());
                                    session.setCreationTime(userDetails.getCreationTime());
                                    session.setUserRole(userDetails.getUserRole());
                                    session.setCreatorId(userDetails.getCreatorUserId());
                                    startActivity();

                                }
                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        //flag++;
                        }

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("error", "onFailure : " + statusCode);
                        Toast.makeText(Login.this,"Server Not Connected!",Toast.LENGTH_SHORT).show();

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

    public class UserDetails {

        String id;
        String creationTime;
        String emailAddress;
        String isActive;
        String name;
        String surName;
        String phoneNumber;
        String tenantId;
        String userName;
        String address;
        String userRole;
        String isDeleted;
        String locationName;
        String deleterUserId;
        String deletionTime;
        String lastModificationTime;
        String lastModifierUserId;
        String creatorUserId;

        public UserDetails() {
        }

        public UserDetails(String id, String name, String emailAddress,String phoneNumber, String address, String userRole,String locationName, String isActive,  String tenantId,String creationTime,String creatorUserId) {
            this.id = id;
            this.creationTime = creationTime;
            this.emailAddress = emailAddress;
            this.isActive = isActive;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.tenantId = tenantId;
            this.address = address;
            this.userRole = userRole;
            this.locationName=locationName;
            this.creatorUserId=creatorUserId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(String creationTime) {
            this.creationTime = creationTime;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public String getCreatorUserId() {
            return creatorUserId;
        }

        public void setCreatorUserId(String creatorUserId) {
            this.creatorUserId = creatorUserId;
        }

    }


    private void verifyOTP(String contactNo) {

        // to generate random Otp
        Random rand = new Random();
        int n = rand.nextInt(9999) + 999;

        //To send OTP on Driver Mobile number
        messageToSend = "" + n;
        String number = contactNo;
         if(sendsms.equals("send")) {
         new SendSMS().execute("\n" + number + " is verification code for InTimeFleet ");


        //Take otp as input and compare it with origional
        // get prompts.xml view
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.otplayout, null);
        alertDialogBuilder.setTitle("Validate Your Number");
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.otpInput);
        final Button otpbtnok = (Button) promptsView.findViewById(R.id.btnotploginok);
        final Button otpbtncancle = (Button) promptsView.findViewById(R.id.btnotplogincancel);
        final ImageView imsuccess = (ImageView) promptsView.findViewById(R.id.imsuccess);
        final TextView tvsuccess = (TextView) promptsView.findViewById(R.id.tvotploginmsg);
        final TextView tvsuccessful = (TextView) promptsView.findViewById(R.id.tvotpsuccessful);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        otpbtnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInput.getText().toString().equals(messageToSend)) {
                    status = true;
                    otpbtnok.setVisibility(View.GONE);
                    userInput.setVisibility(View.GONE);
                    tvsuccess.setVisibility(View.GONE);

                    imsuccess.setVisibility(View.VISIBLE);
                    tvsuccessful.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this, "OTP verification successful", Toast.LENGTH_SHORT).show();
                    //checkInputFields();
                    startActivity();

                } else {
                    userInput.setError("Invalid OTP!");
                    status = false;
                }
            }
        });


        otpbtncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // show it
        alertDialog.show();
    }
    }

    public void startActivity() {

        Intent intent=new Intent(Login.this,Dashboard.class);
        startActivity(intent);
        finish();

    }

    class SendSMS extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String...mobileno) {

            String JsonResponse = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = null;
                try {
                    url = new URL("https://intimefleet.azurewebsites.net/api/services/app/Sms/sendSMS?number=" + mobileno[0] + "&text=" + messageToSend);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                int responsecode = urlConnection.getResponseCode();

                if(responsecode==500)
                {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
