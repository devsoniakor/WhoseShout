package com.mad.whosetreat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.activityFragments.GroupSelectionFragment;
import com.mad.whosetreat.activityFragments.MenuSelectionFragment;
import com.mad.whosetreat.activityFragments.RandNumFragment;
import com.mad.whosetreat.activityFragments.ShopDetailFragment;
import com.mad.whosetreat.activityFragments.WhereToGoFragment;
import com.mad.whosetreat.dialog.LocDialogFragment;
import com.mad.whosetreat.dialog.NewGroupDialogueFragment;
import com.mad.whosetreat.dialog.NewSearchDialogFragment.NewSearchDialogFragmentListener;
import com.mad.whosetreat.dialog.TreatConfirmDialogFragment;
import com.mad.whosetreat.model.Groups;
import com.mad.whosetreat.model.Person;
import com.mad.whosetreat.model.PlaceDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
import static com.mad.whosetreat.activityFragments.MenuSelectionFragment.RAND_NUM_FRAGMENT;
import static com.mad.whosetreat.activityFragments.RandNumFragment.RAND;
import static com.mad.whosetreat.activityFragments.ShopListFragment.SHOP_DETAIL_FRAGMENT;
import static com.mad.whosetreat.activityFragments.ShopListFragment.WHERE_TO_GO_FRAGMENT;
import static com.mad.whosetreat.activityFragments.WhereToGoFragment.WTG;

