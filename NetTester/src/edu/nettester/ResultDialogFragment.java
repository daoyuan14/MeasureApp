package edu.nettester;

import edu.nettester.db.MeasureDBHelper;
import edu.nettester.util.Constant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ResultDialogFragment extends DialogFragment implements Constant {

    private Cursor cursor;
    private Context mContext;

    public ResultDialogFragment(Context context, Cursor cursor) {
        super();
        this.cursor = cursor;
        this.mContext = context;
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
                                mDbHelper.deleteOneRow(cursor);
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
        startActivity(intent);
    }

}
