package ru.bstu.checkers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DeleteLoadSavedGameDialogFragment extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onSavedGameDialogDeleteClick(DialogFragment dialog);
        public void onSavedGameDialogLoadClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_deleteloadsavedgame, null);
        builder.setView(view);
        view.findViewById(R.id.DLSGDialogDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSavedGameDialogDeleteClick(DeleteLoadSavedGameDialogFragment.this);
                dismiss();
            }
        });
        view.findViewById(R.id.DLSGDialogLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSavedGameDialogLoadClick(DeleteLoadSavedGameDialogFragment.this);
                dismiss();
            }
        });
        //setCancelable(false);
        AlertDialog dialog = builder.create();
        //dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
