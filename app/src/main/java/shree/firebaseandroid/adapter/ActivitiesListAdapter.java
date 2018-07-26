package shree.firebaseandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.fragments.FragmentProjects;

/**
 * Created by Shrinivas on 11-06-2018.
 */

public class ActivitiesListAdapter extends BaseAdapter implements Filterable {


    private List<ProjectActivities.ActivityDetails> activityList = null;
    private ArrayList<ProjectActivities.ActivityDetails> activityDetails;
    Context mContext;
    LayoutInflater inflater;
    ValueFilter valueFilter;



    public ActivitiesListAdapter(Context context, ArrayList<ProjectActivities.ActivityDetails> activitydata)
    {
        this.activityDetails = new ArrayList<ProjectActivities.ActivityDetails>();
        this.mContext = context;
        activityDetails.addAll(activitydata);
        this.activityList=activitydata;
    }


    public class ViewHolder {
        TextView ActivityName;
       // TextView createtime;
    }

    @Override
    public int getCount() {
        return activityList.size();
    }

    @Override
    public Object getItem(int position) {
        return activityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ActivitiesListAdapter.ValueFilter();
        }
        return valueFilter;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ActivitiesListAdapter.ViewHolder holder;
        inflater= LayoutInflater.from(mContext);

        if (view == null) {
            holder = new ActivitiesListAdapter.ViewHolder();
            view = inflater.inflate(R.layout.activitylistdesign, null);

            holder.ActivityName = (TextView) view.findViewById(R.id.projectname);
          //  holder.createtime=(TextView) view.findViewById(R.id.createtime);
            view.setTag(holder);

        } else {
            holder = (ActivitiesListAdapter.ViewHolder) view.getTag();
        }

        holder.ActivityName.setText(activityList.get(i).getSubActivityName());
        //holder.createtime.setText(activityList.get(i).getCreationTime());
        return view;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<ProjectActivities.ActivityDetails> filterList = new ArrayList<ProjectActivities.ActivityDetails>();
            if (constraint != null && constraint.length() > 0) {
                //ArrayList<FragmentProjects.ProjectDetails> filterList = new ArrayList<FragmentProjects.ProjectDetails>();
                for (int i = 0; i < activityDetails.size(); i++) {
                    if ( (activityDetails.get(i).getSubActivityName().toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {

                        ProjectActivities.ActivityDetails country1 = new ProjectActivities.ActivityDetails(activityDetails.get(i).getSubActivityName() ,activityDetails.get(i)
                                .getId(), activityDetails.get(i).getCreationTime(), activityDetails.get(i)
                                .getIsDeleted());

                        filterList.add(country1);
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
            activityList = (ArrayList<ProjectActivities.ActivityDetails>) results.values;
            notifyDataSetChanged();
        }
    }

}
