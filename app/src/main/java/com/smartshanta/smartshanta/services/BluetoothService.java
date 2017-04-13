package com.smartshanta.smartshanta.services;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.smartshanta.smartshanta.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;


public class BluetoothService extends IntentService {

    private BluetoothAdapter mBluetoothAdapter;
    public static BluetoothSocket mSocket;
    private BluetoothDevice shanta;
    private String action;

    public BluetoothService() {
        super("BluetoothService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            action = intent.getAction();
            if (action.equals(Constants.BL_ACTION_CONNECT)) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                getBluetoothDevice();
                requestConnection();
            } else
                sendData(intent.getStringExtra("msg"));
        }
    }

    private void getBluetoothDevice() {
        // get paired devices
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceAdd = device.getAddress();
                if (deviceName.equals("HC-05")) {
                    Log.i("Bluetooth","Device found! Name: \"" + deviceName + "\" Address: \"" + deviceAdd + "\"");
                    shanta = device;
                    // it is better to cancel discovery to save Bluetooth resources before starting any connections:
                    mBluetoothAdapter.cancelDiscovery();

                    break;
                }
            }
        }
    }

    private void requestConnection(){
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            mSocket = shanta.createRfcommSocketToServiceRecord(UUID.fromString(Constants.UUID));
        } catch (IOException e) {
        }
        while(true){
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mSocket.connect();
                sendState(Constants.BL_ACTION_CONNECT, Constants.BL_MSG_CONNECTED);
                return;
            } catch (IOException connectException) {

            }
        }
    }

    private void receiveData()
    {
        InputStream in;
        InputStreamReader inr;
        BufferedReader br;
        String msg="";
        char[] buffer = new char[30];  // buffer store for the stream, 3 bytes for BPM + 5 bytes for temp
        try {
            while(true) {
                in = mSocket.getInputStream();
                inr = new InputStreamReader(in);
                br = new BufferedReader(inr);
                while(in.available() > 0 && br.ready()) {
                    br.read(buffer);
                    if (buffer[0]=='\u0000')
                        continue;
                    msg += new String(buffer);
                    sendState(action, msg);
                }
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendData(String msg)
    {
        OutputStream os;
        OutputStreamWriter osw;
        try {
            os = mSocket.getOutputStream();
            os.write(msg.getBytes());
            receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendState(String action, String msg)
    {
        Intent localIntent = new Intent(action);
        localIntent.putExtra("msg",msg);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void takeAction(String msg) {

    }
    /*
    private void acceptConnection() {
        BluetoothServerSocket bluetoothServerSocket;
        try {
            bluetoothServerSocket = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord("com.smartshanta.smartshanta", UUID.fromString(Constants.UUID));
            Log.d("Bluetooth",bluetoothServerSocket.toString());
            while (true) {
                mSocket = bluetoothServerSocket.accept();
                Log.d("Bluetooth",bluetoothServerSocket.toString());
                bluetoothServerSocket.close();
                sendState(Constants.BL_ACTION_CONNECT);
                Constants.isShantaConnected = true;
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
}
