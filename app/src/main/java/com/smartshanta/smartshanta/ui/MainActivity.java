package com.smartshanta.smartshanta.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.smartshanta.smartshanta.R;
import com.smartshanta.smartshanta.data.DataContract;
import com.smartshanta.smartshanta.services.BluetoothService;
import com.smartshanta.smartshanta.util.Constants;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeFragment homeFragment;
    private ListsFragment listsFragment;
    private PagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    private Toolbar myToolbar;
    private final int RC_SIGN_IN = 1;
    private AccountHeader header;
    private Drawer drawer;
    private final int ID_HEADER_DEFAULT = 1, ID_LOGIN_ITEM = 2, ID_SETTINGS_ITEM = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /********* GUI setup *********/
        // tool bar:
        myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        /******** Pager and Tabs *******/
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // attach adapter to the viewpager:
        homeFragment = new HomeFragment();
        listsFragment = new ListsFragment();
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        // link between viewpager and tablayout
        tabLayout.setupWithViewPager(viewPager);

        /********** Setup Navigation Menu **********/
        // Create the AccountHeader
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_background)
                .addProfiles(
                        new ProfileDrawerItem().withIdentifier(ID_HEADER_DEFAULT).withName("Please Login First")
                )
                .build();
        // create items
        PrimaryDrawerItem loginItem = new PrimaryDrawerItem().withIdentifier(ID_LOGIN_ITEM).withName("Login");
        //initialize the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(MainActivity.this).load(uri)
                        .centerCrop().into(imageView);
            }
        });
        // create drawer
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        loginItem
                        //new DividerDrawerItem(),
                        //settingsItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem.getIdentifier()==ID_LOGIN_ITEM)
                            firebaseLogin();
                        return true;
                    }
                })
                .build();
        // setup hamborger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        /******* Try Firebase Login ********/
        firebaseLogin();

        /********** Add Item To Shanta***********/
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.isShantaConnected)
                    new AddItemDialog().show(getSupportFragmentManager(),AddItemDialog.class.getSimpleName());
                else
                    Snackbar.make(v,"Please Connect first!",Snackbar.LENGTH_SHORT).show();
            }
        });


    }

    private void firebaseLogin(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        // attach auth listener:
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();
                    // Update profile header menu
                    header.removeProfileByIdentifier(ID_HEADER_DEFAULT);
                    header.addProfiles(
                            new ProfileDrawerItem().withName(name).withEmail(email)
                                    .withIcon(photoUrl)
                                    .withIdentifier(ID_HEADER_DEFAULT));
                    // Remove login Item from Menu
                    drawer.removeItem(ID_LOGIN_ITEM);
                    SecondaryDrawerItem settingsItem = new SecondaryDrawerItem().withIdentifier(ID_SETTINGS_ITEM).withName("Settings");
                    drawer.addItem(settingsItem);
                } else {
                    // User is signed out, try sign up or in:
                    startActivity(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.bag_icon)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setIsSmartLockEnabled(false)
                                    .build());

                }
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
    public static class AddItemDialog extends DialogFragment {

        private EditText itemName;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog_add_item, null);
            itemName = (EditText) view.findViewById(R.id.dialog_item_name);
            builder.setTitle("Add Item")
                    .setMessage("Type the item name you want to add:")
                    .setView(view)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if(Constants.isShantaConnected){
                                // Store in local database first:
                                ContentValues cv = new ContentValues();
                                cv.put(DataContract.COLUMN_ITEM_NAME, itemName.getText().toString());
                                cv.put(DataContract.COLUMN_TS, Long.toString(System.currentTimeMillis()));
                                cv.put(DataContract.COLUMN_ITEM_CHECKED, 0);
                                getActivity().getContentResolver().insert(DataContract.LIST_URI, cv);
                                // Send to Shanta
                                Intent intent = new Intent(getActivity(), BluetoothService.class);
                                intent.setAction(Constants.BL_ACTION_SEND);
                                intent.putExtra(Constants.BL_MSG_KEY, Constants.BL_MSG_DEFINE_ITEM + itemName.getText().toString());
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
                    });
            return builder.create();
        }
    }
}