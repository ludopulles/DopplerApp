package eu.ludiq.dopplerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Loek on 19-4-2015.
 */
public class PickerDialog extends DialogFragment {
    String[] list;
    int num;

    public static PickerDialog newInstance(String[] frequencies, int num) {
        PickerDialog dialog = new PickerDialog();

        dialog.list = new String[] {"Henk", "Joop", "Ludo"};
        dialog.num = num;

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick frequency " + num).setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast toast = Toast.makeText(getActivity(), "Sprite", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return builder.create();
    }
}
