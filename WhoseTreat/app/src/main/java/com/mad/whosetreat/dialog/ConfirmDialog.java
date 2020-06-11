package com.mad.whosetreat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mad.whosetreat.R;

import static com.mad.whosetreat.adapter.GroupInfoAdapter.DELETE;
import static com.mad.whosetreat.adapter.GroupInfoAdapter.RESET;
import static com.mad.whosetreat.adapter.NameAdapter.NAME_ADAPTER;
import static com.mad.whosetreat.dialog.NewGroupDialogueFragment.CODE;

/**
 * ConfirmDialog is used for asking a confirmation to users regarding deletion and
 * reset of group.
 */
public class ConfirmDialog extends DialogFragment {

    private final String TAG = "DeleteConfirmDialog_TAG";
    private confirmDialogListener mHost;

    /**
     * binds view and sets the contents according to the request type
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Create the custom layout using the LayoutInflater class
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_confirm_dialog, null);

        // sets the dialog message depending on the request code
        TextView msg = v.findViewById(R.id.confirm_tv);
        switch (getArguments().getString(CODE)) {
            case RESET:
                msg.setText(R.string.reset_the_group);
                break;
            case DELETE:
                msg.setText(R.string.delete_the_group);
                break;
            case NAME_ADAPTER:
                msg.setText(R.string.delete_the_person);
                break;
        }


        // Build the dialog
        builder.setTitle(R.string.alert)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mHost.onEditConfirmClick(ConfirmDialog.this, getArguments().getString(CODE));
                        } catch (NullPointerException e) {
                            Log.i(TAG, "NullPointerException");

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onCancelClick(ConfirmDialog.this);

                    }
                })
                .setView(v);

        return builder.create();
    }

    /**
     * binding and sets host activity
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mHost = (confirmDialogListener) context;
        } catch (Exception e) {

        }
    }


    /**
     * interface for confirmDialogListener
     */
    public interface confirmDialogListener {
        void onEditConfirmClick(DialogFragment dlg, String code);

        void onCancelClick(DialogFragment dlg);
    }
}
