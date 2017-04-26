package com.smartshanta.smartshanta.ui;


import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartshanta.smartshanta.R;
import com.smartshanta.smartshanta.services.BluetoothService;
import com.smartshanta.smartshanta.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private View fragmentLayout;
    private Button mapBtn, findBtn, unlockBtn, userNumBtn;
    private FloatingActionButton bluetoothBtn;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLayout = inflater.inflate(R.layout.fragment_home, container, false);
        bluetoothBtn = (FloatingActionButton) fragmentLayout.findViewById(R.id.connect_btn);
        mapBtn = (Button) fragmentLayout.findViewById(R.id.map_btn);
        findBtn = (Button) fragmentLayout.findViewById(R.id.find_btn);
        unlockBtn = (Button) fragmentLayout.findViewById(R.id.unlock_btn);
        userNumBtn = (Button) fragmentLayout.findViewById(R.id.set_user_num_btn);


        /***********Buttons************/
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter != null) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }
                    Intent mServiceIntent = new Intent(getActivity(), BluetoothService.class);
                    mServiceIntent.setAction(Constants.BL_ACTION_CONNECT);
                    Snackbar.make(v,"Connecting...",Snackbar.LENGTH_SHORT).show();
                    getActivity().startService(mServiceIntent);

                } else
                    Snackbar.make(v,"Bluetooth not supported or no permission!",Snackbar.LENGTH_SHORT).show();

            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Constants.isShantaConnected){
                    Intent mapIntent = new Intent(getActivity(), BluetoothService.class);
                    mapIntent.setAction(Constants.BL_ACTION_LOCATE);
                    mapIntent.putExtra(Constants.BL_MSG_KEY, Constants.BL_MSG_LOCATE);
                    getActivity().startService(mapIntent);
                }else{
                    Snackbar.make(v,"Please Connect first!",Snackbar.LENGTH_SHORT).show();
                }

                /*
                Uri gmmIntentUri = Uri.parse("geo:30.071270,31.020738");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                */
                /*
                if(Constants.isShantaConnected){
                    Intent sendIntent = new Intent(getActivity(), BluetoothService.class);
                    sendIntent.setAction(Constants.BL_ACTION_SEND);
                    sendIntent.putExtra("msg",Constants.BL_MSG_LOCATE);
                    getActivity().startService(sendIntent);
                }else{
                    Toast.makeText(getActivity(), "Please connect first!", Toast.LENGTH_SHORT).show();
                }
                */
            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.isShantaConnected){
                    Intent sendIntent = new Intent(getActivity(), BluetoothService.class);
                    sendIntent.setAction(Constants.BL_ACTION_SEND);
                    sendIntent.putExtra(Constants.BL_MSG_KEY, Constants.BL_MSG_FIND_ME);
                    getActivity().startService(sendIntent);
                }else{
                    Snackbar.make(v, "Please Connect first!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.isShantaConnected) {
                    new UnlockDialog().show(getActivity().getSupportFragmentManager(), UnlockDialog.class.getSimpleName());

                } else {
                    Snackbar.make(v, "Please Connect first!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        /***************** Register BCR with intent filters ********************/
        // The Intent Filters' action
        IntentFilter connectIF = new IntentFilter(
                Constants.BL_ACTION_CONNECT);
        IntentFilter locateIF = new IntentFilter(
                Constants.BL_ACTION_LOCATE);
        // BC instance
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
        // Registers each receiver and its intent filter
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                bluetoothReceiver,
                connectIF);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                bluetoothReceiver,
                locateIF);

        return fragmentLayout;
    }

    /******************* Broadcast Receiver ************************/
    // Broadcast receiver for receiving status updates from the IntentService
    private class BluetoothReceiver extends BroadcastReceiver
    {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            String msg = intent.getStringExtra(Constants.BL_MSG_KEY);

            if (action.equals(Constants.BL_ACTION_CONNECT) && msg != null) {
                bluetoothBtn.setVisibility(View.GONE);
                Constants.isShantaConnected = true;
                // start bluetooth service to get items states to update ListFragment
                Intent staffIntent = new Intent(getActivity(), BluetoothService.class);
                staffIntent.setAction(Constants.BL_ACTION_STAFF_CHECK);
                staffIntent.putExtra(Constants.BL_MSG_KEY, Constants.BL_MSG_STUFF);
                getActivity().startService(staffIntent);

            } else if (action.equals(Constants.BL_ACTION_LOCATE) && msg != null) {
                Intent activityIntent = new Intent(getActivity(), MapActivity.class);
                activityIntent.putExtra(Constants.BL_MSG_KEY, msg);
                startActivity(activityIntent);
            }
        }
    }

    /******************
     * Add Item Dialog
     ******************/
    public static class UnlockDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog_unlock, null);
            final EditText password = (EditText) view.findViewById(R.id.password_txt);
            builder.setTitle("Unlock")
                    .setMessage("Type password to unlock Shanta:")
                    .setView(view)
                    .setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (Constants.isShantaConnected) {
                                // Send to Shanta
                                Intent intent = new Intent(getActivity(), BluetoothService.class);
                                intent.setAction(Constants.BL_ACTION_UNLOCK);
                                intent.putExtra(Constants.BL_MSG_KEY, Constants.BL_MSG_UNLOCK + password.getText().toString());
                                getActivity().startService(intent);
                            } else {
                                Toast.makeText(getActivity(), "Please connect first!", Toast.LENGTH_SHORT).show();
                            }
                            //UnlockDialog.this.getDialog().cancel();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            UnlockDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}
