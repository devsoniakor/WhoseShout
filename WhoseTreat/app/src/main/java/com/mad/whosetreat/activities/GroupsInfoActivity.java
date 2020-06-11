package com.mad.whosetreat.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.adapter.GroupInfoAdapter;
import com.mad.whosetreat.dialog.ConfirmDialog;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;

import java.util.ArrayList;
import java.util.HashSet;

import static com.mad.whosetreat.adapter.GroupInfoAdapter.DELETE;
import static com.mad.whosetreat.adapter.GroupInfoAdapter.DETAIL;
import static com.mad.whosetreat.adapter.GroupInfoAdapter.RESET;
import static com.mad.whosetreat.dialog.NewGroupDialogueFragment.CODE;

/**
 * GroupsInfoActivity allows user to browse all the groups and can manipulate them (either delete or
 * reset data). Whenever user tries to make changes on group, an alert dialog will be displayed and get
 * confirmation from user.
 */
public class GroupsInfoActivity extends AppCompatActivity implements ConfirmDialog.confirmDialogListener, GroupInfoAdapter.OnGroupInfoClickListener {


    public static final String DELETION_CONFIRM_DIALOG = "deletion confirm dialog";
    static final String EDIT_POSITION = "position of group for edit";
    static final String DATA_FOR_EDIT = "edit data";
    private static final String TAG = "GroupInfo_TAG";
    private static final String RESET_CONFIRM_DIALOG = "reset confirm dialog";
    public static final int REQUEST_GALLERY = 1001;
    private static final int EDIT_DETAIL = 5001;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 6001;
    private RecyclerView mGroupInfoRv;
    private GroupInfoAdapter mGroupInfoAdapter;
    private Group mEditGroup;
    private int mEditPos;
    private RelativeLayout mContainer;
    private TextView mNoGroupTv;
    private WhoseTreatApplication mApp;

