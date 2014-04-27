package edu.nettester;

import java.util.List;

import org.apache.http.NameValuePair;
import edu.nettester.db.MeasureContract.OfflineDel;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.db.MofflineDBHelper;
import edu.nettester.task.OPHTTPClient;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class ResultDialogFragment extends DialogFragment implements Constant {

    private Cursor cursor;
    private Context mContext;
    private ActionBar actionbar;

    public ResultDialogFragment(Context context, Cursor cursor, ActionBar actionbar) {
        super();
        this.cursor = cursor;
        this.mContext = context;
        this.actionbar = actionbar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_result)
               .setItems(R.array.pick_result,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {                        
                        switch(which) {
                            case 0: //Delete
                                if (DEBUG) Log.d(TAG, "0");
                                MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
                                long c_id = mDbHelper.fetchKeyId(cursor);
                                Cursor cur = mDbHelper.fetchOneRow(c_id);
                                cur.moveToFirst();
                                int cindex = cur.getColumnIndex(MeasureLog.MID);
                                String c_mid = cur.getString(cindex);
                                int cindex2 = cur.getColumnIndex(MeasureLog.MUID);
                                String c_muid = cur.getString(cindex2);
                                
                                if(CommonMethod.M_UID.equals(c_muid)) {
	                                //insert the deleted record to the OfflineDB
	                                MofflineDBHelper mOffline = new MofflineDBHelper(mContext);
	                                SQLiteDatabase db = mOffline.getWritableDatabase();
	                                ContentValues values = new ContentValues();
	                                values.put(OfflineDel.MUID, CommonMethod.M_UID);
	                                values.put(OfflineDel.MID, c_mid);
	                                long newRowId = db.insert(OfflineDel.TABLE_NAME, null, values);
	                                if (DEBUG)
	                                    Log.d(TAG, "Insert to OfflineDB, row: "+newRowId);
	                                
	                                //delete the record in the original database
	                                mDbHelper.deleteOneRow(cursor);
	                                mOffline.close();
                                } else if(c_muid.equals("0")) {
                                	//delete the data of Anonymous user directly
                                	mDbHelper.deleteOneRow(cursor);
                                }
                                mDbHelper.close();
                                
                                //((SimpleCursorAdapter)((BaseAdapter) list_result.getAdapter())).notifyDataSetChanged();
                                actionbar.setSelectedNavigationItem(0);
                                actionbar.setSelectedNavigationItem(1);
                                
                                break;
                                
                            case 1: //Show
                                if (DEBUG) Log.d(TAG, "1");
                                openResultActivity();
                                break;
                                
                            case 2: //Close
                                if (DEBUG) Log.d(TAG, "2");
                                dialog.dismiss();
                                break;
                                
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        return builder.create();
    }
    
    private void openResultActivity() {
        Intent intent = new Intent(mContext, ResultActivity.class);
        
        MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
        long keyid = mDbHelper.fetchKeyId(cursor);
        intent.putExtra("keyid", keyid);
        if (DEBUG)
            Log.d(TAG, "sent keyid: "+keyid);
        
        startActivity(intent);
    }
    
    private class DelSync extends AsyncTask<List<NameValuePair>, Void, String> {
    	@Override
		protected String doInBackground(List<NameValuePair>...params) {
    		String output = "";
    		        	
        	OPHTTPClient mclient = new OPHTTPClient();
        	output = mclient.postPage(CommonMethod.deldata_url, params[0]);
        	
        	if (CommonMethod.DEBUG)
        		Log.d(CommonMethod.TAG, output);
        	
        	mclient.destroy();
        	
    		return output;
    	}
    	
    	@Override
        protected void onPostExecute(String result) {
    		
    	}
    }

}
