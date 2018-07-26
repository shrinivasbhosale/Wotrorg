package shree.firebaseandroid.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import shree.firebaseandroid.Login;
import shree.firebaseandroid.R;
import shree.firebaseandroid.utils.SessionManager;

public class ProfileDetails extends AppCompatActivity {

    TextView tvname,tvemail,tvaddress,tvmobileno,tvrole;
    private SessionManager session;
    ImageView userimage;
    String userimagestring;
    byte[] imageBytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        getSupportActionBar().setTitle("My Profile");

        tvname=(TextView)findViewById(R.id.user_name);
        tvemail=(TextView)findViewById(R.id.user_email);
        tvaddress=(TextView)findViewById(R.id.user_address);
        tvmobileno=(TextView)findViewById(R.id.user_phone);
        tvrole=(TextView)findViewById(R.id.user_role);
        userimage=(ImageView)findViewById(R.id.userimage);

        session = new SessionManager(this);
        tvname.setText(session.getName());
        tvemail.setText(session.getEmailId());
        tvaddress.setText(session.getAddress());
        tvmobileno.setText(session.getMobile());
        tvrole.setText(session.getUserRole());
        userimagestring=session.getCreatorId();

        if(userimagestring.length()>5) {
            //decode base64 string to image
            imageBytes = Base64.decode(userimagestring, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            userimage.setImageBitmap(decodedImage);
        }


    }
}
