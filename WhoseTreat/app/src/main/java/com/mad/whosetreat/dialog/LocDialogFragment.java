package com.mad.whosetreat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mad.whosetreat.R;

/**
 * LocDialogFragment showing the dialog for asking location options to user.
 * user can choose manual input, turning on GPS and cancel.
 */
public class LocDialogFragment extends DialogFragment {

    private final String TAG = "TAG: LocDialogFragment";
    private LocDialogListener mHost;

    /**
     * on creation, binds the view and sets the contents of it.
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Create the custom layout using the LayoutInflater class
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_loc_dialog, null);

        // Build the dialog
        builder.setTitle(R.string.loc_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mHost.onOkayClick(LocDialogFragment.this);
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onCancelClick(LocDialogFragment.this);

                    }
                })
                .setNeutralButton(R.string.manual_input, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onManualCity(LocDialogFragment.this);

                    }
                })
                .setView(v);

        return builder.create();
    }

    /**
     * host context is initiated onAttach
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHost = (LocDialogListener) context;
    }

    /**
     * interface for LocDialogListener
     * defines three methods which will be handled by host
     */
    public interface LocDialogListener {
        void onManualCity(DialogFragment dlg);

        void onOkayClick(DialogFragment dlg) throws Settings.SettingNotFoundException;

        void onCancelClick(DialogFragment dlg);
    }
}
