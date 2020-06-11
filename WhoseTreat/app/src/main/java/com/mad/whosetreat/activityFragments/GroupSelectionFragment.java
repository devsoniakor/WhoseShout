package com.mad.whosetreat.activityFragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.activities.AddMemberActivity;
import com.mad.whosetreat.adapter.GroupAdapter;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
import static com.mad.whosetreat.activities.MainActivity.MENU_FRAGMENT_TAG;

/**
 * GroupSelectionFragment allows user to select exiting group to draw random person
 * or adding new group and/or members to the list.
 */
public class GroupSelectionFragment extends Fragment {


    public static final String SELECTED_GROUP = "selected group";
    public static final int REQUEST_ACT_ADD_GROUP = 3001;
    private static final String TAG = "GroupPreference_TAG";
    public GroupAdapter groupAdapter;
    public TextView noGroupTv;
    private RecyclerView mGroupRv;
    private CoordinatorLayout mContainer;



    public GroupSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * new instnace of fragment
     *
     * @return
     */
    public static GroupSelectionFragment newInstance() {
        Bundle args = new Bundle();
        GroupSelectionFragment fragment = new GroupSelectionFragment();
        fragment.setArguments(args);

//        Log.d(TAG, "GroupSelectionFragment");

        return fragment;
    }

    /**
     * inflates the layout and link or set listener to the view
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
        View rootView = inflater.inflate(R.layout.fragment_group_selection, container, false);
        mContainer = rootView.findViewById(R.id.group_selection_container);

        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
        Groups groupsFromSharedPreferences = whoseTreatApplication.getGroupsFromSharedPreferences();
        whoseTreatApplication.setTreat(false);

        Snackbar.make(mContainer,  R.string.enter_msg, Snackbar.LENGTH_SHORT).show();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mGroupRv = rootView.findViewById(R.id.groupRv);
        groupAdapter = new GroupAdapter(getActivity(), groupsFromSharedPreferences.getGroups());
        mGroupRv.setAdapter(groupAdapter);
        mGroupRv.setLayoutManager(layoutManager);
        groupAdapter.notifyDataSetChanged();

        noGroupTv = rootView.findViewById(R.id.title_tv10);
        if(groupAdapter.getItemCount()==0){
            noGroupTv.setVisibility(View.VISIBLE);
        }

        // if user selects a group, this will instantiate a new MenuSelectionFragment for further flow
        groupAdapter.setOnGroupClickListener(new GroupAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClicks(View itemView, Group group, int position) {

                MenuSelectionFragment fragment;

                fragment = MenuSelectionFragment.newInstance(position);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).setTransition(TRANSIT_FRAGMENT_CLOSE)
                        .replace(R.id.container, fragment, MENU_FRAGMENT_TAG).commit();

            }
        });

        FloatingActionButton fab = rootView.findViewById(R.id.addMemberFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Intent intent = new Intent(getActivity(), AddMemberActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_ACT_ADD_GROUP);

            }
        });

        return rootView;

    }


    /**
     * If user adds the new group to the list by clicking the fab, onResume of this fragment notifies
     */
    @Override
    public void onResume() {
        // if user deletes the group after instantiate this fragment then return to here again,
        // adapter should be notified about possible changes.
        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
        Groups groupsFromSharedPreferences = whoseTreatApplication.getGroupsFromSharedPreferences();
        if (groupAdapter.getItemCount()!= groupsFromSharedPreferences.getGroups().size()){
            groupAdapter.changeList(groupsFromSharedPreferences.getGroups());
            if(groupsFromSharedPreferences.getGroups().size()==0){
                noGroupTv.setVisibility(View.VISIBLE);
            } else {
                noGroupTv.setVisibility(View.INVISIBLE);
            }
        }
        super.onResume();
    }
}
