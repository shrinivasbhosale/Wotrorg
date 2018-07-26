package shree.firebaseandroid.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import shree.firebaseandroid.R;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubActivityDetails extends Fragment {

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    ProgressDialog pd;
    public SubActivityDetails() {
        // Required empty public constructor
    }

    View view;
    TextView tv,subactivityname,unitofmeasure,units,startdate,enddate,description;

    RelativeLayout subtaskinfo;
    public static Context context;
    AlertDialog alertDialog;
    String title,subactivitytitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
               view= inflater.inflate(R.layout.fragment_sub_action, container, false);

               tv=view.findViewById(R.id.tv);
               subactivityname=view.findViewById(R.id.subacttitle);

               Bundle arguments=getArguments();
               title=arguments.getString("key");
               subactivitytitle=arguments.getString("title");

               tv.setText(title);
               subactivityname.setText(subactivitytitle);

                subtaskinfo=(RelativeLayout)view.findViewById(R.id.rlsubtaskinfo);

                /*unitofmeasure=(TextView)view.findViewById(R.id.uom);
                units=(TextView)view.findViewById(R.id.unitquantity);
                startdate=(TextView)view.findViewById(R.id.startdate);
                enddate=(TextView)view.findViewById(R.id.enddate);
                description=(TextView)view.findViewById(R.id.descinfo);*/




                subtaskinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                        alertDialogBuilder.setCancelable(false);
                        LayoutInflater li = LayoutInflater.from(getActivity());
                        View promptsView = li.inflate(R.layout.otp_prompts, null);
                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);


                        final Button otpbtnok = (Button) promptsView.findViewById(R.id.btncomplete);
                        final Button otpbtncancle = (Button) promptsView.findViewById(R.id.btnupdate);

                        // create alert dialog
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.setTitle(""+title);

                        otpbtnok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                updateStatus();

                               //  alertDialog.dismiss();
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
                });

        return view;
    }

    private void updateStatus() {

        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(getActivity());
        pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));
        String tasklistid = "2";
        String status="4";

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(getActivity(), "/LoginAndroidService/UpdateStatusOfSubActivityForAndroid?ImplementationPlanCheckListID=" +tasklistid+ "&status=" + status, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        pd.hide();
                        JSONArray results = null;
                        String activityname,UoM,unit,startDate,endDate,description,status,creationTime,id;
                        try {
                            results = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (results.length() > 0) {
                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject object = results.getJSONObject(i);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Sub Activities are not assinged yet", Toast.LENGTH_SHORT).show();
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
}
