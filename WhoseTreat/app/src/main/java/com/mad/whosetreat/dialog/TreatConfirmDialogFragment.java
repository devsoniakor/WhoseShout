package com.mad.whosetreat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;
import com.mad.whosetreat.model.Person;
import com.mad.whosetreat.model.PlaceDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.PERSON_INDEX;
import static com.mad.whosetreat.activityFragments.RandNumFragment.RAND;
import static com.mad.whosetreat.activityFragments.ShopDetailFragment.GROUP_INDEX;
import static com.mad.whosetreat.activityFragments.WhereToGoFragment.CODE_TREAT_DIALOG;

/**
 * ConfirmDialog is used for asking a confirmation to users regarding deletion and
 * reset of group.
 */
public class TreatConfirmDialogFragment extends DialogFragment {

    private final String TAG = "TreatConfirm_TAG";
    private TreatConfirmDialogListener mHost;
    private PlaceDto.Place mPlace;
    private FrameLayout mContainer;

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
        View v = inflater.inflate(R.layout.fragment_treat_dialog, null);

        mContainer = v.findViewById(R.id.treat_dialog_container);
        TextView mTreatMsgTv = v.findViewById(R.id.treatConfTv);
        TableLayout mTable = v.findViewById(R.id.treatGroupTableView);


        final WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
        final Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();
        final Group group = groups.getGroups().get(getArguments().getInt(GROUP_INDEX));

        int position = getArguments().getInt(PERSON_INDEX);
        String name = group.getPerson().get(position).getName();
        mTreatMsgTv.setText("Is " + name + getString(R.string.s_shout_confirmed));

        for (int i = 0; i < group.getPerson().size(); i++) {
            TableRow row = (TableRow) inflater.inflate(R.layout.attribute_row, null);
            TextView nameTv = (TextView) row.getVirtualChildAt(0);
            TextView treatTv = (TextView) row.getVirtualChildAt(1);
            nameTv.setText(group.getPerson().get(i).getName());
            treatTv.setText("" + group.getPerson().get(i).getCount());

            mTable.addView(row, i + 1);
        }


        // Build the dialog
        final int pos = position;
        final String finalName = name;
        builder.setTitle(R.string.confirm_treat)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onTreatClick(getArguments().getInt(CODE_TREAT_DIALOG));
                        Person person = group.getPerson().get(pos);
                        person.setCount(group.getPerson().get(pos).getCount() + 1);

                        Snackbar.make(mContainer, getString(R.string.thank_you) + " " + finalName + " :) !", Snackbar.LENGTH_SHORT).show();


                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm", getResources().getConfiguration().locale);
                        String date = df.format(Calendar.getInstance().getTime());

                        if (getArguments().getInt(CODE_TREAT_DIALOG) == RAND) {
                            List<PlaceDto.Place> places = new ArrayList<>();
                            Group group = groups.getGroups().get(getArguments().getInt(GROUP_INDEX));

                            mPlace = new PlaceDto.Place();
                            mPlace.setPlace(false);
                            mPlace.setName("N/A");
                            mPlace.getShout().add(person);
                            mPlace.setVisited_date(date);
                            places.add(mPlace);
                            group.getPlaces().add((group.getPlaces().size()), mPlace);

                            whoseTreatApplication.saveAllGroups(groups);

                        } else {
                            if (groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces() == null) { // if the shout never made
                                List<PlaceDto.Place> places = new ArrayList<>();
                                places.add(0, mPlace);
                                List<Person> persons = new ArrayList<>();
                                persons.add(groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPerson().get(getArguments().getInt(PERSON_INDEX)));
                                mPlace.setShout(persons);
                                mPlace.setVisited_date(date);
//                                Log.d(TAG, "set shout?: " + mPlace.getShout().get(0).getName());
                                groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).setPlaces(places);
//                                Log.d(TAG, "saved? " + groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces().contains(mPlace));
//                                Log.d(TAG, "person saved? " + groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces().get(0).getShout().get(0).getName());
                                whoseTreatApplication.saveAllGroups(groups);


                            } else {  // if the shout was made at some point in the past

                                groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces().add((groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces().size()), mPlace);
//                                Log.d(TAG, "saved? " + groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces().contains(mPlace));

                                int index = groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces().indexOf(mPlace);
                                mPlace = groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPlaces().get(index);

                                mPlace.getShout().add(groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPerson().get(getArguments().getInt(PERSON_INDEX)));
                                mPlace.setVisited_date(date);
//                                Log.d(TAG, "date saved?" + mPlace.getVisited_date());
                                whoseTreatApplication.saveAllGroups(groups);

                            }
                        }
                        whoseTreatApplication.saveAllGroups(groups);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
        mHost = (TreatConfirmDialogFragment.TreatConfirmDialogListener) context;
        mPlace = mHost.getPlace(getArguments().getInt(CODE_TREAT_DIALOG));
    }

    /**
     * interface for TreatConfirmDialogListener
     * defines one method which will be handled by host
     */
    public interface TreatConfirmDialogListener {
        void onTreatClick(int code);

        PlaceDto.Place getPlace(int code);
    }
}
