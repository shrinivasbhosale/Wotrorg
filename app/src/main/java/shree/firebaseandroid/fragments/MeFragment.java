package shree.firebaseandroid.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import shree.firebaseandroid.Dashboard;
import shree.firebaseandroid.Login;
import shree.firebaseandroid.ProjectActivities;
import shree.firebaseandroid.R;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.view.ProfileDetails;
/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    View view;
    TextView username;
    LinearLayout profile,location,setting,myactivity;
    ImageView userimage;
    private SessionManager session;
    byte[] imageBytes;
    String imageString;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_me, container, false);
        username=(TextView)view.findViewById(R.id.profile_username);
        profile=(LinearLayout)view.findViewById(R.id.myprofile);
        location=(LinearLayout)view.findViewById(R.id.loutlocation);
        setting=(LinearLayout)view.findViewById(R.id.loutsetting);
        myactivity=(LinearLayout)view.findViewById(R.id.llmyactivity);
        userimage=(ImageView)view.findViewById(R.id.imguser);


        session = new SessionManager(getActivity());
        username.setText(session.getName());
        imageString=session.getCreatorId();

        if(imageString.length()>5) {
            //decode base64 string to image
            imageBytes = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            userimage.setImageBitmap(decodedImage);
        }


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getActivity(), ProfileDetails.class);
                startActivity(i);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setTitle("Your RRC Location is")
                        .setIcon(R.drawable.location_icon_36x36)
                        .setMessage("\n"+session.getLocations())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        }).create().show();
            }
        });

        myactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String projectname=FragmentProjects.projectdata.get(0).getProgramName().toString();
                String projecticon="R.drawable.lnt_icon_96x96";
                String projectid=FragmentProjects.projectdata.get(0).getProgramid().toString();

                Intent i =new Intent(getActivity(), ProjectActivities.class);
                i.putExtra("project_id",projectid);
                i.putExtra("project_name", projectname);
                i.putExtra("project_icon", projecticon);
                startActivity(i);

            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return view;
    }

    private void logout(){
        session.setLoggedin(false);
        getActivity().finish();
        startActivity(new Intent(getActivity(),Login.class));
    }
}
