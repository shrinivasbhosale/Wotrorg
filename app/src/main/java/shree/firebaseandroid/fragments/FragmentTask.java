package shree.firebaseandroid.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import shree.firebaseandroid.adapter.CompletedTaskAdapter;
import shree.firebaseandroid.adapter.ProjectListAdapter;
import shree.firebaseandroid.adapter.TaskAdapter;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.ViewPagerAdapter;

import static shree.firebaseandroid.fragments.CompletedFragment.taskAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTask extends android.support.v4.app.Fragment {

    View view;

    //This is our tablayout used show all task related tabs.
    private TabLayout tabLayout;
    //This is our viewPager used to swap between tabs of task.
    private ViewPager viewPager;
    //Fragments
    AssignedFragment assignedFragment;
    CompletedFragment completedFragment;
    TodayFragment todayFragment;
    FragmentTransaction transaction=null;
    public static int fragment=0;
    public SessionManager session;
    ProgressDialog pd;
    public static ArrayList<FragmentTask.AllTasks> allTasks = new ArrayList<FragmentTask.AllTasks>();
    public static FragmentTask.AllTasks taskdetails;

    public FragmentTask() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_task, container, false);
        setHasOptionsMenu(true);

        session = new SessionManager(getActivity());
        //Initializing viewPager
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        //Initializing the tablayout
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);
                if(position==0)
                {
                    fragment=1;
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.listxyz, new CompletedFragment());
                    transaction.commit();
                }
                else if(position==1)
                {
                    fragment=2;
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.listxyz, new AssignedFragment());
                    transaction.commit();
                }
                else if(position==2)
                {
                    fragment=3;
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.listxyz, new TodayFragment());
                    transaction.commit();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setupViewPager(viewPager);
        fragment=1;
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#01A1FF"));
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.listxyz, new CompletedFragment());
        transaction.commit();

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {

        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.projects_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if(completedFragment.allTasks.contains(query)){
                    taskAdapter.getFilter().filter(query);
                }else{
                    Toast.makeText(getActivity(), "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0)
                {
                    //getAllTasks(session.getUserId().toString());
                    taskAdapter= new CompletedTaskAdapter(getActivity(),CompletedFragment.allTasks);
                    CompletedFragment.tasklist.setAdapter(taskAdapter);
                }
                else
                {
                    taskAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        completedFragment =new CompletedFragment();
        assignedFragment =new AssignedFragment();
        todayFragment =new TodayFragment();
        adapter.addFragment(completedFragment,"Completed");
        adapter.addFragment(assignedFragment,"Assigned");
        adapter.addFragment(todayFragment,"Today");
        viewPager.setAdapter(adapter);
    }



    private void getAllTasks(String s) {

        List<Header> headers = new ArrayList<Header>();

        pd = new ProgressDialog(getActivity());
        pd.setTitle(getString(R.string.connecting));
        pd.setMessage(getString(R.string.wait));

        headers.add(new BasicHeader("Accept", "text/plain"));
        URLConnection.get(getActivity(), "/LoginAndroidService/GetAllTask?userId=" + s, headers.toArray(new Header[headers.size()]),
                null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        pd.hide();
                        JSONArray results = null;
                        String subActivityname, description,status, startdate;
                        try {
                            results = response.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        allTasks.clear();
                        if (results.length() > 0) {
                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject object = results.getJSONObject(i);
                                    subActivityname = object.getString("subActivityName");
                                    description = object.getString("description");
                                    status = object.getString("status");
                                    startdate = object.getString("startDate");

                                    taskdetails = new AllTasks(subActivityname,description,status,startdate);
                                    allTasks.add(taskdetails);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Project is not assinged yet", Toast.LENGTH_SHORT).show();
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


    public class AllTasks {

        String subActivityname, description,status, startdate;

        public AllTasks() {
        }

        public AllTasks(String subActivityname, String description, String status, String startdate) {
            this.subActivityname = subActivityname;
            this.description = description;
            this.status = status;
            this.startdate = startdate;
        }

        public String getSubActivityname() {
            return subActivityname;
        }

        public void setSubActivityname(String subActivityname) {
            this.subActivityname = subActivityname;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStartdate() {
            return startdate;
        }

        public void setStartdate(String startdate) {
            this.startdate = startdate;
        }
    }
}
