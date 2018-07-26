package shree.firebaseandroid.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import shree.firebaseandroid.R;


public class Letsmeet extends AppCompatActivity {


    EditText meetingtitle,meetingdate,meetingduration,meetingplace,meetingagenda;
    Button btn_meeting_Send;

    String mtitle,mdate,mduration,mplace,magenda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letsmeet);

        meetingtitle=(EditText)findViewById(R.id.et_meeting_title);
        meetingdate=(EditText)findViewById(R.id.et_meeting_datepicker);
        meetingduration=(EditText)findViewById(R.id.et_meeting_duration);
        meetingplace=(EditText)findViewById(R.id.et_meeting_place);
        meetingagenda=(EditText)findViewById(R.id.et_meeting_agenda);
        btn_meeting_Send=(Button)findViewById(R.id.btn_send_meeting);

        btn_meeting_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getValues();
                if (mtitle.equals("")||mdate.equals("")||mduration.equals("")||mplace.equals("")||magenda.equals(""))
                {
                    Toast.makeText(Letsmeet.this,"Please fill meeting data!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Call service here.
                }
            }
        });
    }

    public void getValues()
    {
         mtitle=meetingtitle.getText().toString();
         mdate=meetingdate.getText().toString();
         mduration=meetingduration.getText().toString();
         mplace=meetingplace.getText().toString();
         magenda=meetingagenda.getText().toString();
    }
}
