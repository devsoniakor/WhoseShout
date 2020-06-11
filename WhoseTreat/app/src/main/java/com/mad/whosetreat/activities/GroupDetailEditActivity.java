package com.mad.whosetreat.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.activityFragments.ShopDetailFragment;
import com.mad.whosetreat.dialog.LocDialogFragment;
import com.mad.whosetreat.dialog.NewGroupDialogueFragment;
import com.mad.whosetreat.dialog.NewNameDialogueFragment;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;
import com.mad.whosetreat.model.Person;
import com.mad.whosetreat.model.PlaceDto;
import com.mad.whosetreat.utilClass.Distance;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
import static com.mad.whosetreat.activities.GroupsInfoActivity.DATA_FOR_EDIT;
import static com.mad.whosetreat.activities.GroupsInfoActivity.EDIT_POSITION;
import static com.mad.whosetreat.activityFragments.ShopListFragment.SHOP_DETAIL_FRAGMENT;
import static com.mad.whosetreat.dialog.NewGroupDialogueFragment.CODE;
import static com.mad.whosetreat.dialog.NewGroupDialogueFragment.SWITCH_CODE;
import static com.mad.whosetreat.dialog.NewNameDialogueFragment.ADDITION_CODE;
import static com.mad.whosetreat.dialog.NewNameDialogueFragment.EDIT_MEMBER_DETAIL;
import static com.mad.whosetreat.dialog.NewNameDialogueFragment.TARGET_PERSON;

