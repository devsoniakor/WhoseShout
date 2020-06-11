package com.mad.whosetreat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mad.whosetreat.R;

import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.MSG;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.RADIUS;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.VIEW_ID;

/**
 * NewSearchDialogFragment is for displaying Google Places API Auto complete service.
 */
public class NewSearchDialogFragment extends DialogFragment {

    private final String TAG = "TAG: NewSearchDialog";
    private NewSearchDialogFragmentListener mHost;
    private int mId;
    private int mRadius;
    private TextView mMsgTv;


    /**
     * finds and connects the view and sets the contents
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Create the custom layout using the LayoutInflater class
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_new_search_dialog, null);

        mMsgTv = v.findViewById(R.id.newSearchTv);
        mMsgTv.setText(getArguments().getString(MSG));
        mId = getArguments().getInt(VIEW_ID);
        mRadius = getArguments().getInt(RADIUS);

        // Build the dialog
        builder.setTitle("Alert")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onOkayNewSearchClick(NewSearchDialogFragment.this, mId, mRadius);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onCancelNewSearchClick(NewSearchDialogFragment.this);
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
        mHost = (NewSearchDialogFragmentListener) context;
    }

    /**
     * interface for NewSearchDialogFragmentListener
     * defines two method which will be handled by host
     */
    public interface NewSearchDialogFragmentListener {
        void onOkayNewSearchClick(DialogFragment dlg, int id, int radius);

        void onCancelNewSearchClick(DialogFragment dlg);
    }
}
