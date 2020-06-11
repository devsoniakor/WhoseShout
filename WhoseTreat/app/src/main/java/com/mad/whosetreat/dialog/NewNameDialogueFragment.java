package com.mad.whosetreat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mad.whosetreat.R;
import com.mad.whosetreat.model.Person;

/**
 * NewGroupDialogueFragment pops up when user clicks DONE button in adding name fragment.
 * will display the textview and a button to set the group's name
 */
public class NewNameDialogueFragment extends DialogFragment {

    public static final String ADDITION_CODE = "addition code";
    public static final String EDIT_MEMBER_DETAIL = "edit member detail";
    public static final String ADD_NEW_MEMBER = "add new member";
    public static final String TARGET_PERSON = "target member";
    private static final String TAG = "NewNameDialogue_TAG";
    private Button mConfirmNewNameBtn;
    private TextInputEditText mNewNameEt;
    private TextView mNewNameTv;
    private TextInputEditText mNewContactEt;
    private NewNameDialogueFragmentListener mHost;

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
        View v = inflater.inflate(R.layout.fragment_new_name_dialogue, null);
        mNewNameEt = v.findViewById(R.id.newPersonNameEt);
        mNewContactEt = v.findViewById(R.id.newPersonContactEt);
        mNewNameTv = v.findViewById(R.id.newPersonNameTv);

        mConfirmNewNameBtn = v.findViewById(R.id.confirmNewPersonBtn);
        if (getArguments().getString(ADDITION_CODE).equals(EDIT_MEMBER_DETAIL)) {
            mConfirmNewNameBtn.setText(getResources().getString(R.string.done));
        }
        mConfirmNewNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getArguments().getString(ADDITION_CODE)) {
                    case EDIT_MEMBER_DETAIL:
                        if (mNewContactEt.getText().toString().isEmpty()) {
                            mHost.onAddConfirmClick(NewNameDialogueFragment.this, mNewNameTv.getText().toString(), mNewContactEt.getHint().toString());
                            dismiss();
                        } else if (isValidPhone(mNewContactEt.getText().toString())) {
                            mHost.onAddConfirmClick(NewNameDialogueFragment.this, mNewNameTv.getText().toString(), mNewContactEt.getText().toString());
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), R.string.please_correct_number, Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case ADD_NEW_MEMBER:
                        if (!mNewNameEt.getText().toString().isEmpty() && !mNewContactEt.getText().toString().isEmpty()) {
                            if (isValidPhone(mNewContactEt.getText().toString())) {
                                mHost.onAddConfirmClick(NewNameDialogueFragment.this, mNewNameEt.getText().toString(), mNewContactEt.getText().toString());
                                dismiss();
                            } else {
                                Toast.makeText(getActivity(), R.string.please_correct_number, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.please_fill_fields, Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
        });

        if (getArguments().getString(ADDITION_CODE).equals(EDIT_MEMBER_DETAIL)) {
            builder.setTitle("Edit Detail").setView(v);
            Person person = getArguments().getParcelable(TARGET_PERSON);
            mNewNameEt.setVisibility(View.GONE);
            mNewNameTv.setVisibility(View.VISIBLE);
            mNewNameTv.setText(person.getName());
            mNewContactEt.setHint(person.getPhone());
        } else {
            builder.setTitle(R.string.add_new_person).setView(v);
        }

        return builder.create();
    }

    /**
     * validate the phone number input
     *
     * @param number
     * @return
     */
    public boolean isValidPhone(String number) {
        if (number.length() < 6 || number.length() > 13) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(number).matches();
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
        mHost = (NewNameDialogueFragmentListener) context;
    }

    /**
     * interface for NewGroupDialogueFragmentListener
     * defines one method which will be handled by host
     */
    public interface NewNameDialogueFragmentListener {
        void onAddConfirmClick(DialogFragment dlg, String name, String phone);
    }
}
