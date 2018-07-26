package shree.firebaseandroid.adapter;

import android.app.Activity;
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

import shree.firebaseandroid.R;
import shree.firebaseandroid.fragments.AssignedFragment;
import shree.firebaseandroid.fragments.CompletedFragment;
import shree.firebaseandroid.fragments.FragmentProjects;

/**
 * Created by Shrinivas on 13-06-2018.
 */

public class CompletedTaskAdapter extends BaseAdapter implements Filterable{

    private List<CompletedFragment.AllTasks> taskList = null;
    private ArrayList<CompletedFragment.AllTasks> allTasks;
    Context mContext;
    LayoutInflater inflater;
    ValueFilter valueFilter;



    public CompletedTaskAdapter(Activity context, ArrayList<CompletedFragment.AllTasks> allTask) {
        super();

        this.mContext = context;
        this.allTasks = new ArrayList<CompletedFragment.AllTasks>();
        allTasks.addAll(allTask);
        this.taskList = allTasks;
        inflater = LayoutInflater.from(mContext);


    }

    public class ViewHolder {
        TextView taskname;
        TextView taskshortdesc;
        TextView taskcreatetime;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new CompletedTaskAdapter.ValueFilter();
        }
        return valueFilter;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final CompletedTaskAdapter.ViewHolder holder;


        if (view == null) {
            holder = new CompletedTaskAdapter.ViewHolder();
            view = inflater.inflate(R.layout.tasklist, null);

            holder.taskname = (TextView) view.findViewById(R.id.taskname);
            holder.taskshortdesc = (TextView) view.findViewById(R.id.taskdesc);
            holder.taskcreatetime=(TextView) view.findViewById(R.id.taskdate);
            view.setTag(holder);

        } else {
            holder = (CompletedTaskAdapter.ViewHolder) view.getTag();
        }
        holder.taskname.setText(taskList.get(i).getSubActivityname());
        holder.taskshortdesc.setText(taskList.get(i).getDescription());
        holder.taskcreatetime.setText(taskList.get(i).getStartdate());
        return view;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<CompletedFragment.AllTasks> filterList = new ArrayList<CompletedFragment.AllTasks>();
            if (constraint != null && constraint.length() > 0) {
                for (int i = 0; i < allTasks.size(); i++) {
                    if ( (allTasks.get(i).getSubActivityname().toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {

                        CompletedFragment.AllTasks  country = new CompletedFragment.AllTasks(allTasks.get(i)
                                .getSubActivityname(),allTasks.get(i)
                                .getDescription() ,allTasks.get(i).getStatus(), allTasks.get(i)
                                .getStartdate());

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
            taskList = (ArrayList<CompletedFragment.AllTasks>) results.values;
            notifyDataSetChanged();
        }
    }

}
