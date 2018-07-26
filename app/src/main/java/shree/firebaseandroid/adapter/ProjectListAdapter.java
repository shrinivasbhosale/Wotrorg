package shree.firebaseandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.fragments.FragmentProjects;
import shree.firebaseandroid.utils.ProjectManager;
import shree.firebaseandroid.utils.SessionManager;

/**
 * Created by Shrinivas on 10-01-2018.
 */

public class ProjectListAdapter extends BaseAdapter implements Filterable {

    private List<FragmentProjects.ProjectDetails> projectList = null;
    private ArrayList<FragmentProjects.ProjectDetails> projectDetails;
    Context mContext;
    LayoutInflater inflater;
    ValueFilter valueFilter;
    public static String SelectedProjectname="";
    private int[] image_res={R.drawable.it, R.drawable.ic_project_abc};

    public ProjectListAdapter(Context context, ArrayList<FragmentProjects.ProjectDetails> projectdata)
    {
        this.projectDetails = new ArrayList<FragmentProjects.ProjectDetails>();
        this.mContext = context;
        projectDetails.addAll(projectdata);
        this.projectList=projectdata;
    }

    public class ViewHolder {
        TextView ProjectName;
        TextView shortdesc;
        TextView createtime;
        ImageView imgproject;

    }

    @Override
    public int getCount() {
        return projectList.size();
    }

    @Override
    public Object getItem(int position) {
        return projectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        inflater= LayoutInflater.from(mContext);

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_project_listview, null);

            holder.ProjectName = (TextView) view.findViewById(R.id.projectname);
            holder.shortdesc = (TextView) view.findViewById(R.id.shortdesc);
            holder.createtime=(TextView) view.findViewById(R.id.createtime);
            holder.imgproject=(ImageView)view.findViewById(R.id.ivproject);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.ProjectName.setText(projectList.get(i).getProgramName());
        holder.shortdesc.setText(projectList.get(i).getShortDescription());
        holder.createtime.setText(projectList.get(i).getCreationTime());
        holder.imgproject.setImageResource(R.drawable.it);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String projectname=FragmentProjects.projectdata.get(i).getProgramName().toString();
                String projecticon="R.drawable.lnt_icon_96x96";
                FragmentProjects.projectid=FragmentProjects.projectdata.get(i).getProgramid().toString();

                SelectedProjectname=projectname;
                Intent i =new Intent(mContext, ProjectActivities.class);
                i.putExtra("project_id",FragmentProjects.projectid);
                i.putExtra("project_name", projectname);
                i.putExtra("project_icon", projecticon);
                mContext.startActivity(i);

                //Toast.makeText(mContext,"Project Clicked",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<FragmentProjects.ProjectDetails> filterList = new ArrayList<FragmentProjects.ProjectDetails>();
            if (constraint != null && constraint.length() > 0) {
                for (int i = 0; i < projectDetails.size(); i++) {
                    if ( (projectDetails.get(i).getProgramName().toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {

                        FragmentProjects.ProjectDetails country = new FragmentProjects.ProjectDetails(projectDetails.get(i)
                                .getProgramid(),projectDetails.get(i)
                                .getProgramName() , projectDetails.get(i)
                                .getShortDescription(), projectDetails.get(i)
                                .getCreationTime());

                        filterList.add(country);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            projectList = (ArrayList<FragmentProjects.ProjectDetails>) results.values;
            notifyDataSetChanged();
        }
    }
}
