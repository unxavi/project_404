package unxavi.com.github.project404.features.task.feed.detail;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import unxavi.com.github.project404.R;


public class DeleteDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    DeleteDialogFragmentListener mListener;

    public interface DeleteDialogFragmentListener {
        void onDialogPositiveClick();

        void onDialogNegativeClick();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (DeleteDialogFragmentListener) getParentFragment();
        } catch (ClassCastException e) {
            // The context doesn't implement the interface, throw exception
            throw new ClassCastException("Caller must implement DeleteDialogFragmentListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_delete)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
