package shree.firebaseandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;

import shree.firebaseandroid.Activities.ViewExpense;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.fragments.FragmentProjects;

/**
 * Created by Shrinivas on 04-07-2018.
 */

public class ViewExpenseAdapter extends BaseAdapter{

    private List<ViewExpense.ViewExpenseDetails> expenseList = null;
    ArrayList<ViewExpense.ViewExpenseDetails> viewExpensedata1;
    Context mContext;
    LayoutInflater inflater;
    byte[] imageBytes;

    public ViewExpenseAdapter(Context context, ArrayList<ViewExpense.ViewExpenseDetails> viewExpensedata) {
        this.viewExpensedata1 = new ArrayList<ViewExpense.ViewExpenseDetails>();
        this.mContext = context;
        viewExpensedata1.addAll(viewExpensedata);
        this.expenseList=viewExpensedata;
    }

    public class ViewHolder {
        TextView expensetitle1;
        TextView expensedate1;
        TextView expenseamt1;
        ImageView imgexpimage;
    }

    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int i, View view, ViewGroup parent) {
        final ViewHolder holder;
        inflater= LayoutInflater.from(mContext);

        if (view == null) {
            holder = new ViewExpenseAdapter.ViewHolder();
            view = inflater.inflate(R.layout.viewexpenselist, null);

            holder.expensetitle1 = (TextView) view.findViewById(R.id.expensetitle);
            holder.expensedate1 = (TextView) view.findViewById(R.id.expensedate);
            holder.expenseamt1=(TextView) view.findViewById(R.id.expenseamount);
            holder.imgexpimage=(ImageView)view.findViewById(R.id.lvexpimage);
            view.setTag(holder);

        } else {
            holder = (ViewExpenseAdapter.ViewHolder) view.getTag();
        }

        holder.expensetitle1.setText(expenseList.get(i).getExpTitle().toString());
        holder.expensedate1.setText("Date: "+expenseList.get(i).getExpdate().toString());
        holder.expenseamt1.setText("Rs."+expenseList.get(i).getExpamount().toString());
        String image=expenseList.get(i).getEximage().toString();

        if(image.equals(null)||image.equals("")||image==null) {
            holder.imgexpimage.setImageResource(R.drawable.it);
        }
        else
        {
                //decode base64 string to image
                imageBytes = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.imgexpimage.setImageBitmap(decodedImage);

        }
        return view

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String projectname=FragmentProjects.projectdata.get(i).getProgramName().toString();
                String projecticon="R.drawable.lnt_icon_96x96";
                FragmentProjects.projectid=FragmentProjects.projectdata.get(i).getProgramid().toString();

                Intent i =new Intent(mContext, ProjectActivities.class);
                i.putExtra("project_id",FragmentProjects.projectid);
                i.putExtra("project_name", projectname);
                i.putExtra("project_icon", projecticon);
                mContext.startActivity(i);

                //Toast.makeText(mContext,"Project Clicked",Toast.LENGTH_SHORT).show();
            }
        });*/
        ;
    }
}
