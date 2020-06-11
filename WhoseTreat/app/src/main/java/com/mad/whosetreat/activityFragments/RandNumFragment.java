package com.mad.whosetreat.activityFragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.dialog.TreatConfirmDialogFragment;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;
import com.mad.whosetreat.model.Person;

import java.util.ArrayList;
import java.util.Random;

import static android.view.View.GONE;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.PERSON_INDEX;
import static com.mad.whosetreat.activityFragments.ShopDetailFragment.GROUP_INDEX;
import static com.mad.whosetreat.activityFragments.WhereToGoFragment.CODE_TREAT_DIALOG;

/**
 * RandNumFragment handles random number drawing event in which user did not select specific group
 * and/or use random draw without using location based search service.
 */
public class RandNumFragment extends Fragment {

    public static final int RAND = 2;
    private static final String CODE = "Code";
    private static final String TAG = "TAG";
    private static final String RESULT = "Random Result";
    private static final String SHOPS = "Shop list";
    private static final String NUMBER = "Number of People";
    private static final String PEOPLE = "People";
    private static final String FLAG = "Flag for location";
    public String mSelectedName = "";
    public int mSelectedIndex = 0;
    private TextView mNumberTv;
    private RelativeLayout mRanNumLayout;
    private LinearLayout mRanNumProgressLayout;
    private Button mTreatBtn;
    private Boolean flag = false;

    public RandNumFragment() {
        // Required empty public constructor
    }

    /**
     * sets bundle of fragment with parameters
     *
     * @param people
     * @param flag
     * @return
     */
    public static RandNumFragment newInstance(ArrayList<Person> people, int groupIndex, boolean flag) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(PEOPLE, people);
        args.putInt(GROUP_INDEX, groupIndex);
        RandNumFragment fragment = new RandNumFragment();
        fragment.setArguments(args);
        args.putBoolean(FLAG, flag);

        return fragment;
    }


    /**
     * inflates the layout and links view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rand_num, container, false);

        // Link the confirmBtn
        mTreatBtn = rootView.findViewById(R.id.rand_num_treatBtn);
        // set the textView with the generated random number
        mNumberTv = rootView.findViewById(R.id.randomNumTv);
        // Link layout for result display
        mRanNumLayout = rootView.findViewById(R.id.ranNumResultLayout);
        // Link layout for progress display
        mRanNumProgressLayout = rootView.findViewById(R.id.ranNumProgressLayout);


        // if there is saved instance state, the number textview will be set to the previously drawn name/number
        if (savedInstanceState != null) {
            mNumberTv.setText(savedInstanceState.getString(RESULT));
        } else {
//             set OnClickListener for confirm Button: moves to the next fragments (shops)
            mTreatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    TreatConfirmDialogFragment customDialog = new TreatConfirmDialogFragment();
                    Bundle bdl = new Bundle();
                    bdl.putInt(CODE_TREAT_DIALOG, RAND);
                    bdl.putInt(GROUP_INDEX, getArguments().getInt(GROUP_INDEX));
                    bdl.putInt(PERSON_INDEX, mSelectedIndex);
                    customDialog.setArguments(bdl);
                    customDialog.show(getFragmentManager(), "TreatConfirmDialogFragment");


                }
            });

            if (mSelectedName.equals("")) {
                // instantiate AsyncTask
                new RanNumAsyncTask(getArguments().getStringArrayList(PEOPLE).size()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            } else {
                mNumberTv.setText(getString(R.string.thanks) + " " + mSelectedName + getString(R.string.rand_msg_tail));
            }

        }
        return rootView;
    }

    /**
     * as soon as user leave the page, will destroy the view so that user cannot turn back and is gone through
     * another draw
     */
    @Override
    public void onPause() {
        super.onPause();
        onDestroyView();
    }

    @Override
    public void onDestroy() {
        getActivity().setTitle(getContext().getString(R.string.app_name));
        super.onDestroy();
    }

    /**
     * is used for saving the drawing result
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.i(TAG, "onSaveInstanceState!!");
        outState.putString(RESULT, mNumberTv.getText().toString());
    }

    /**
     * change the button state if group confirm the treat.
     */
    public void setTreatBtn() {
        mTreatBtn.setEnabled(false);
        mTreatBtn.setBackgroundColor(getResources().getColor(R.color.material_gray_300));
    }


    /**
     * Private inner class RanNumAsyncTask will sleep UI thread for seconds then shows the progressbar
     * before it returns the random number result.
     */
    private class RanNumAsyncTask extends AsyncTask<Void, Void, String> {

        int mNumber;

        /**
         * constructor receives an integer that will be used for the maximum number of random selection.
         *
         * @param number
         */
        public RanNumAsyncTask(int number) {
            mNumber = number;
        }

        /**
         * hides the result display and shows the progress layout that contains a TextView
         * (displays the item_message while it generates random number) and a ProgressBar.
         */
        @Override
        protected void onPreExecute() {
            mRanNumProgressLayout.setVisibility(View.VISIBLE);
            mRanNumLayout.setVisibility(GONE);
            super.onPreExecute();
        }

        /**
         * sleeps UI thread for 2 seconds then
         * generates a random integer then return it
         *
         * @param mVoid
         * @return
         */
        @Override
        protected String doInBackground(Void... mVoid) {
            try {
                Thread.sleep(2000);
                WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
                Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();
                Person selectedPerson;
                Group group = groups.getGroups().get(getArguments().getInt(GROUP_INDEX));

                if (group.isRandom()) {
                    if (group.isRandom()) {
                        Random random = new Random();
                        int ranNum = random.nextInt(group.getPerson().size());
                        selectedPerson = group.getPerson().get(ranNum);
                        mSelectedName = selectedPerson.getName();
                        mSelectedIndex = ranNum;
                    }
                } else {
                    selectedPerson = group.getPerson().get(0);

                    int min = selectedPerson.getCount();
                    int index = 0;
                    if (group.getPerson().size() > 1) {
                        for (int i = 1; i < group.getPerson().size(); i++) {
                            Person person = group.getPerson().get(i);
                            if (person.getCount() <= min) {
                                selectedPerson = person;
                                index = i;
                            }
                        }
                        mSelectedIndex = index;
                        mSelectedName = selectedPerson.getName();
//                    Log.d(TAG, "Person index @ randNum: " + mSelectedIndex);
                    }

                }

                return mSelectedName;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * hides the progress layout (TextVies and ProgressBar) and shows the result layout
         * with random number and a item_message.
         *
         * @param name
         */
        @Override
        protected void onPostExecute(String name) {
            try {
                mRanNumProgressLayout.setVisibility(View.GONE);
                mRanNumLayout.setVisibility(View.VISIBLE);
                mNumberTv.setText(getString(R.string.thanks) + " " + mSelectedName + getString(R.string.rand_msg_tail));
                getActivity().setTitle(name + getString(R.string.s_treat));

            } catch (IllegalStateException e) {
//                Log.d(TAG, e.getMessage());
            }
        }
    }


}
