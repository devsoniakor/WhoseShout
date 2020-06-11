package com.mad.whosetreat.activityFragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.dialog.TreatConfirmDialogFragment;
import com.mad.whosetreat.model.PlaceDto;
import com.mad.whosetreat.utilClass.Distance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
import static com.mad.whosetreat.WhoseTreatApplication.API_KEY;
import static com.mad.whosetreat.activities.MainActivity.MENU_FRAGMENT_TAG;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.PERSON_INDEX;
import static com.mad.whosetreat.activityFragments.ShopDetailFragment.GROUP_INDEX;

/**
 * WhereToGoFragment is for random draw for the place within available list
 * Optional flow which is initiated when user clicks shuffle floating button
 */
public class WhereToGoFragment extends Fragment {


    public static final int WTG = 1;
    public static final String CODE_TREAT_DIALOG = "code_treat_dialog";
    private static final String SHOPS = "Random Shops";
    private static final String TAG = "TAG: WhereToGoFragment";
    private static final String PLACE_FOR_GROUP = "selected place for group";

    private TextView mShopsTv;
    private TextView mShopsMsgTv;
    private RelativeLayout mRanShopLayout;
    private LinearLayout mRanShopProgressLayout;
    private Button mDetailBtn;
    private Button mTreatBtn;
    private ImageView mShopImage;
    private PlaceDto mPlaceDto;
    private PlaceDto.Place mShop;
    private double mLat;
    private double mLng;
    private boolean flag;

    public WhereToGoFragment() {
        // Required empty public constructor
    }

    /**
     * takes in String ArrayList representing shops' name and creates new instance of fragment
     *
     * @param shops
     * @return
     */
    public static WhereToGoFragment newInstance(ArrayList<String> shops, int groupIndex, int personIndex) {
        Bundle args = new Bundle();
        args.putInt(GROUP_INDEX, groupIndex);
        args.putInt(PERSON_INDEX, personIndex);
        args.putStringArrayList(SHOPS, shops);
        WhereToGoFragment fragment = new WhereToGoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * inflates view and links widgets
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflates layout
        View rootView = inflater.inflate(R.layout.fragment_where_to_go, container, false);
        // Link TextView showing the random result
        mShopsTv = rootView.findViewById(R.id.randShopTv);
        // Link TextView showing a item_message that will be prepended to the mShopsTv
        mShopsMsgTv = rootView.findViewById(R.id.shopMsgTv);

        // Link confirm button and ImageView
        mDetailBtn = rootView.findViewById(R.id.confShopBtn);
        mShopImage = rootView.findViewById(R.id.ranShopIv);
        mTreatBtn = rootView.findViewById(R.id.treatShopBtn);

        // if a person already treated group, set button disabled
        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
        if (whoseTreatApplication.getTreat()) {
            mTreatBtn.setEnabled(false);
            mTreatBtn.setBackgroundColor(getResources().getColor(R.color.material_gray_300));
        }

        // on confirm button click event, this method will open a new map intent showing the location of the shop
        mDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // initiate the Distance object for calculating the distance
                Distance distance = new Distance(mLat, mLng, mShop.getGeometry().getLocation().getLat(), mShop.getGeometry().getLocation().getLng());
                String dist = "" + distance.getDistance() + " M";
                // setting the price tag in accordance with the returned value from Google Places API
                String priceLv = "";
                switch (mShop.getPrice_level()) {
                    case 0:
                        priceLv = "-";
                        break;
                    case 1:
                        priceLv = "$";
                        break;
                    case 2:
                        priceLv = "$$";
                        break;
                    case 3:
                        priceLv = "$$$";
                        break;
                    case 4:
                        priceLv = "$$$$";
                        break;
                }

                ShopDetailFragment detailFragment = ShopDetailFragment.newInstance(dist, priceLv, getArguments().getInt(GROUP_INDEX), getArguments().getInt(PERSON_INDEX));
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .setTransition(TRANSIT_FRAGMENT_CLOSE).replace(R.id.container, detailFragment).commit();

            }
        });

        mTreatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                TreatConfirmDialogFragment customDialog = new TreatConfirmDialogFragment();
                Bundle bdl = new Bundle();
                bdl.putInt(CODE_TREAT_DIALOG, WTG);
                bdl.putInt(GROUP_INDEX, getArguments().getInt(GROUP_INDEX));
                bdl.putInt(PERSON_INDEX, getArguments().getInt(PERSON_INDEX));
                bdl.putString(PLACE_FOR_GROUP, mShop.getName());
                customDialog.setArguments(bdl);
                customDialog.show(getFragmentManager(), "TreatConfirmDialogFragment");
            }
        });

        // Link layouts: one for result and the other for progress view
        mRanShopLayout = rootView.findViewById(R.id.ranShopResultLayout);
        mRanShopProgressLayout = rootView.findViewById(R.id.ranShopProgressLayout);

        // instantiate AsyncTask
        new WhereToGoFragment.RanShopAsyncTask(getArguments().getStringArrayList(SHOPS)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Inflate the layout for this fragment
        return rootView;
    }

    public PlaceDto.Place getShop() {
        return mShop;
    }

    /**
     * on creation of the view, this method will call the initData() to populate own fields
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
     * so that it can pass the location information to the next fragment
     */
    private void initData() {
        MenuSelectionFragment fragment = (MenuSelectionFragment) getFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);
        mPlaceDto = fragment.getShopsResult();
        mLat = fragment.latitude;
        mLng = fragment.longitude;
    }

    public void setTreatBtn() {
        mTreatBtn.setEnabled(false);
        mTreatBtn.setBackgroundColor(getResources().getColor(R.color.material_gray_300));
        flag = false;
    }


    /**
     * Private inner class RanShopAsyncTask will sleep UI thread for seconds then shows the progressbar
     * before it returns the random shop result.
     */
    private class RanShopAsyncTask extends AsyncTask<Void, Void, String> {

        private ArrayList<String> mShops = new ArrayList<>();
        /**
         * constructor takes in ArrayList of Strings and add it to its own list
         *
         * @param shops
         */
        public RanShopAsyncTask(ArrayList<String> shops) {
            for (int i = 0; i < shops.size(); i++) {
                mShops.add(shops.get(i));
            }
        }

        /**
         * hides the result display and shows the progress layout that contains a TextView
         * (displays the item_message while it generates random shop) and a ProgressBar.
         */
        @Override
        protected void onPreExecute() {
            mRanShopLayout.setVisibility(View.GONE);
            mRanShopProgressLayout.setVisibility(View.VISIBLE);
        }

        /**
         * sleeps UI thread for 2 seconds then
         * return the randomly chosen shop's name
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                Random random = new Random();
                int number = random.nextInt(mShops.size());
                mShop = mPlaceDto.getResults().get(number);
                return mShop.getName();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * takes in a string representing the shop's name then sets the TextView using the result.
         * hides the progress display and shows the result display
         *
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            mRanShopLayout.setVisibility(View.VISIBLE);
            mRanShopProgressLayout.setVisibility(View.GONE);
            mShopsTv.setText(s);
            mShopsMsgTv.setVisibility(View.VISIBLE);
            try {
                // setting up the image view for the shop's picture
                String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "maxwidth=500" +
                        "&photoreference=" + mShop.getPhotos().get(0).getPhoto_reference() +
                        "&key=" + API_KEY;

                Picasso.with(getActivity()).load(imageUrl).into(mShopImage);
                mShopImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } catch (NullPointerException e) {
                mShopImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }


}
