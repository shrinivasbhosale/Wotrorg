package shree.firebaseandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import shree.firebaseandroid.R;
import shree.firebaseandroid.fragments.AssignedFragment;
import shree.firebaseandroid.fragments.CompletedFragment;
import shree.firebaseandroid.fragments.FragmentTask;

/**
 * Created by Shrinivas on 22-03-2018.
 */

public class TaskAdapter extends BaseAdapter {

    private List<AssignedFragment.AllTasks> taskList = null;
    private ArrayList<AssignedFragment.AllTasks> allTasks;
    Context mContext;
    LayoutInflater inflater;


    public TaskAdapter(Activity context, ArrayList<AssignedFragment.AllTasks> allTask) {
        super();

            this.mContext = context;
            this.allTasks = new ArrayList<AssignedFragment.AllTasks>();
            this.mContext = context;
            allTasks.addAll(allTask);
            this.taskList = allTasks;

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
    public View getView(int i, View view, ViewGroup viewGroup) {

        final TaskAdapter.ViewHolder holder;
        inflater = LayoutInflater.from(mContext);
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.tasklist, null);

            holder.taskname = (TextView) view.findViewById(R.id.taskname);
            holder.taskshortdesc = (TextView) view.findViewById(R.id.taskdesc);
            holder.taskcreatetime=(TextView) view.findViewById(R.id.taskdate);
            view.setTag(holder);

        } else {
            holder = (TaskAdapter.ViewHolder) view.getTag();
        }
        holder.taskname.setText(taskList.get(i).getSubActivityname());
        holder.taskshortdesc.setText(taskList.get(i).getDescription());
        holder.taskcreatetime.setText(taskList.get(i).getStartdate());
        return view;
    }

}
