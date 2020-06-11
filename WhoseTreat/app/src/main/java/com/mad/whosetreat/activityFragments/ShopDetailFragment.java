package com.mad.whosetreat.activityFragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.activities.GroupDetailEditActivity;
import com.mad.whosetreat.dialog.TreatConfirmDialogFragment;
import com.mad.whosetreat.model.PlaceDto;
import com.squareup.picasso.Picasso;

import static com.mad.whosetreat.WhoseTreatApplication.API_KEY;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.PERSON_INDEX;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.SHOP_LIST_FRAGMENT;
import static com.mad.whosetreat.activityFragments.ShopListFragment.WHERE_TO_GO_FRAGMENT;

/**
 * ShopDetailFragment presents the selected shop's detail to users and provides link to the google map
 * to check the location and navigate to it.
 * A simple {@link Fragment} subclass.
 */
public class ShopDetailFragment extends Fragment {

    public static final String GROUP_INDEX = "group index";
    private static final String TAG = "ShopDetailFragment_TAG";
    private static final String PRICE = "price level";
    private static final String DISTANCE = "distance";

    private PlaceDto.Place mShop;
    private ImageView mShopImg;
    private TextView mNameTv;
    private TextView mBudgetTv;
    private TextView mDistanceTv;
    private TextView mOpeningTv;
    private TextView mAddressTv;
    private RatingBar mRating;
    private FloatingActionButton mMapFab;
    private Button mTreatBtn;
    private boolean mIsFromGroup = false;


    public ShopDetailFragment() {
        // Required empty public constructor
    }

    /**
     * new instance of the fragment.
     * distance and price text of the shop are parameters.
     *
     * @param dist
     * @param price
     * @param groupIndex
     * @param personIndex
     * @return
     */
    public static ShopDetailFragment newInstance(String dist, String price, int groupIndex, int personIndex) {

        Bundle args = new Bundle();
        args.putString(DISTANCE, dist);
        args.putString(PRICE, price);
        args.putInt(PERSON_INDEX, personIndex);
        args.putInt(GROUP_INDEX, groupIndex);
        ShopDetailFragment fragment = new ShopDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static ShopDetailFragment newInstance(String dist, String price) {

        Bundle args = new Bundle();
        args.putString(DISTANCE, dist);
        args.putString(PRICE, price);
        ShopDetailFragment fragment = new ShopDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }


    /**
     * Inflates the layout and binds to view items.
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shop_detail, container, false);

        // linking views
        mBudgetTv = rootView.findViewById(R.id.detail_budget_tv);
        mDistanceTv = rootView.findViewById(R.id.detail_distance_tv);
        mOpeningTv = rootView.findViewById(R.id.opening_Tv);
        mRating = rootView.findViewById(R.id.detail_rating);
        mAddressTv = rootView.findViewById(R.id.detail_vicinity_tv);
        mMapFab = rootView.findViewById(R.id.map_fab);
        mTreatBtn = rootView.findViewById(R.id.place_confirm_btn);

        if (!mIsFromGroup) {

            // if a person already treated group, set button disabled
            WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
            if (whoseTreatApplication.getTreat()) {
                mTreatBtn.setEnabled(false);
                mTreatBtn.setBackgroundColor(getResources().getColor(R.color.material_gray_300));
            }

            mTreatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: add codes to pop up dialog etc. showing the treat count board
//                    Log.d(TAG, "Group index: " + getArguments().getInt(GROUP_INDEX));
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    TreatConfirmDialogFragment customDialog = new TreatConfirmDialogFragment();
                    Bundle bdl = new Bundle();
//                    Log.d(TAG, "Person index @ details: " + getArguments().get(PERSON_INDEX));

                    bdl.putInt(GROUP_INDEX, getArguments().getInt(GROUP_INDEX));
                    bdl.putInt(PERSON_INDEX, getArguments().getInt(PERSON_INDEX));
                    customDialog.setArguments(bdl);
                    customDialog.show(getFragmentManager(), "TreatConfirmDialogFragment");
                }
            });
        } else {
            mTreatBtn.setVisibility(View.INVISIBLE);
        }

        if (!getArguments().getString(DISTANCE).equals("N/A")) {
            mDistanceTv.setText(getArguments().getString(DISTANCE) + " " + getString(R.string.distance_from_you));
        } else {
            mDistanceTv.setText(getArguments().getString(DISTANCE));
        }
        mRating.setActivated(false);
        mRating.setRating((float) mShop.getRating());
        mBudgetTv.setText(getArguments().getString(PRICE));
        mAddressTv.setText(mShop.getVicinity());


        // if opening hours reference exists, the textview will be set accordingly.
        // otherwise, default item_message will appear.
        try {
            if (mShop.getOpening_hours().isOpen_now()) {
                mOpeningTv.setText(R.string.shop_open_now);
                mOpeningTv.setTextColor(getResources().getColor(R.color.colorGreen));
            } else {
                mOpeningTv.setText(R.string.shop_close_now);
                mOpeningTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }

        } catch (Exception e) {

        }

        // link and set resources for image view
        mShopImg = rootView.findViewById(R.id.detail_img_1);

        mNameTv = rootView.findViewById(R.id.detail_title_Tv);
        mNameTv.setText(mShop.getName());

        // When fab clicked, map intent will be opened
        mMapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + mShop.getName());
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");
                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

        // try to set the picture to the view if the reference exists
        try {
            setPicture(mShop.getPhotos().get(0).getPhoto_reference(), mShopImg);
//            Log.d(TAG, "photo reference size = " + mShop.getPhotos().size());
        } catch (Exception e) {
            mShopImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            Log.d(TAG, "Exception");
        }
        return rootView;
    }

    /**
     * setting picture of the shop to the view
     *
     * @param reference
     * @param targetView
     */
    private void setPicture(String reference, ImageView targetView) {
        String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=500" +
                "&photoreference=" + reference +
                "&key=" + API_KEY;
        try {
            // setting picture of the shop using Picasso library
            Picasso.with(getActivity()).load(imageUrl).error(R.drawable.ic_title_icon).into(targetView);
            targetView.setMinimumHeight(100);
            targetView.setMinimumWidth(100);

        } catch (NullPointerException e) {
            // if no image reference available, default image will be set.
            targetView.setImageResource(R.drawable.ic_title_icon);
            targetView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    /**
     * on creation of the fragment, initData() will be called to initiate the fields
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }


    /**
     * get the shops data from the MenuSelectionFragment fragment then set the field with it.
     */
    private void initData() {
        ShopListFragment shopListFragment = (ShopListFragment) getFragmentManager().findFragmentByTag(SHOP_LIST_FRAGMENT);
        WhereToGoFragment whereToGoFragment = (WhereToGoFragment) getFragmentManager().findFragmentByTag(WHERE_TO_GO_FRAGMENT);

        if (whereToGoFragment != null) {
            mShop = whereToGoFragment.getShop();
        } else if (shopListFragment != null) {
            mShop = shopListFragment.getSelectedShop();
        } else {
            GroupDetailEditActivity activity = (GroupDetailEditActivity) getActivity();
            mShop = activity.getShopFromGroupHx();
            mIsFromGroup = true;
//            Log.d(TAG, "mShop? " + mShop.getName());
        }
    }

    public void setTreatBtn() {
        mTreatBtn.setEnabled(false);
        mTreatBtn.setBackgroundColor(getResources().getColor(R.color.material_gray_300));
//        Log.d(TAG, "treat btn changed @ shop detail");


    }

    public PlaceDto.Place getShop() {
        return mShop;
    }
}