public class GroupDetailEditActivity extends AppCompatActivity implements
        NewNameDialogueFragment.NewNameDialogueFragmentListener, NewGroupDialogueFragment.NewGroupDialogueFragmentListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocDialogFragment.LocDialogListener {

    public static final String GROUP_NAME = "group name";
    private static final String EDIT_MEMBER_DIALOG = "edit member detail dialog";
    private static final String TAG = "GroupDetailEdit_TAG";
    private static final String DATA_BDL = "data bundle";
    private static final String EDIT_GROUP_DIALOG = "Edit group name dialog";
    private static final String LOCATION_DIALOG = "Location Dialog TAG";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private int mEditPos;
    private TextView mGroupNameTv;
    private TextView mRandomStatusTv;
    private Group mEditGroup;
    private int mTargetPos;
    private String mDefaultDist = "N/A";
    private LatLng mCurLatLng;
    private GoogleApiClient mGoogleApiClient;
    private PlaceDto.Place mTempPlace;
    private boolean mIsVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail_edit);

        Intent intent = getIntent();
        Bundle bdl = intent.getBundleExtra(DATA_FOR_EDIT);
        mEditPos = bdl.getInt(EDIT_POSITION);

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        setData();

    }


    /**
     * Set data contents of the view
     * Generate Table of members dynamically and set OnClickListeners
     */
    private void setData() {

        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
        Groups groupsFromSharedPreferences = whoseTreatApplication.getGroupsFromSharedPreferences();

        mEditGroup = groupsFromSharedPreferences.getGroups().get(mEditPos);
        mGroupNameTv = findViewById(R.id.g_name_tv);
        mGroupNameTv.setText(mEditGroup.getName());
        mRandomStatusTv = findViewById(R.id.g_random_tv);

        setSwitchStatusTv(mEditGroup.isRandom());

        ImageView groupEditIV = findViewById(R.id.g_name_edit_iv);
        mGroupNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGroupDetailEditClick(v);
            }
        });

        groupEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGroupDetailEditClick(v);
            }
        });

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Table of group members
        TableLayout table_members = findViewById(R.id.g_table);
        // Table of visited places
        TableLayout table_places = findViewById(R.id.g_table_places);


        // Dynamically generating visited places table
        if (table_places.getChildCount() <= mEditGroup.getPlaces().size()) {
            populatePlaceTable(0, inflater, table_places);

        } else {
            populatePlaceTable(1, inflater, table_places);

        }

        sortTable(table_members, inflater);


    }

    private void populatePlaceTable(int startPosition, LayoutInflater inflater, TableLayout placeTable) {
        for (int i = startPosition; i < mEditGroup.getPlaces().size(); i++) {

            int placeIndex = (startPosition == 0) ? i : i - 1;
            final PlaceDto.Place place = mEditGroup.getPlaces().get(placeIndex);

            TableRow row = (TableRow) inflater.inflate(R.layout.attribute_row_places, null);
            TextView dateTv = (TextView) row.getVirtualChildAt(0);
            TextView placeTv = (TextView) row.getVirtualChildAt(1);
            final TextView personTv = (TextView) row.getVirtualChildAt(2);


            dateTv.setText(place.getVisited_date());
            personTv.setText(place.getShout().get(0).getName());


            // Initialize a new ClickableSpan to display red background
            final PlaceDto.Place finalPlace = place;
            if (finalPlace.isPlace()) {
                // Initialize a new SpannableStringBuilder instance
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(place.getName());
                ClickableSpan clickableTextSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        view.invalidate();
                        onPlaceClick(finalPlace);
                    }
                };
                // Apply the clickable text to the span
                ssBuilder.setSpan(
                        clickableTextSpan, // Span to add
                        place.getName().indexOf(place.getName().charAt(0)), // Start of the span (inclusive)
                        place.getName().length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
                );
                placeTv.setText(ssBuilder);
            } else {
                placeTv.setText(finalPlace.getName());
            }

            // Specify the TextView movement method
            placeTv.setMovementMethod(LinkMovementMethod.getInstance());

            switch (startPosition) {
                case 0:
                    placeTable.addView(row, startPosition);
                    break;
                case 1:
                    placeTable.removeViewAt(i);
                    placeTable.addView(row, i + 1);
                    break;
            }
        }

    }

    private void sortTable(TableLayout memberTable, LayoutInflater inflater) {
        // sorting list according to person's name
        if (memberTable.getChildCount() < mEditGroup.getPerson().size()) {
            Collections.sort(mEditGroup.getPerson(), new Comparator<Person>() {
                public int compare(Person v1, Person v2) {
                    return v1.getName().compareTo(v2.getName());
                }
            });

            populateMemberTable(0, inflater, memberTable);
        } else {
            // sorting list according to person's name
            Collections.sort(mEditGroup.getPerson(), new Comparator<Person>() {
                public int compare(Person v1, Person v2) {
                    return v1.getName().compareTo(v2.getName());
                }
            });

            populateMemberTable(1, inflater, memberTable);
        }
    }

    private void populateMemberTable(int startPosition, LayoutInflater inflater, TableLayout memberTable) {

        //dynamically generates the table_members rows
        for (int i = startPosition; i < mEditGroup.getPerson().size(); i++) {
            int personIndex = (startPosition == 0) ? i : i - 1;
            final Person person = mEditGroup.getPerson().get(personIndex);

            TableRow row = (TableRow) inflater.inflate(R.layout.attribute_row_edit, null);
            TextView nameTv = (TextView) row.getVirtualChildAt(0);
            TextView treatTv = (TextView) row.getVirtualChildAt(1);
            final TextView contactTv = (TextView) row.getVirtualChildAt(2);
            ImageView editIv = (ImageView) row.getVirtualChildAt(3);

            nameTv.setText(mEditGroup.getPerson().get(personIndex).getName());
            treatTv.setText("" + mEditGroup.getPerson().get(personIndex).getCount());
            contactTv.setText(mEditGroup.getPerson().get(personIndex).getPhone());

            editIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    onEditIvClick(person);
                }
            });


            switch (startPosition) {
                case 0:
                    memberTable.addView(row, i + 1);
                    break;
                case 1:
                    memberTable.removeViewAt(i);
                    memberTable.addView(row, i);
                    break;
            }

        }
    }

    private void setSwitchStatusTv(boolean isRandom) {
        mRandomStatusTv.setText((isRandom) ? getString(R.string.on) : getString(R.string.off));
        mRandomStatusTv.setTextColor(((isRandom) ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.material_gray_700)));

    }

    private void onPlaceClick(PlaceDto.Place place) {


        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location curLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        final Distance distance;
        if (curLocation != null) {
            distance = new Distance(curLocation.getLatitude(), curLocation.getLongitude(),
                    place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
            mTempPlace = place;

            String dist = "";
            if (distance.getDistance() > 999) {
                dist += (distance.getDistance() / 1000) + "KM";
            } else {
                dist = "" + distance.getDistance() + "M";
            }
            openShopDetail(dist, place);
        } else {
            mTempPlace = place;
            LocDialogFragment customDialog = new LocDialogFragment();
            customDialog.show(getSupportFragmentManager(), LOCATION_DIALOG);
        }

    }

    private void openShopDetail(String dist, PlaceDto.Place place) {

        String priceLv = "";
        switch (place.getPrice_level()) {
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


        ShopDetailFragment detailFragment = ShopDetailFragment.newInstance(dist, priceLv);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).setTransition(TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.detail_container, detailFragment, SHOP_DETAIL_FRAGMENT).commit();
        mIsVisible = false;
    }

    public PlaceDto.Place getShopFromGroupHx() {
        return mTempPlace;
    }


    public void onEditIvClick(Person person) {
        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
        Groups groupsFromSharedPreferences = whoseTreatApplication.getGroupsFromSharedPreferences();

        mTargetPos = groupsFromSharedPreferences.getGroups().get(mEditPos).getPerson().indexOf(person);
        NewNameDialogueFragment newNameDialogueFragment = new NewNameDialogueFragment();
        Bundle bdl = new Bundle();
        bdl.putString(ADDITION_CODE, EDIT_MEMBER_DETAIL);
        bdl.putParcelable(TARGET_PERSON, person);
        newNameDialogueFragment.setArguments(bdl);
        newNameDialogueFragment.show(getSupportFragmentManager(), EDIT_MEMBER_DIALOG);
    }

    @Override
    public void onAddConfirmClick(DialogFragment dlg, String name, String phone) {

        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
        Groups groupsFromSharedPreferences = whoseTreatApplication.getGroupsFromSharedPreferences();
        Collections.sort(mEditGroup.getPerson(), new Comparator<Person>() {
            public int compare(Person v1, Person v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });

        groupsFromSharedPreferences.getGroups().get(mEditPos).getPerson().get(mTargetPos).setPhone(phone);
        whoseTreatApplication.saveAllGroups(groupsFromSharedPreferences);
        setData();

    }

    public void onGroupDetailEditClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        NewGroupDialogueFragment newNameDialogueFragment = new NewGroupDialogueFragment();
        Bundle bdl = new Bundle();
        bdl.putInt(CODE, 3);
        bdl.putBoolean(SWITCH_CODE, mEditGroup.isRandom());
        bdl.putString(GROUP_NAME, mEditGroup.getName());
        newNameDialogueFragment.setArguments(bdl);
        newNameDialogueFragment.show(getSupportFragmentManager(), EDIT_GROUP_DIALOG);

    }

    @Override
    public void onBackPressed() {
        if (mIsVisible) {
            Intent intent = new Intent(GroupDetailEditActivity.this, GroupsInfoActivity.class);
            Bundle data = new Bundle();
            intent.putExtra(DATA_BDL, data);
            setResult(RESULT_OK, intent);
            finish();
            super.onBackPressed();
        } else {
            super.onBackPressed();
            mIsVisible = true;
        }
    }

    /**
     * takes in new name from NewGroupDialogueFragment and changes the group's name if applicable.
     *
     * @param dlg
     * @param name
     * @param isRandom
     */
    @Override
    public void onConfirmClick(DialogFragment dlg, String name, boolean isRandom) {
        if (!name.equals(mEditGroup.getName()) || isRandom != mEditGroup.isRandom()) {
            WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
            Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();
            Group group = groups.getGroups().get(mEditPos);
            group.setName(name);
            group.setRandom(isRandom);
            whoseTreatApplication.saveAllGroups(groups);
            mEditGroup.setName(name);
            mEditGroup.setRandom(isRandom);
            mGroupNameTv.setText(name);
            setSwitchStatusTv(isRandom);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            mCurLatLng = place.getLatLng();

            Distance distance = new Distance(mCurLatLng.latitude, mCurLatLng.longitude,
                    mTempPlace.getGeometry().getLocation().getLat(), mTempPlace.getGeometry().getLocation().getLng());
            String dist = "";
            if (distance.getDistance() > 999) {
                dist += (distance.getDistance() / 1000) + "KM";
            } else {
                dist = "" + distance.getDistance() + "M";
            }

            openShopDetail(dist, mTempPlace);

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            // TODO: Handle the error.

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled operation.
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        }
    }

    @Override
    public void onCancelClick(DialogFragment dlg) {
        openShopDetail("N/A", mTempPlace);
    }
}
