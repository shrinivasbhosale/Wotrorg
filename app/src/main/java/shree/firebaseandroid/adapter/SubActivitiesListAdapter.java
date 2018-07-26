package shree.firebaseandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shree.firebaseandroid.Activities.QuarterAct;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.fragments.SubActivities;

/**
 * Created by Shrinivas on 15-06-2018.
 */

public class SubActivitiesListAdapter extends BaseAdapter implements Filterable {

    private List<QuarterAct.SubActivityDetails> subactivityList = null;
    private ArrayList<QuarterAct.SubActivityDetails> subActivityDetails;
    Context mContext;
    LayoutInflater inflater;
    ValueFilter valueFilter;


    public SubActivitiesListAdapter(Context context, ArrayList<QuarterAct.SubActivityDetails> subactivitydata)
    {
        this.subActivityDetails = new ArrayList<QuarterAct.SubActivityDetails>();
        this.mContext = context;
        subActivityDetails.addAll(subactivitydata);
        this.subactivityList=subactivitydata;

    }


    public class ViewHolder {
        TextView SubActivityName;
        TextView Subactdesc;
        TextView Subactstatus;
    }

    @Override
    public int getCount() {
        return subactivityList.size();
    }

    @Override
    public Object getItem(int position) {
        return subactivityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new SubActivitiesListAdapter.ValueFilter();
        }
        return valueFilter;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final SubActivitiesListAdapter.ViewHolder holder;
        inflater= LayoutInflater.from(mContext);

        if (view == null) {
            holder = new SubActivitiesListAdapter.ViewHolder();
            view = inflater.inflate(R.layout.subactivitylistdesign, null);

            holder.SubActivityName = (TextView) view.findViewById(R.id.subactname);
            holder.Subactdesc=(TextView) view.findViewById(R.id.subactdesc);
            holder.Subactstatus=(TextView) view.findViewById(R.id.subactstatus);
            view.setTag(holder);

        } else {
            holder = (SubActivitiesListAdapter.ViewHolder) view.getTag();
        }
        holder.SubActivityName.setText(subactivityList.get(i).getSubActivityName());
        holder.Subactdesc.setText(subactivityList.get(i).getDescription());
        holder.Subactstatus.setText(subactivityList.get(i).getStatus());

        if (subactivityList.get(i).getStatus().equals("Overdue"))
        {
            holder.Subactstatus.setTextColor(Color.parseColor("#cc0000"));
        }
        else if (subactivityList.get(i).getStatus().equals("Completed"))
        {
            holder.Subactstatus.setTextColor(Color.parseColor("#008000"));
        }
        else if (subactivityList.get(i).getStatus().equals("InProgress"))
        {
            holder.Subactstatus.setTextColor(Color.parseColor("#0078ff"));
        }
        else if (subactivityList.get(i).getStatus().equals("NotStarted"))
        {
            holder.Subactstatus.setTextColor(Color.parseColor("#999900"));
        }

        return view;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<QuarterAct.SubActivityDetails> filterList = new ArrayList<QuarterAct.SubActivityDetails>();
            if (constraint != null && constraint.length() > 0) {
                //ArrayList<FragmentProjects.ProjectDetails> filterList = new ArrayList<FragmentProjects.ProjectDetails>();
                for (int i = 0; i < subActivityDetails.size(); i++) {
                    if ( (subActivityDetails.get(i).getSubActivityName().toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {


                        QuarterAct.SubActivityDetails country1 = new QuarterAct.SubActivityDetails(subActivityDetails.get(i).getSubActivityName() ,subActivityDetails.get(i).getUoM(),subActivityDetails.get(i).getUnit(),subActivityDetails.get(i).getStartDate(),subActivityDetails.get(i).getEndDate(),subActivityDetails.get(i)
                                .getDescription(), subActivityDetails.get(i).getStatus(),subActivityDetails.get(i).getCreationTime(),subActivityDetails.get(i).getId(),subActivityDetails.get(i).getAchivedunits(),subActivityDetails.get(i).getManagerid(),subActivityDetails.get(i).getManagername(),subActivityDetails.get(i).getCostyear(),subActivityDetails.get(i).getProgramQuqterUnitMappingID());

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
            subactivityList = (ArrayList<QuarterAct.SubActivityDetails>) results.values;
            notifyDataSetChanged();
        }
    }

}