public class MainActivity extends AppCompatActivity
        implements LocationListener,
        LocDialogFragment.LocDialogListener, GoogleApiClient.OnConnectionFailedListener,
        NewSearchDialogFragmentListener, NewGroupDialogueFragment.NewGroupDialogueFragmentListener,
        TreatConfirmDialogFragment.TreatConfirmDialogListener {

    // Constants
    public static final String PLACE = "place";
    public static final String MENU_FRAGMENT_TAG = "menuSelectionFragment";
    public static final String GROUP_SELECTION_FRAGMENT = "group selection fragment";
    public static final int CONTACT_PICK = 2002;
    private static final String VISIBILITY_MAIN = "Visibility of MainActivity";
    private static final int REQUEST_ACT_GROUP = 2001;
    private static final String TAG = "MainActivity_TAG";

    public final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    protected Location mCurLocation;
    private ArrayList<Person> mPeople;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = GroupSelectionFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).setTransition(TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.container, fragment, GROUP_SELECTION_FRAGMENT).commit();

        Toolbar toolbar = findViewById(R.id.title_toolbar);
        setSupportActionBar(toolbar);

        mLayout = findViewById(R.id.mainLinearLayout);

    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(VISIBILITY_MAIN, mLayout.getVisibility());

    }

    /**
     * when the 'confirm' button is clicked, this method will be called and handle the event
     * according to the user's input.
     */
    private void onClickHandler(int number) {
        mLayout.setVisibility(View.GONE);
        mPeople = new ArrayList<>(0);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String userName = preferences.getString("username", "Phone Owner");
        Person user = new Person();
        user.setName(userName);
        mPeople.add(0, user);

        // adding person with numbers
        for (int i = 1; i < number; i++) {
            Person person = new Person();
            person.setName("" + i);
            mPeople.add(i, person);
        }

        MenuSelectionFragment fragment = (MenuSelectionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container);

        if (fragment == null) {

            fragment = MenuSelectionFragment.newInstance(mPeople);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).setTransition(TRANSIT_FRAGMENT_CLOSE)
                    .replace(R.id.container, fragment, MENU_FRAGMENT_TAG).commit();
        }
    }

    /**
     * Tool bar option menu inflater
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.action_groups:
                Intent groupIntent = new Intent(this, GroupsInfoActivity.class);
                startActivity(groupIntent);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurLocation = location;
    }

    @Override
    public void onManualCity(DialogFragment dlg) {

        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PRICE_LEVEL,
                Place.Field.PHOTO_METADATAS, Place.Field.OPENING_HOURS, Place.Field.RATING
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    public void onOkayClick(DialogFragment dlg) throws Settings.SettingNotFoundException {
        int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        if (off == 0) {
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.waiting_gps_title)
                    .setMessage(R.string.waiting_gps_signal_message)
                    .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setIcon(R.drawable.ic_exclamation_circle)
                    .show();
        }

    }

    @Override
    public void onCancelClick(DialogFragment dlg) {
//        Log.d(TAG, "MainActivity: onCancelClick");
        MenuSelectionFragment fragment = (MenuSelectionFragment) getSupportFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);
        fragment.onCancelClick();
    }

    /**
     * NewSearchDialogFragmentListener interface implemented methods
     * onOkayNewSearchClick will handle ok button click for new search dialog
     * onCancelNewSearchClick will handle cancel button click for new search dialog
     *
     * @param dlg
     * @param radius
     */
    @Override
    public void onOkayNewSearchClick(DialogFragment dlg, int id, int radius) {
//        Log.d(TAG, "MainActivity: onCancelNewSearchClick");
        MenuSelectionFragment fragment = (MenuSelectionFragment) getSupportFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);
        fragment.setUrlForAsync(id, radius + 200);
    }

    @Override
    public void onCancelNewSearchClick(DialogFragment dlg) {
//        Log.d(TAG, "MainActivity: onCancelNewSearchClick");
    }

    /**
     * Handles after user provide the location manually.
     * Will launch the MenuSelectionFragment again with the location
     * Also, handles the event of adding groups
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.d(TAG, "Place: " + place.getName());

                MenuSelectionFragment fragment = (MenuSelectionFragment) getSupportFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);

                if (fragment != null) {
                    fragment.setLatLngByPlace(place);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);

                // TODO: Handle the error.
                Log.d(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        } else if (requestCode == REQUEST_ACT_GROUP) {
            if (resultCode == RESULT_OK) {
                WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
                Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();

//                Log.d(TAG, "onActivity Result" + groups.getGroups().get(0).getName());
                GroupSelectionFragment fragment = (GroupSelectionFragment) getSupportFragmentManager().findFragmentByTag(GROUP_SELECTION_FRAGMENT);
                fragment.onActivityResult(requestCode, resultCode, data);
                fragment.groupAdapter.addGroup(groups.getGroups().get(groups.getGroups().size() - 1));
                if (fragment.noGroupTv.getVisibility() == View.VISIBLE) {
                    fragment.noGroupTv.setVisibility(View.INVISIBLE);
                }

                whoseTreatApplication.saveAllGroups(groups);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * handles event after user entering the number of people
     *
     * @param dlg
     * @param number
     * @param isRandom
     */
    @Override
    public void onConfirmClick(DialogFragment dlg, String number, boolean isRandom) {
        onClickHandler(Integer.parseInt(number));
    }

    @Override
    public void onTreatClick(int code) {
        if (code == WTG) {
            WhereToGoFragment whereToGoFragment = (WhereToGoFragment) getSupportFragmentManager().findFragmentByTag(WHERE_TO_GO_FRAGMENT);
            whereToGoFragment.setTreatBtn();

            WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
            whoseTreatApplication.setTreat(true);

        } else if (code == RAND) {
            RandNumFragment randNumFragment = (RandNumFragment) getSupportFragmentManager().findFragmentByTag(RAND_NUM_FRAGMENT);
            randNumFragment.setTreatBtn();

            WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
            whoseTreatApplication.setTreat(true);

        } else {
            ShopDetailFragment shopDetailFragment = (ShopDetailFragment) getSupportFragmentManager().findFragmentByTag(SHOP_DETAIL_FRAGMENT);
            if (shopDetailFragment != null) {
                shopDetailFragment.setTreatBtn();
            } else {
                ShopDetailFragment shopDetailFragment1 = (ShopDetailFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                shopDetailFragment1.setTreatBtn();

            }
            WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
            whoseTreatApplication.setTreat(true);

        }
    }

    @Override
    public PlaceDto.Place getPlace(int code) {

        if (code == WTG) {
            WhereToGoFragment whereToGoFragment = (WhereToGoFragment) getSupportFragmentManager().findFragmentByTag(WHERE_TO_GO_FRAGMENT);
            return whereToGoFragment.getShop();

        } else if (code == RAND) {
            RandNumFragment randNumFragment;
            randNumFragment = (RandNumFragment) getSupportFragmentManager().findFragmentByTag(SHOP_DETAIL_FRAGMENT);
            if (randNumFragment == null) {
                randNumFragment = (RandNumFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            }
            return null;
        } else {
            ShopDetailFragment shopDetailFragment;
            shopDetailFragment = (ShopDetailFragment) getSupportFragmentManager().findFragmentByTag(SHOP_DETAIL_FRAGMENT);
            if (shopDetailFragment == null) {
                shopDetailFragment = (ShopDetailFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            }
            return shopDetailFragment.getShop();
        }
    }

}
