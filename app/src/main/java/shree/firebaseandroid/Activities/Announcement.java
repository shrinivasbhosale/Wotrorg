package shree.firebaseandroid.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import shree.firebaseandroid.R;


public class Announcement extends AppCompatActivity {


    EditText anmtTitle,anmtDesc;
    Button sendAnmt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        anmtTitle=(EditText)findViewById(R.id.et_anmt_title);
        anmtDesc=(EditText)findViewById(R.id.et_anmt_desc);
        sendAnmt=(Button)findViewById(R.id.btn_send_announcement);

        sendAnmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String anmt_title=anmtTitle.getText().toString();
                String anmt_desc=anmtDesc.getText().toString();

                if(anmt_title.equals(""))
                {
                    anmtTitle.setError("Please Enter Values!");
                }
                else if(anmt_desc.equals(""))
                {
                    anmtDesc.setError("Please Enter Desc!");
                }
                else
                {
                    //Call service here
                }
            }
        });

    }
}
