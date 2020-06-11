package com.mad.whosetreat.activityFragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.model.Place;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.dialog.LocDialogFragment;
import com.mad.whosetreat.dialog.NewSearchDialogFragment;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;
import com.mad.whosetreat.model.Person;
import com.mad.whosetreat.model.PlaceDto;
import com.mad.whosetreat.utilClass.Distance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import static com.mad.whosetreat.WhoseTreatApplication.API_KEY;
import static com.mad.whosetreat.activities.MainActivity.PLACE;

/**
 * MenuSelectionFragment allows users to select the category of menu and based on the location,
 * will search the nearby places then leads users to the next fragments.
 * In this fragment, random selection of the person treat for the group will be decided unless
 * user just want to draw a random number without using location based service
 * <p>
 * A simple {@link Fragment} subclass.
 */
public class MenuSelectionFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // constants
    public static final String VIEW_ID = "clicked view id";
    public static final String RADIUS = "Radius";
    public static final String MSG = "Message";
    public static final String SHOP_LIST_FRAGMENT = "ShopList fragment";
    public static final String RAN_NUM_FRAGMENT = "RanNumFragment";
    public static final String PERSON_INDEX = "selected person index";
    public static final String RAND_NUM_FRAGMENT = "random number fragment";
    private static final String NUMBER = "Number of people";
    private static final String TAG = "MenuSelection TAG";
    private static final String LUNCH = "restaurant";
    private static final String COFFEE = "cafe";
    private static final String DRINK = "bar";
    private static final String LOCATION_KEY = "location key";

    // For making URL
    private static final String URL_FRONT = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private static final String RADIUS_TAG = "&radius=";
    private static final String TYPE_TAG = "&type=";
    private static final String PEOPLE = "names";
    private static final String GROUP_NAME = "group name";
    private static final String GROUP_INDEX = "group index";
    // Json to Java object
    private static PlaceDto mShopsResult;
    private static Distance mDistance;
    // Location related fields
    private final long LOC_UPDATE_INTERVAL = 10000; // 10s in milliseconds
    private final long LOC_FASTEST_UPDATE = 5000; // 5s in milliseconds
    public double longitude;
    public double latitude;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocRequest;
    protected Location mCurLocation;
    private Group group;
    private LocationManager mLocManager;
    private ArrayList<String> mShops = new ArrayList<>();
    private GoogleMap mMap;
    // Views
    private ImageButton mCoffeeBtn;
    private ImageButton mLunchBtn;
    private ImageButton mDrinkBtn;
    private Spinner mMeterSpinner;
    private int mRadius;
    private boolean flag;
    private String mName = ""; //selected person's name
    private int mUrlId;


    public MenuSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * New instance of MenuSelectionFragment
     * set its argument parcelable arrayList with passed Person list
     *
     * @param group
     * @return
     */
    public static MenuSelectionFragment newInstance(ArrayList<Person> group) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PEOPLE, group);
        MenuSelectionFragment fragment = new MenuSelectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MenuSelectionFragment newInstance(int index) {

        Bundle args = new Bundle();
        args.putInt(GROUP_INDEX, index);
        MenuSelectionFragment fragment = new MenuSelectionFragment();
        fragment.setArguments(args);

        return fragment;
    }


    /**
     * inflates layout and links widgets
     * connect to GoogleService and create LocationRequest for location updates
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
        View rootView = inflater.inflate(R.layout.fragment_menu_selec, container, false);

        mMeterSpinner = rootView.findViewById(R.id.meterSpinner);

        // user can selects the radius of search
        mMeterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRadius = (position + 1) * 200;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mRadius = 200;
            }
        });

        mCoffeeBtn = rootView.findViewById(R.id.coffeeBtn);
        mLunchBtn = rootView.findViewById(R.id.lunchBtn);
        mDrinkBtn = rootView.findViewById(R.id.drinkBtn);

        // based on the user choice on menu category as well as the radius, the fragment will dynamically
        // generate corresponding URL for search
        mCoffeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                menuOnClickHandler(v);
            }
        });
        mLunchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                menuOnClickHandler(v);
            }
        });
        mDrinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                menuOnClickHandler(v);
            }
        });

        // sets current location -> if there is savedInstanceState exists, sets location accordingly
        if (savedInstanceState != null) {
            mCurLocation = savedInstanceState.getParcelable(LOCATION_KEY);
        } else {
            mCurLocation = null;
        }

        // Create the Play Services client object.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


        // create LocationRequest for location updates
        mLocRequest = new LocationRequest();
        mLocRequest.setInterval(LOC_UPDATE_INTERVAL);
        mLocRequest.setFastestInterval(LOC_FASTEST_UPDATE);
        mLocRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return rootView;
    }

    /**
     * handles onclick event of each menu icon
     * checks current location or current Latitude and Longitude
     * If those are valid, will call setUrlForAsync() to form URL
     *
     * @param v
     * @throws Settings.SettingNotFoundException
     */
    private void menuOnClickHandler(View v) {
        int id = v.getId();

        // check if current location is available
        if (mCurLocation == null) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            } else {
                //if permission is granted, request location updates and set the location accordingly
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocRequest, this);

                mCurLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurLocation != null) {
                    longitude = mCurLocation.getLongitude();
                    latitude = mCurLocation.getLatitude();
                }
            }

            // if still the location is unknown, the dialog will pop up to ask user for a choice
            if (latitude == 0 || longitude == 0) {
                LocDialogFragment customDialog = new LocDialogFragment();
                customDialog.show(getFragmentManager(), "CustomDialogFragment");
                mUrlId = id;
            }

        } else {
            longitude = mCurLocation.getLongitude();
            latitude = mCurLocation.getLatitude();
        }

        if (longitude != 0 && latitude != 0) {
            setUrlForAsync(id, mRadius);
        }
    }

    /**
     * sets URL for passing AsyncTask retrieving places list information.
     * Depending on user's selection, search key will be changed.
     *
     * @param id
     */
    public void setUrlForAsync(int id, int radius) {

        // Create a Url from an intent string.
        String url = URL_FRONT + latitude + "," + longitude + RADIUS_TAG + radius + TYPE_TAG;
        
        switch (id) {
            case R.id.coffeeBtn:
                url += COFFEE + "&key=" + API_KEY;
                break;

            case R.id.lunchBtn:
                url += LUNCH + "&key=" + API_KEY;
                break;

            case R.id.drinkBtn:
                url += DRINK + "&key=" + API_KEY;
                break;
        }

        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
        Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();

        // calling NearbyAsyncTask for search
        try {
            NearbyAsyncTask placesTask;
            if (groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPerson().size() <= 1) {
                placesTask = new NearbyAsyncTask(url, id, radius, false);

            } else {
                placesTask = new NearbyAsyncTask(url, id, radius, true);
            }
            placesTask.execute();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * commit new fragment depending on string argument (name of person)
     * if there is only one person, commits WhereToGo fragment.
     * Otherwise, commits RanNumFragment.
     *
     * @param name : name of person
     */
    private void beginNewFragment(String name, int personIndex) {

        ShopListFragment shopListFragment;
        FragmentManager fragmentManager = getFragmentManager();


        shopListFragment = ShopListFragment.newInstance(name, getArguments().getInt(GROUP_INDEX), personIndex);

        try {
            fragmentManager.beginTransaction().addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).replace(R.id.container, shopListFragment, SHOP_LIST_FRAGMENT).commit();

        } catch (IllegalStateException e) {
            flag = true;
            mName = name;
        } catch (NullPointerException e) {
        }
    }

    /**
     * after connected to GoogleApi service, this method will be called.
     * this method will check the permission then call startLocationUpdates()
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }

    }

    /**
     * starts location update
     * check whether permission is granted or GoogleApiClient is connected as well
     */
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocRequest, this);
        mCurLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }


    /**
     * if GoogleApiClient is disconnected, this method will reconnect to the service.
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // updates the location if there is a change
    @Override
    public void onLocationChanged(Location location) {
        mCurLocation = location;
    }

    // Other classes can access to the result of shop lists via this method
    public PlaceDto getShopsResult() {
        return mShopsResult;
    }


    /**
     * Lifecycle events
     */
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().getParcelable(PLACE) != null) {
        }
        if (flag) {
            // if user opens other activity (e.g. Group Info), the fragment won't be initiated in the "beginNewFragment" method
            // as the fragment will be undergone "onSaveInstance" state. This case, when it is resumed,
            // will instantiate the fragment again here.
            ShopListFragment shopListFragment = (ShopListFragment) getFragmentManager().findFragmentByTag(SHOP_LIST_FRAGMENT);
            FragmentManager fragmentManager = getFragmentManager();

            if (shopListFragment == null) {
                shopListFragment = ShopListFragment.newInstance(mName, getArguments().getInt(GROUP_INDEX), getArguments().getInt(GROUP_INDEX));
                fragmentManager.beginTransaction().addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).replace(R.id.container, shopListFragment, SHOP_LIST_FRAGMENT).commit();
            }


        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * is called by MainActivity when user provides their location manually.
     * sets latitude and longitude
     *
     * @param place
     */

    public void setLatLngByPlace(Place place) {
        Log.d(TAG, "place? " + place);
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
        setUrlForAsync(mUrlId, mRadius);
    }

    /**
     * When user press the cancel button on the dialogue, it will check the number of people and if
     * the number is larger than 1, will return the randomised result for the person who treat for the group.
     * Otherwise, shows the toast item_message.
     */
    public void onCancelClick() {
        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
        Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();

        FragmentManager fragmentManager = getFragmentManager();
        RandNumFragment ranNumFragment = RandNumFragment.
                newInstance((ArrayList<Person>) groups.getGroups().get(getArguments().getInt(GROUP_INDEX)).getPerson(),
                        getArguments().getInt(GROUP_INDEX), false);
        fragmentManager.beginTransaction().addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).replace(R.id.container, ranNumFragment, RAND_NUM_FRAGMENT).commit();

    }

    /**
     * when attached to the MainActivity, initialise LocationManager
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * saves current location to the bundle
     *
     * @param savedInstanceState
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(LOCATION_KEY, mCurLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void showNoResultDialog(int id, int radius) {
        // if there is no shops available within the given radius, the dialog will appear and
        //ask user whether he/she wants to extend the search increased by 200 meters.
        try {
            NewSearchDialogFragment newSearchDialog = new NewSearchDialogFragment();
            Bundle bdl = new Bundle();
            bdl.putInt(VIEW_ID, id);
            bdl.putInt(RADIUS, radius);
            bdl.putString(MSG, getString(R.string.alert_msg_front) + " " + radius + getString(R.string.alert_msg_middle) + " " + (radius + 200) + getString(R.string.alert_msg_tail));
            newSearchDialog.setArguments(bdl);
            newSearchDialog.show(getFragmentManager(), "newSearchDialog");

        } catch (IllegalStateException e) {
        }
    }


    /**
     * NearbyAsyncTask loads the nearby places data from Google map
     * Then it will call beginNewFragment() method to commit new fragment
     */
    private class NearbyAsyncTask extends AsyncTask<Object, Object, PlaceDto> {

        private URL mGoogleMapUrl;
        private int mId;
        private int mRadius;
        private ProgressBar mFindingPb;
        private RelativeLayout mLayout;
        private TextView mLoadingTv;
        private Spinner mRadiusSpinner;
        private SwitchCompat mOpenSwitch;

        /**
         * constructor takes in Url for retrieving places information
         *
         * @param gmUrl
         * @param id
         * @param radius
         * @throws MalformedURLException
         */
        public NearbyAsyncTask(String gmUrl, int id, int radius, boolean flag) throws MalformedURLException {
            mGoogleMapUrl = new URL(gmUrl);
            mId = id;
            mRadius = radius;
        }

        // link to the view items and set visibilities or text
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFindingPb = getView().findViewById(R.id.menu_selec_pb);
            mLoadingTv = getView().findViewById(R.id.loadingTv);
            mLayout = getView().findViewById(R.id.menu_selec_layout);
            mRadiusSpinner = getView().findViewById(R.id.meterSpinner);
            mOpenSwitch = getView().findViewById(R.id.openSwitch);
            mOpenSwitch.setVisibility(View.GONE);
            mRadiusSpinner.setVisibility(View.GONE);
            mFindingPb.setVisibility(View.VISIBLE);
            mLoadingTv.setVisibility(View.VISIBLE);

            //TODO: radius & meter space
            mLoadingTv.setText(getString(R.string.loading_msg_front) + " " + mRadius + getString(R.string.loading_msg_tail));
            mLayout.setVisibility(View.INVISIBLE);
        }

        /**
         * loads the places from google as Json then makes a java object by Gson and return it
         *
         * @param params
         * @return
         */
        @Override
        protected PlaceDto doInBackground(Object... params) {

            try {

                Thread.sleep(2000);

                HttpsURLConnection conn = (HttpsURLConnection) mGoogleMapUrl.openConnection();

                String s = "";
                Gson gson = new GsonBuilder().create();

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder sb = new StringBuilder(s);

                while ((s = br.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");

                }

                br.close();

                // making java object from Json (using Gson)
                mShopsResult = gson.fromJson(sb.toString(), PlaceDto.class);

            } catch (IOException e) {
                e.printStackTrace();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return mShopsResult;
        }

        /**
         * add the name of shops to the mShops list
         *
         * @param place
         */
        @Override
        protected void onPostExecute(PlaceDto place) {

            mFindingPb.setVisibility(View.INVISIBLE);
            mLoadingTv.setVisibility(View.INVISIBLE);

            if (place.getResults().size() != 0) {

                List<PlaceDto.Place> removeObject = new ArrayList<>();
                for (int i = 0; i < place.getResults().size(); i++) {
                    if (mOpenSwitch.isChecked()) {
                        try {
                            if (!place.getResults().get(i).getOpening_hours().isOpen_now()) {
                                removeObject.add(place.getResults().get(i));

                            } else {
                                mShops.add(place.getResults().get(i).getName());

                            }
                        } catch (NullPointerException e) {
                        }
                    } else {
                        mShops.add(place.getResults().get(i).getName());
                    }
                }

                if (removeObject.size() != 0) {
                    for (int i = 0; i < removeObject.size(); i++) {
                        place.getResults().remove(removeObject.get(i));
                    }
                    mShopsResult = place;
                }
                if (place.getResults().size() == 0) {
                    showNoResultDialog(mId, mRadius);
                } else {

                    String selectedName = "";
                    Person selectedPerson;
                    int personIndex = 0;

                    try {
                        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getActivity().getApplication();
                        Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();
                        Group group = groups.getGroups().get(getArguments().getInt(GROUP_INDEX));

                        //if the number of people is more than 1, the random number/person will be selected here.
                        if (group.getPerson().size() > 1) {

                            selectedPerson = group.getPerson().get(1);

                            // if user is not using saved group but arbitrary number draw, this will selects the random number
                            if (selectedPerson.getName().equals("1")) {
                                Random random = new Random();
                                int ranNum = random.nextInt(group.getPerson().size());
                                selectedPerson = group.getPerson().get(ranNum);
                                selectedName = selectedPerson.getName();
                                whoseTreatApplication.saveAllGroups(groups);

                            } else {
                                if (group.isRandom()) {
                                    Random random = new Random();
                                    int ranNum = random.nextInt(group.getPerson().size());
                                    selectedPerson = group.getPerson().get(ranNum);
                                    selectedName = selectedPerson.getName();
                                    personIndex = ranNum;

                                } else {

                                    // otherwise, the person who has minimum treat number will be selected
                                    // as this list is not sorted, it is not alphabetical order and relatively random
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
                                        personIndex = index;
                                        selectedName = selectedPerson.getName();
                                    }
                                }

                            }
                        }

                        beginNewFragment(selectedName, personIndex);

                    } catch (NullPointerException e) {
//                    Log.d(TAG, e.getMessage());
                    }
                }
            } else {
                showNoResultDialog(mId, mRadius);
            }

            super.onPostExecute(place);
        }
    }
}
