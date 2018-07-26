package shree.firebaseandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import shree.firebaseandroid.utils.SessionManager;

public class Splashscreen extends Activity {

    private static int SPLASH_TIME_OUT=1500;
    private SessionManager session;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        context=getApplicationContext();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                    Intent i = new Intent(Splashscreen.this, Login.class);
                    startActivity(i);
                    finish();
            }
        },SPLASH_TIME_OUT);

    }
}
