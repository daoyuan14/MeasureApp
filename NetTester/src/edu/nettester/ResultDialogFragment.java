package edu.nettester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ResultDialogFragment extends DialogFragment {

    private Cursor cursor;

    public ResultDialogFragment(Cursor cursor) {
        super();
        this.cursor = cursor;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_result)
               .setItems(R.array.pick_result,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                    }
                });
        return builder.create();
    }

}
