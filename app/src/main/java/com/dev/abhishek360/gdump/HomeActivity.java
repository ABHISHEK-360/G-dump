package com.dev.abhishek360.gdump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements ScanFragment.OnFragmentInteractionListener,DashboardFragment.OnFragmentInteractionListener,
                                                                    ProfileFragment.OnFragmentInteractionListener

{

    private TextView mTextMessage;
    private String name,email;
    private Fragment fragment=null;
    private FragmentTransaction ft;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_dashboard:

                    fragment =new DashboardFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.home_botnav_frame,fragment);
                    ft.commit();
                    return true;
                case R.id.navigation_scan:

                    fragment =new ScanFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.home_botnav_frame,fragment);
                    ft.commit();
                    return true;
                case R.id.navigation_myprofile:

                    fragment =new ProfileFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.home_botnav_frame,fragment);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("gdumpPref",MODE_PRIVATE);
        //spEditor= sharedPreferences.edit();

        fragment = new ScanFragment();
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.home_botnav_frame,fragment);

        ft.commit();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_scan);

        name = sharedPreferences.getString("FULL_NAME","User");
        email = sharedPreferences.getString("EMAIL","User@gdum.com");
        String imgUrl =sharedPreferences.getString("IMG_URL","url/gdump");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dot_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int intId = item.getItemId();
        switch (intId)
        {
            case R.id.menu_settings:
                Intent in = new Intent(HomeActivity.this,SettingsActivity.class);

                startActivity(in);
                break;

            case R.id.menu_signout:

                FirebaseAuth.getInstance().signOut();
                spEditor=sharedPreferences.edit();
                spEditor.clear();
                spEditor.commit();

                Intent logoutIntent = new Intent(HomeActivity.this,LoginActivity.class);

                startActivity(logoutIntent);
                break;

            case R.id.menu_help:
                Intent helpIntent = new Intent(HomeActivity.this,HelpActivity.class);
                startActivity(helpIntent);
                break;




        }
        return super.onOptionsItemSelected(item);
    }

    public static void tosty(String str, Context ctx)
    {

        Toast.makeText(ctx,str,Toast.LENGTH_LONG).show();

    }


    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }
}
