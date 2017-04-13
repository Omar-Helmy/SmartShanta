package com.smartshanta.smartshanta.ui;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.smartshanta.smartshanta.R;
import com.smartshanta.smartshanta.data.DataContract;
import com.smartshanta.smartshanta.services.BluetoothService;
import com.smartshanta.smartshanta.util.Constants;

import java.sql.Timestamp;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListsFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>  {

    private View fragmentLayout;
    private int CURSOR_LOADER_ID = 1;
    private ListAdapter listAdapter;
    private ListView listView;

    public ListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLayout = inflater.inflate(R.layout.fragment_lists, container, false);
        listView = (ListView) fragmentLayout.findViewById(R.id.list_view);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        listAdapter = new ListAdapter(getActivity(),null,0);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_checkbox);
                TextView textView = (TextView) view.findViewById(R.id.list_item_time);
                String ts = textView.getText().toString();
                getActivity().getContentResolver().query();
                checkBox.setChecked(!checkBox.isChecked());
                */
            }
        });

        // The filter's action
        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BL_ACTION_ITEM_CHECK);
        // BC instance
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                bluetoothReceiver,
                statusIntentFilter);
        return fragmentLayout;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader (getActivity(), DataContract.LIST_URI, null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.swapCursor(data);
    }

    /**ListAdapter**/
    private class ListAdapter extends CursorAdapter {

        public ListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            // read from cursor
            viewHolder.item.setText(cursor.getString(cursor.getColumnIndex(DataContract.COLUMN_ITEM_NAME)));
            viewHolder.time.setText(new Timestamp(
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(DataContract.COLUMN_TS)))).toString().substring(0,16));
            viewHolder.checkBox.setChecked(cursor.getInt(cursor.getColumnIndex(DataContract.COLUMN_ITEM_CHECKED))==1);
            if(Constants.isShantaConnected)
                checkItemInShanta(cursor.getString(cursor.getColumnIndex(DataContract.COLUMN_ITEM_NAME)));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view =  LayoutInflater.from(context).inflate(R.layout.fragment_lists_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return view;
        }

        private void checkItemInShanta(String item){
            Intent intent = new Intent(getActivity(), BluetoothService.class);
            intent.setAction(Constants.BL_ACTION_ITEM_CHECK);
            intent.putExtra("msg",Constants.BL_MSG_STUFF+item);
            getActivity().startService(intent);
        }

        public class ViewHolder{

            public final TextView item, time;
            public final CheckBox checkBox;

            public ViewHolder(View view){
                item = (TextView) view.findViewById(R.id.list_item_text);
                time = (TextView) view.findViewById(R.id.list_item_time);
                checkBox = (CheckBox) view.findViewById(R.id.list_item_checkbox);
            }

        }

    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class BluetoothReceiver extends BroadcastReceiver
    {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.BL_ACTION_ITEM_CHECK))
            {
                ContentValues cv = new ContentValues();
                cv.put(DataContract.COLUMN_ITEM_CHECKED, intent.getStringExtra("msg").equals("inside")?1:0);
                getActivity().getContentResolver().update(DataContract.appendToUri(intent.getStringExtra("msg")),
                        cv,null,null);
            }
        }
    }

}
