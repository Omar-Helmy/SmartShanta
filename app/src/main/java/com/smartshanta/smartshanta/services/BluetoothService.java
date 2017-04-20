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

    private BluetoothAdapter adapter;
    public static BluetoothSocket socket;
    private BluetoothDevice device;
    private String action;
    private final String LOG_TAG = BluetoothService.class.getSimpleName();

    public BluetoothService() {
        super("BluetoothService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            action = intent.getAction();
            if (action != null && action.equals(Constants.BL_ACTION_CONNECT)) {
                adapter = BluetoothAdapter.getDefaultAdapter();
                connectToShanta();
            } else
                sendData(intent.getStringExtra("msg"));
        }
    }

    private void connectToShanta() {

        /*** Get Bluetooth Device ***/
        // get paired devices
        final Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceAdd = device.getAddress();
                if (deviceName.equals(Constants.BL_DEVICE_NAME)) {
                    this.device = device;
                    Log.i(LOG_TAG,"Device found! Name: \"" + deviceName + "\" Address: \"" + deviceAdd + "\"");
                    // it is better to cancel discovery to save Bluetooth resources before starting any connections:
                    adapter.cancelDiscovery();
                    break;
                }
            }
        }
        /*** Try to connect ***/
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            if(device != null)
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.UUID));
        } catch (IOException e) {
        }
        while(socket != null){
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect();
                // Done connected
                Log.i(LOG_TAG,"Done! Connected with: "+socket.getRemoteDevice().getName());
                sendState(Constants.BL_ACTION_CONNECT, Constants.BL_MSG_CONNECTED);
                return;
            } catch (IOException connectException) {
                // continue trying to connect again
            }
        }

    }

    private void receiveData()
    {
        InputStream in;
        InputStreamReader inr;
        BufferedReader br;
        String msg="";
        char[] buffer = new char[30];  // buffer store
        while(true) {
            try {
                in = socket.getInputStream();
                inr = new InputStreamReader(in);
                br = new BufferedReader(inr);
                while(in.available() > 0 && inr.ready() && br.ready()) {
                    // TODO: use br.readLine() to get String!!!
                    br.read(buffer);
                    if (buffer[0]=='\u0000')
                        continue;
                    msg += new String(buffer);
                    sendState(action, msg);
                }
                return;
            }
            catch (IOException e) {
                // continue
            }

        }
    }

    private void sendData(String msg)
    {
        OutputStream os;
        OutputStreamWriter osw;
        try {
            os = socket.getOutputStream();
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
            bluetoothServerSocket = adapter
                    .listenUsingRfcommWithServiceRecord("com.smartshanta.smartshanta", UUID.fromString(Constants.UUID));
            Log.d("Bluetooth",bluetoothServerSocket.toString());
            while (true) {
                socket = bluetoothServerSocket.accept();
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
