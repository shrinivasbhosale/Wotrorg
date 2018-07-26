package shree.firebaseandroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import shree.firebaseandroid.fragments.FragmentProjects;
import shree.firebaseandroid.fragments.FragmentTask;
import shree.firebaseandroid.fragments.MeFragment;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


public class Dashboard extends AppCompatActivity {

    Toolbar toolbar;
    public static android.support.v4.app.FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppCenter.start(getApplication(), "d8399959-92bf-46b8-8d37-94831c0e8412",
                Analytics.class, Crashes.class);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_projects).setChecked(true);

        getSupportActionBar().setTitle("Projects");
        mFragmentManager = getSupportFragmentManager();
        openFragment(new FragmentProjects());
    }

    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction=null;

            switch (item.getItemId()) {
                case R.id.navigation_me:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.framedashboard, new MeFragment());
                    transaction.commit();
                    return true;

                case R.id.navigation_task:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.framedashboard, new FragmentTask());
                    transaction.commit();
                    getSupportActionBar().setTitle("Task");
                    return true;

                case R.id.navigation_projects:
                    getSupportActionBar().setTitle("Projects");
                    mFragmentManager = getSupportFragmentManager();
                    openFragment(new FragmentProjects());
                    return true;

            }
            return false;
        }
    };

    public static void openFragment(Fragment mFragment)
    {
        mFragmentManager.beginTransaction().replace(R.id.framedashboard, mFragment).commit();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        final AlertDialog.Builder alertbox = new AlertDialog.Builder(Dashboard.this);
        alertbox.setIcon(R.drawable.logo);
        alertbox.setTitle("You Want To Exit Programm?");
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

            }
        });

        alertbox.show();
    }
}
