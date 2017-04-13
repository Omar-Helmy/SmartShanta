package com.smartshanta.smartshanta.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.smartshanta.smartshanta.R;
import com.smartshanta.smartshanta.data.DataContract;
import com.smartshanta.smartshanta.services.BluetoothService;
import com.smartshanta.smartshanta.util.Constants;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeFragment homeFragment;
    private ListsFragment listsFragment;
    private PagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /********* GUI setup *********/
        // tool bar:
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        // Pager and Tabs:
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // attach adapter to the viewpager:
        homeFragment = new HomeFragment();
        listsFragment = new ListsFragment();
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        // link between viewpager and tablayout
        tabLayout.setupWithViewPager(viewPager);

        // setup navigation view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,myToolbar,R.string.drawer_toggle_open,R.string.drawer_toggle_close);
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);      // Highlight the selected item
                        drawerLayout.closeDrawers();    // Close drawer
                        return true;
                    }
                });

        // fab:
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddItemDialog().show(getSupportFragmentManager(),AddItemDialog.class.getSimpleName());
            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    /***********************************Fragment Pager Adapter**************************************/
    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: {
                    return homeFragment;
                }
                case 1: {
                    return listsFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    return "Home";
                }
                case 1: {
                    return "List";
                }
            }
            return null;
        }
    }

    /******************Add Item Dialog******************/
    public class AddItemDialog extends DialogFragment {

        private EditText itemName;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_dialog_add_item, null);
            itemName = (EditText) view.findViewById(R.id.dialog_item_name);
            builder.setView(view)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if(Constants.isShantaConnected){

                                ContentValues cv = new ContentValues();
                                cv.put(DataContract.COLUMN_ITEM_NAME, itemName.getText().toString());
                                cv.put(DataContract.COLUMN_TS, Long.toString(System.currentTimeMillis()));
                                cv.put(DataContract.COLUMN_ITEM_CHECKED, 0);
                                getActivity().getContentResolver().insert(DataContract.LIST_URI, cv);

                                Intent intent = new Intent(getActivity(), BluetoothService.class);
                                intent.setAction(Constants.BL_ACTION_SEND);
                                intent.putExtra("msg",Constants.BL_MSG_DEFINE_ITEM+itemName.getText().toString());
                                getActivity().startService(intent);
                            }else{
                                Toast.makeText(getActivity(), "Please connect first!", Toast.LENGTH_SHORT).show();
                            }
                            AddItemDialog.this.getDialog().cancel();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddItemDialog.this.getDialog().cancel();
                        }
                    })
                    .setTitle("Add Item:");
            return builder.create();
        }
    }
}