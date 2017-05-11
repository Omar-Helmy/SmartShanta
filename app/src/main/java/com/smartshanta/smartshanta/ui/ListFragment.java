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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartshanta.smartshanta.R;
import com.smartshanta.smartshanta.data.DataContract;
import com.smartshanta.smartshanta.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private View fragmentLayout;
    private final int CURSOR_LOADER_ID = 1;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLayout = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) fragmentLayout.findViewById(R.id.recycle_list);

        // Recycle Adapter
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        // The filter's action
        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BL_ACTION_STAFF_CHECK);
        // BC instance
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                bluetoothReceiver,
                statusIntentFilter);
        return fragmentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DataContract.LIST_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recyclerAdapter.updateCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /////////////////////////////// Recycle Adapter /////////////////////////////////
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Cursor cursor;
        //private ArrayList<String> itemsList = new ArrayList<>();   // this is array of items names used to check states via bluetooth

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ((NormalViewHolder) holder).setupData();

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new NormalViewHolder(inflater.inflate(R.layout.fragment_list_item, parent, false));
        }


        @Override
        public int getItemCount() {
            if (null == cursor) return 0;
            // items = cursor count
            return cursor.getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        /* this method not overridden */
        public void updateCursor(Cursor c) {
            cursor = c;
            notifyDataSetChanged();
        }

        public class NormalViewHolder extends RecyclerView.ViewHolder {

            private final TextView nameText, timeText; // must be final
            private final ImageView imageView;
            //private final CheckBox checkBox;


            public NormalViewHolder(View itemView) {
                super(itemView);    // pass view to super class
                /* attach views */
                nameText = (TextView) itemView.findViewById(R.id.list_item_text);
                timeText = (TextView) itemView.findViewById(R.id.list_item_time);
                imageView = (ImageView) itemView.findViewById(R.id.list_item_remove);
                //checkBox = (CheckBox) itemView.findViewById(R.id.list_item_checkbox);

                /* on click listener on the whole view */
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // remove from shanta, but keep in database
                        // TODO: remove!!
                        Toast.makeText(getActivity(), "REMOVE!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void setupData() {
                cursor.moveToPosition(getAdapterPosition());
                nameText.setText(cursor.getString(cursor.getColumnIndex(DataContract.COLUMN_ITEM_NAME)));
                timeText.setText(DateFormat.format("hh:mm a - dd/MM/yyyy",  // hour:minute AM/PM - date
                        Long.parseLong(cursor.getString(cursor.getColumnIndex(DataContract.COLUMN_TS)))));
            }

        }
    }


    // Broadcast receiver for receiving status updates from the IntentService
    private class BluetoothReceiver extends BroadcastReceiver {

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BL_ACTION_STAFF_CHECK)) {
                // Response format: ItemName1,ItemState1_ItemName2,ItemState2 ...
                for (String pair : intent.getStringExtra(Constants.BL_MSG_KEY).split("_")) {
                    String[] itemState = pair.split(",");
                    ContentValues cv = new ContentValues();
                    cv.put(DataContract.COLUMN_ITEM_CHECKED, itemState[1].equals("Inside") ? 1 : 0);
                    getActivity().getContentResolver().update(DataContract.appendToUri(itemState[0]),
                            cv, null, null);
                }
            }
        }
    }

}
