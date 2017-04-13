package com.smartshanta.smartshanta;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.smartshanta.smartshanta.data.DataContract;
import com.smartshanta.smartshanta.services.BluetoothService;
import com.smartshanta.smartshanta.util.Constants;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeFragment homeFragment;
    private ListsFragment listsFragment;
    private PagerAdapter pagerAdapter;
    private TabLayout.Tab homeTab, listsTab;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        /*********GUI setup*********/
        fab = (FloatingActionButton) findViewById(R.id.fab);
        // tool bar:
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // find layout viewPager:
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        // tab layout:
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // get tabs from ab layout:
        homeTab = tabLayout.getTabAt(0);
        listsTab = tabLayout.getTabAt(1);
        // attach adapter to the viewpager:
        homeFragment = new HomeFragment();
        listsFragment = new ListsFragment();
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        // link between viewpager and tablayout
        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddItemDialog().show(getSupportFragmentManager(),AddItemDialog.class.getSimpleName());
            }
        });




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