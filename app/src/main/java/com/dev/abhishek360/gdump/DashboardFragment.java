package com.dev.abhishek360.gdump;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class DashboardFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;
    private TextView username,email,point_disp,isemailverified_textview;
    private String name,email_string,uid;
    private ProgressBar progressBar;
    private String database_key;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private CircleImageView circleImageView;

    private int points_int;


    private OnFragmentInteractionListener mListener;

    public DashboardFragment()
    {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        sharedPreferences = this.getActivity().getSharedPreferences("gdumpPref",MODE_PRIVATE);
        String uid_string= sharedPreferences.getString("UID","uid01");

        Boolean isemailverified=currentUser.isEmailVerified();


        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("gdumpPref",MODE_PRIVATE);

        points_int=sharedPreferences.getInt("POINTS",0);
        name=sharedPreferences.getString("FULL_NAME","User");
        email_string=sharedPreferences.getString("EMAIL","gdump@email.com");
        uid=sharedPreferences.getString("UID","error");



        databaseReference=firebaseDatabase.getReference("GDUMP").child("USERS").child(uid).child("G_POINTS");




        username = (TextView)view.findViewById(R.id.dash_username_edittext);
        email=(TextView)view.findViewById(R.id.dash_email_edittext);
        point_disp=(TextView)view.findViewById(R.id.dash_points_edittext);
        isemailverified_textview=(TextView)view.findViewById(R.id.dash_isemailverified_edittext);
        circleImageView=(CircleImageView)view.findViewById(R.id.dash_proPic);
        progressBar=(ProgressBar)view.findViewById(R.id.dash_image_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        storageReference=firebaseStorage.getReference().child("profilePic/"+uid_string+".jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                progressBar.setVisibility(View.GONE);

                Glide.with(getActivity()).load(uri.toString()).into(circleImageView);
            }
        });



        isemailverified_textview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Toast.makeText(getActivity(),"Verification Email Sent!Please check your spam folder also.",Toast.LENGTH_LONG).show();
                        Log.d("Email Verify","Email Verification Email Sent");
                    }
                });
            }
        });






        username.setText("Hello,"+name);
        email.setText(""+email_string);



        if(!isemailverified)
        {
            isemailverified_textview.setVisibility(View.VISIBLE);
            isemailverified_textview.setClickable(true);
            isemailverified_textview.setText("Email not Verified!(Click to Verify)");
        }




        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.exists())
                {

                    points_int=dataSnapshot.getValue(Integer.class);

                    point_disp.setText("Your G-dump points: "+points_int);
                }
                else
                    {

                        point_disp.setText("Your G-dump points: "+points_int);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

class userData
{
    String address;
    int g_points,pin_code,phone_no;

    public userData()
    {


    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public int getG_points()
    {
        return g_points;
    }

    public void setG_points(int g_points)
    {
        this.g_points = g_points;
    }

    public int getPin_code()
    {
        return pin_code;
    }

    public void setPin_code(int pin_code)
    {
        this.pin_code = pin_code;
    }

    public int getPhone_no()
    {
        return phone_no;
    }

    public void setPhone_no(int phone_no)
    {
        this.phone_no = phone_no;
    }
}
