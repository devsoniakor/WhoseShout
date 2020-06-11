package com.mad.whosetreat.activityFragments;


import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.whosetreat.R;
import com.mad.whosetreat.adapter.ShopAdapter;
import com.mad.whosetreat.model.PlaceDto;

import java.util.ArrayList;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
import static com.mad.whosetreat.activities.MainActivity.MENU_FRAGMENT_TAG;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.PERSON_INDEX;
import static com.mad.whosetreat.activityFragments.ShopDetailFragment.GROUP_INDEX;

/**
 * ShopListFragment displays the list of shops in card views.
 * brief information regarding individual shops will be shown.
 */
public class ShopListFragment extends Fragment {

    public static final String WHERE_TO_GO_FRAGMENT = "WhereToGo Fragment";
    public static final String SELECTED_PERSON_S_NAME = "selected person's name";
    public static final String SHOP_DETAIL_FRAGMENT = "shop detail fragment";
    private static final String TAG = "ShopListFragment_TAG";

    private double mLat;
    private double mLng;
    private PlaceDto mPlaceDto;
    private RecyclerView mShopRv;
    private ShopAdapter mShopAdapter;
    private PlaceDto.Place mSelectedPlace;
    private TextView mGreetingTv;

    public ShopListFragment() {
        // Required empty public constructor
    }

    /**
     * Instantiate the fragment.
     *
     * @param name
     * @param personIndex
     * @param groupIndex
     * @return
     */
    public static ShopListFragment newInstance(String name, int groupIndex, int personIndex) {
        Bundle args = new Bundle();
        args.putString(SELECTED_PERSON_S_NAME, name);
        args.putInt(GROUP_INDEX, groupIndex);
        args.putInt(PERSON_INDEX, personIndex);
        ShopListFragment fragment = new ShopListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Inflating the view and binds them with models
     * RecyclerView for each shop items will be bound here.
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
        final View rootView = inflater.inflate(R.layout.fragment_shop_list, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mShopRv = rootView.findViewById(R.id.shopRv);
        mShopRv.setLayoutManager(mLayoutManager);
        mShopAdapter = new ShopAdapter(getContext(), mPlaceDto.getResults(), mLat, mLng);
        mShopRv.setAdapter(mShopAdapter);
        mShopAdapter.notifyDataSetChanged();

        mGreetingTv = rootView.findViewById(R.id.selected_greeting_tv);

        // when floating action button clicked, the app will randomise the shops and return the random suggestion
        FloatingActionButton fab = rootView.findViewById(R.id.random_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mShopAdapter.getShopNames();
                ArrayList<String> shops = new ArrayList<String>();
                shops.addAll(mShopAdapter.getShopNames());

                WhereToGoFragment fragment;

                fragment = WhereToGoFragment.newInstance(shops, getArguments().getInt(GROUP_INDEX), getArguments().getInt(PERSON_INDEX));

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .setTransition(TRANSIT_FRAGMENT_CLOSE).replace(R.id.container, fragment, WHERE_TO_GO_FRAGMENT).commit();

            }
        });

        if (!getArguments().getString(SELECTED_PERSON_S_NAME).equals("")) {
            // if the name is not equals to "", will get the argument and set the textview with it
            getActivity().setTitle(getArguments().getString(SELECTED_PERSON_S_NAME) + getString(R.string.s_treat));
            mGreetingTv.setText(getString(R.string.thank_you) + " " + getArguments().getString(SELECTED_PERSON_S_NAME) + getString(R.string.it_is_your_treat));
        } else {
            // otherwise, it is user's own treat
            mGreetingTv.setText(getString(R.string.your_treat));
        }

        // When the shop item (card view) is clicked, the onclickListener will instantiate the new fragment
        // showing the detail of the shop.
        mShopAdapter.setOnItemClickListener(new ShopAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View itemView, int position, PlaceDto.Place shop) {
                mSelectedPlace = shop;
                String dist = ((TextView) itemView.findViewById(R.id.distance_tv)).getText().toString();
                String price = ((TextView) itemView.findViewById(R.id.budget_tv)).getText().toString();
                ShopDetailFragment detailFragment = ShopDetailFragment.newInstance(dist, price,
                        getArguments().getInt(GROUP_INDEX), getArguments().getInt(PERSON_INDEX));
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .setTransition(TRANSIT_FRAGMENT_CLOSE).replace(R.id.container, detailFragment, SHOP_DETAIL_FRAGMENT).commit();
            }
        });

        return rootView;
    }


    /**
     * on creation of the fragment, initData() will be called for initiating the fields from
     * the previous activity
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onDestroy() {
        getActivity().setTitle(getContext().getString(R.string.app_name));
        super.onDestroy();
    }

    /**
     * get the shops data from the previous fragment then set the field with it.
     * information regarding the geographic location will be initiated for calculating the distance.
     */
    private void initData() {
        MenuSelectionFragment fragment = (MenuSelectionFragment) getFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);
        mPlaceDto = fragment.getShopsResult();
        mLat = fragment.latitude;
        mLng = fragment.longitude;
    }

    /**
     * return the selected place available to other classes
     *
     * @return
     */
    public PlaceDto.Place getSelectedShop() {
        return mSelectedPlace;
    }
}