    /**
     * inflates layout and links views and their listener where appropriate.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_info);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        mApp = (WhoseTreatApplication) getApplication();
        Groups groupsFromSharedPreferences = mApp.getGroupsFromSharedPreferences();

        mContainer = findViewById(R.id.group_info_container);
        mGroupInfoRv = findViewById(R.id.GroupsInfoRv);
        mGroupInfoRv.setLayoutManager(mLayoutManager);
        mGroupInfoAdapter = new GroupInfoAdapter(this, groupsFromSharedPreferences.getGroups());
        mGroupInfoRv.setAdapter(mGroupInfoAdapter);
        mGroupInfoAdapter.notifyDataSetChanged();
        mNoGroupTv = findViewById(R.id.no_group_info_tv);

        if (groupsFromSharedPreferences.getGroups().size() == 0) {
            mNoGroupTv.setVisibility(View.VISIBLE);
        } else {
            mNoGroupTv.setVisibility(View.INVISIBLE);
        }

        // handles the option selection event (delete / reset group)
        mGroupInfoAdapter.setOnGroupInfoClickListener(this);

    }

    /**
     * if user confirm their choice (delete / reset) this method will do what user wants to do.
     *
     * @param dlg
     * @param code
     */
    @Override
    public void onEditConfirmClick(DialogFragment dlg, String code) {

        if (code.equals(RESET)) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                    showRequestDialog();

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_SEND_SMS);
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                }
            } else {
                sendSMS();
            }

        } else { // dealing with deletion of group
            mApp = (WhoseTreatApplication) getApplication();
            Groups groupsFromSharedPreferences = mApp.getGroupsFromSharedPreferences();
            HashSet<String> groupNameSet = mApp.getGroupNameSet();
            mGroupInfoAdapter.removeGroup(mEditPos);
            if (mGroupInfoAdapter.getItemCount() == 0) {
                mNoGroupTv.setVisibility(View.VISIBLE);
            }

            groupsFromSharedPreferences.getGroups().remove(groupsFromSharedPreferences.getGroups().get(mEditPos));
            groupNameSet.remove(mEditGroup.getName());

            // saves changes to shared preferences
            mApp.saveAllGroups(groupsFromSharedPreferences);
            mApp.saveAllGroupNamesSet(groupNameSet);
            mGroupInfoAdapter.notifyDataSetChanged();
        }

    }

    private void sendSMS() {
        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
        Groups groupsFromSharedPreferences = whoseTreatApplication.getGroupsFromSharedPreferences();

        SmsManager smsManager = SmsManager.getDefault();
        String s = "Shouts count of " + mEditGroup.getName() + " has been reset";
        s += System.getProperty("line.separator");
        String t = "The counts before reset were: " + System.getProperty("line.separator");
        ArrayList<String> numbers = new ArrayList<>(mEditGroup.getCount());


        for (int i = 0; i < mEditGroup.getPerson().size(); i++) {
            t += groupsFromSharedPreferences.getGroups().get(mEditPos).getPerson().get(i).getName();
            t += "\t";
            t += groupsFromSharedPreferences.getGroups().get(mEditPos).getPerson().get(i).getCount();
            t += System.getProperty("line.separator");
            numbers.add(groupsFromSharedPreferences.getGroups().get(mEditPos).getPerson().get(i).getPhone());
            groupsFromSharedPreferences.getGroups().get(mEditPos).getPerson().get(i).setCount(0);
        }
        mGroupInfoAdapter.resetGroup(mEditPos);
        for (int i = 0; i < numbers.size(); i++) {
            smsManager.sendTextMessage(numbers.get(i), null, s, null, null);
            smsManager.sendTextMessage(numbers.get(i), null, t, null, null);
        }

        Snackbar.make(mContainer, "Messages sent.", Snackbar.LENGTH_SHORT).show();

        // saves changes to shared preferences
        whoseTreatApplication.saveAllGroups(groupsFromSharedPreferences);
        mGroupInfoAdapter.notifyDataSetChanged();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                } else {
                    showRequestDialog();
                }
                break;
            case REQUEST_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    mGroupInfoAdapter.getImageFromGallery();

                }
                break;
        }

    }

    public void showRequestDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupsInfoActivity.this);
        alertDialog.setTitle("Permission Required")
                .setMessage("To reset shouts, please allow me to send out messages about current shouts status to other members of the group.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(GroupsInfoActivity.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                PERMISSIONS_REQUEST_SEND_SMS);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    @Override
    public void onCancelClick(DialogFragment dlg) {
    }

    @Override
    public void onGroupClick(View itemView, int position, Group group, String option) {

        mEditGroup = group;
        mEditPos = position;

        switch (option) {
            case DELETE:
                // showing dialog for confirmation
                ConfirmDialog delDialog = new ConfirmDialog();
                Bundle bdl1 = new Bundle();
                bdl1.putString(CODE, DELETE);
                delDialog.setArguments(bdl1);
                delDialog.show(getSupportFragmentManager(), DELETION_CONFIRM_DIALOG);
                break;
            case RESET:
                // showing dialog for confirmation
                ConfirmDialog resetDialog = new ConfirmDialog();
                Bundle bdl2 = new Bundle();
                bdl2.putString(CODE, RESET);
                resetDialog.setArguments(bdl2);
                resetDialog.show(getSupportFragmentManager(), RESET_CONFIRM_DIALOG);

                break;
            case DETAIL:
                Intent editGroup = new Intent(this, GroupDetailEditActivity.class);
                Bundle bdl3 = new Bundle();
                bdl3.putString(CODE, DETAIL);
                bdl3.putInt(EDIT_POSITION, mEditPos);
                editGroup.putExtra(DATA_FOR_EDIT, bdl3);
                startActivityForResult(editGroup, EDIT_DETAIL);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case EDIT_DETAIL:
                if (resultCode == RESULT_OK) {
                    WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
                    Groups groupsFromSharedPreferences = whoseTreatApplication.getGroupsFromSharedPreferences();
                    mGroupInfoAdapter.resetGroupList(groupsFromSharedPreferences.getGroups());
                    mGroupInfoAdapter.notifyDataSetChanged();
                }
                break;

            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    // Let's read picked image path using content resolver
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    assert uri != null;
                    Cursor cursor = getContentResolver().query(uri, filePath, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                    cursor.close();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    mGroupInfoAdapter.setGroupImage(imagePath);
                    Groups groups = mApp.getGroupsFromSharedPreferences();
                    groups.getGroups().get(GroupInfoAdapter.getTargetIndex()).setImage(imagePath);
                    mApp.saveAllGroups(groups);

                }
                break;
        }


    }

}
