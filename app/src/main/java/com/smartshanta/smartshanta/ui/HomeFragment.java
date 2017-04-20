package com.smartshanta.smartshanta.ui;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.smartshanta.smartshanta.R;
import com.smartshanta.smartshanta.services.BluetoothService;
import com.smartshanta.smartshanta.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private View fragmentLayout;
    private Button mapBtn, findBtn, bagNumBtn, userNumBtn;
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
        bagNumBtn = (Button) fragmentLayout.findViewById(R.id.get_bag_num_btn);
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
                    mapIntent.putExtra("msg",Constants.BL_MSG_LOCATE);
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
                    sendIntent.putExtra("msg",Constants.BL_MSG_FIND_ME);
                    getActivity().startService(sendIntent);
                }else{
                    Toast.makeText(getActivity(), "Please connect first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // The filter's action
        IntentFilter connectIF = new IntentFilter(
                Constants.BL_ACTION_CONNECT);
        IntentFilter locateIF = new IntentFilter(
                Constants.BL_ACTION_LOCATE);
        // BC instance
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                bluetoothReceiver,
                connectIF);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                bluetoothReceiver,
                locateIF);

        return fragmentLayout;
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class BluetoothReceiver extends BroadcastReceiver
    {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.BL_ACTION_CONNECT))
            {
                bluetoothBtn.setVisibility(View.GONE);
                Constants.isShantaConnected = true;
            }
            else if(intent.getAction().equals(Constants.BL_ACTION_LOCATE))
            {
                String msg = intent.getStringExtra("msg");
                if(msg!=null) {
                    Intent activityIntent = new Intent(getActivity(), MapActivity.class);
                    activityIntent.putExtra("msg", msg);
                    startActivity(activityIntent);
                }
                else
                    Toast.makeText(getActivity(), "Please connect first!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
