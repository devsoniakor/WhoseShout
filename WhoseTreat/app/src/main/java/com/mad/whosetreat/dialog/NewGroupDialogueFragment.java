package com.mad.whosetreat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.model.Person;

import java.util.HashSet;
import java.util.List;

import static com.mad.whosetreat.activities.GroupDetailEditActivity.GROUP_NAME;

/**
 * NewGroupDialogueFragment pops up when user clicks DONE button in adding name fragment.
 * will display the textview and a button to set the group's name
 */
public class NewGroupDialogueFragment extends DialogFragment {

    private static final String TAG = "NewGroupDialogue_TAG";

    public static final String CODE = "Request code for dialog";
    public static final String MEMBERS_LIST = "Members code for dialog";
    public static final String SWITCH_CODE = "Random Switch Code";

    private Button mConfirmNewNameBtn;
    private TextInputEditText mNewNameEt;
    private NewGroupDialogueFragmentListener mHost;
    private FrameLayout mContainer;

    private ImageButton mInfoBtn; // button for launch info popup
    private TextView mSwitchTv; // TextView showing the status of random switch
    private Switch mRandomSwitch; // switch for random selection of person
    private LinearLayout mMemberContainer;


    /**
     * binds and sets the view
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this fragment
        // Create the custom layout using the LayoutInflater class
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_new_group_dialogue, null);
        mContainer = v.findViewById(R.id.new_group_dialog_container);
        mNewNameEt = v.findViewById(R.id.newGroupNameEt);

        mInfoBtn = v.findViewById(R.id.random_info_btn);
        mSwitchTv = v.findViewById(R.id.random_state_tv);
        mRandomSwitch = v.findViewById(R.id.random_switch);
        mMemberContainer = v.findViewById(R.id.new_group_member_container);


        //TODO: launch popup for showing info
        mInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder infoDialog = new AlertDialog.Builder(getContext());
                infoDialog.setTitle("Random On & Off")
                        .setIcon(R.drawable.ic_question)
                        .setMessage(R.string.random_info_message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

            }
        });

        mRandomSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitchText(mRandomSwitch.isChecked());
            }
        });

        mConfirmNewNameBtn = v.findViewById(R.id.confirmNewGroupBtn);
        mConfirmNewNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                onConFirmBtnClick();
            }
        });

        switch (getArguments().getInt(CODE)) {
            case 1: // from AddMemberActivity
                setMemberTable(); //setup names table for confirmation
                mRandomSwitch.setChecked(false); // false by default
                builder.setTitle(R.string.new_group_dialog)
                        .setView(v);
                break;
            case 2:  // if code ==2, is called from MainActivity for random number draw
                mNewNameEt.setHint(R.string.input_hint);
                mNewNameEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setTitle(R.string.using_random_numbers)
                        .setView(v);
                break;
            case 3: // from GroupDetailEditActivity
                mRandomSwitch.setChecked(getArguments().getBoolean(SWITCH_CODE)); //set switch status according to the current group's setting
                setSwitchText(mRandomSwitch.isChecked()); //set the text for switch status textview
                mNewNameEt.setHint(getArguments().getString(GROUP_NAME));
                mNewNameEt.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setTitle(R.string.change_group_name_title)
                        .setView(v);
                break;
        }

        return builder.create();
    }

    /**
     * set the table data with names of new group members
     */
    private void setMemberTable() {
        List<Person> personList = getArguments().getParcelableArrayList(MEMBERS_LIST);
        //TODO: dynamically generate the table
        for (int i = 0; i < personList.size(); i++) {
            Log.d(TAG, "for loop: " + personList.get(i).getName());
            Person person = personList.get(i);
            TextView nameTv = new TextView(getContext());
            nameTv.setText(i + 1 + ") " + person.getName() + " (" + person.getPhone() + ")");
            nameTv.setTextSize(20);
            nameTv.setPadding(5, 0, 5, 15);
            mMemberContainer.addView(nameTv);

        }

        mMemberContainer.setVisibility(View.VISIBLE);
    }

    private void onConFirmBtnClick() {
        switch (getArguments().getInt(CODE)) {
            case 1: // from AddMemberActivity
                if (!mNewNameEt.getText().toString().isEmpty()) {

                    // check whether the new group's name is duplicated
                    WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
                    HashSet<String> namesSet = whoseTreatApplication.getGroupNameSet();

                    int size = namesSet.size();
                    namesSet.add(mNewNameEt.getText().toString());
                    if (size != namesSet.size()) {
                        whoseTreatApplication.saveAllGroupNamesSet(namesSet);
                        Log.d(TAG, "Dialog random? " + mRandomSwitch.isChecked());
                        mHost.onConfirmClick(NewGroupDialogueFragment.this, mNewNameEt.getText().toString(), mRandomSwitch.isChecked());
                        dismiss();
                    } else {
                        Snackbar.make(mContainer, R.string.enter_msg, Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            case 2: // from MainActivity
                if (mNewNameEt.getText().toString().isEmpty()) {
                    mHost.onConfirmClick(NewGroupDialogueFragment.this, "1", true);
                    dismiss();
                } else {
                    mHost.onConfirmClick(NewGroupDialogueFragment.this, mNewNameEt.getText().toString(), true);
                    dismiss();
                }
                break;
            case 3: // from GroupDetailEditActivity
                Log.d("Switch check", "isChecked?" + mRandomSwitch.isChecked());
                if (mNewNameEt.getText().toString().isEmpty() || mNewNameEt.getText().toString().equals(getArguments().getString(GROUP_NAME))) {
                    mHost.onConfirmClick(NewGroupDialogueFragment.this, getArguments().getString(GROUP_NAME), mRandomSwitch.isChecked());
                    dismiss();
                } else {
                    // check whether the new group's name is duplicated
                    WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
                    HashSet<String> groupNameSet = whoseTreatApplication.getGroupNameSet();

                    int size = groupNameSet.size();
                    groupNameSet.add(mNewNameEt.getText().toString());

                    if (size != groupNameSet.size()) {
                        whoseTreatApplication.saveAllGroupNamesSet(groupNameSet);
                        groupNameSet.remove(getArguments().getString(GROUP_NAME));
                        mHost.onConfirmClick(NewGroupDialogueFragment.this, mNewNameEt.getText().toString(), mRandomSwitch.isChecked());
                        dismiss();
                    } else {
                        Snackbar.make(mContainer, R.string.group_already_exists, Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void setSwitchText(boolean isSwitchOn) {
        if (isSwitchOn) {
            mSwitchTv.setText(R.string.on);
        } else {
            mSwitchTv.setText(R.string.off);
        }
    }

    /**
     * host context is initiated onAttach
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHost = (NewGroupDialogueFragmentListener) context;
    }

    /**
     * interface for NewGroupDialogueFragmentListener
     * defines one method which will be handled by host
     */
    public interface NewGroupDialogueFragmentListener {
        void onConfirmClick(DialogFragment dlg, String name, boolean isRandom);
    }
}
