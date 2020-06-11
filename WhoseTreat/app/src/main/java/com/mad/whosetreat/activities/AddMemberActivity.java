package com.mad.whosetreat.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;
import com.mad.whosetreat.R;
import com.mad.whosetreat.WhoseTreatApplication;
import com.mad.whosetreat.adapter.NameAdapter;
import com.mad.whosetreat.dialog.ConfirmDialog;
import com.mad.whosetreat.dialog.NewGroupDialogueFragment;
import com.mad.whosetreat.dialog.NewNameDialogueFragment;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;
import com.mad.whosetreat.model.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.mad.whosetreat.activities.ContactPickerActivity.GROUP_FROM_CONTACT;
import static com.mad.whosetreat.activities.MainActivity.CONTACT_PICK;
import static com.mad.whosetreat.dialog.NewGroupDialogueFragment.CODE;
import static com.mad.whosetreat.dialog.NewGroupDialogueFragment.MEMBERS_LIST;
import static com.mad.whosetreat.dialog.NewNameDialogueFragment.ADDITION_CODE;
import static com.mad.whosetreat.dialog.NewNameDialogueFragment.ADD_NEW_MEMBER;

/**
 * AddMemberActivity is an activity where user can add new member and form a group.
 * This has an adapter and recyclerview components displaying the all names that user has been added
 * User can simply click the name to select and form a group as well as type in new names.
 */
public class AddMemberActivity extends AppCompatActivity implements NewGroupDialogueFragment.NewGroupDialogueFragmentListener,
        ConfirmDialog.confirmDialogListener, NewNameDialogueFragment.NewNameDialogueFragmentListener {

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    private static final String TAG = "AddMemberActivity_TAG";
    private static final String NEW_GROUP_DIALOG = "new group dialog";
    private static final String NEW_NAME_DIALOG = "new name dialog";
    private FloatingActionsMenu mFabMenu;
    private EditText mNewNameEt;
    private Button mAddPersonBtn;
    private Button mDoneBtn;
    private RecyclerView mNameRv;
    private NameAdapter mNameAdapter;

    private CoordinatorLayout mContainer;
    private Button mContactBtn;
    private TextView mNoNameTv;
    private List<Person> mNewMembers;

    /**
     * Inflates layout and links the views  as well as set observers
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        mContainer = findViewById(R.id.add_member_r_layout);
        mDoneBtn = findViewById(R.id.doneBtn);
        mNoNameTv = findViewById(R.id.no_name_tv);

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                if (mNameAdapter.getNewMembers().size() > 1) {
                    NewGroupDialogueFragment newNameDialogueFragment = new NewGroupDialogueFragment();
                    Bundle bdl = new Bundle();
                    bdl.putInt(CODE, 1);
                    bdl.putParcelableArrayList(MEMBERS_LIST, (ArrayList<? extends Parcelable>) mNameAdapter.getNewMembers());
                    newNameDialogueFragment.setArguments(bdl);
                    newNameDialogueFragment.show(getSupportFragmentManager(), NEW_GROUP_DIALOG);
                } else {
                    Snackbar.make(mContainer, R.string.please_select_members, Snackbar.LENGTH_SHORT).show();

                }
            }
        });

        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
        Group group = whoseTreatApplication.getGroupFromSharedPreferences();

        for (Person person : group.getPerson()) {
            person.setColor(getRandomMaterialColor(getString(R.string.material_color_code)));
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        mNameRv = findViewById(R.id.namesRv);
        mNameRv.setLayoutManager(mLayoutManager);

        mNameAdapter = new NameAdapter(this, group.getPerson());

        mNameRv.setAdapter(mNameAdapter);
        mNameAdapter.notifyDataSetChanged();

        if (mNameAdapter.getItemCount() == 0) {
            mNoNameTv.setVisibility(View.VISIBLE);
        }

        mFabMenu = findViewById(R.id.fab_menu);

        mFabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mFabMenu.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }

            @Override
            public void onMenuCollapsed() {
                mFabMenu.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        });

        final FloatingActionButton addManual = findViewById(R.id.fb_person_name);
        addManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                NewNameDialogueFragment newNameDialogueFragment = new NewNameDialogueFragment();
                Bundle bdl = new Bundle();
                bdl.putString(ADDITION_CODE, ADD_NEW_MEMBER);
                newNameDialogueFragment.setArguments(bdl);
                newNameDialogueFragment.show(getSupportFragmentManager(), NEW_NAME_DIALOG);

            }
        });

        final FloatingActionButton addContact = findViewById(R.id.fb_by_contact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                getPermissionToReadUserContacts();
            }
        });


        Snackbar.make(mContainer, R.string.add_member_msg, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFabMenu.isExpanded()) {
            mFabMenu.collapseImmediately();
        }
        return super.onTouchEvent(event);
    }

    /**
     * picking up random color for an initial circle of each person
     *
     * @param typeColor
     * @return
     */
    @SuppressLint("ResourceAsColor")
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = R.color.colorPrimary;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.RED);
            colors.recycle();
        }

        return returnColor;
    }


    /**
     * handling the result of dialog adding new group (NewGroupDialogueFragment)
     * after selecting all the new members, user will asked to put the name of group.
     * this method handles the event after that.
     *
     * @param dlg
     * @param name
     * @param isRandom
     */
    @Override
    public void onConfirmClick(DialogFragment dlg, String name, boolean isRandom) {

        if (mNameAdapter.getNewMembers().size() > 1) {
            // new members
            mNewMembers = mNameAdapter.getNewMembers();

            for (int i = 0; i < mNewMembers.size(); i++) {
                mNewMembers.get(i).setSelected(false);
            }

            // set the name and members to new group
            Group newGroup = new Group();
            newGroup.setName(name);
            newGroup.setPerson(mNewMembers);
            newGroup.setRandom(isRandom);

            WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
            Groups groups = whoseTreatApplication.getGroupsFromSharedPreferences();
            Group group = whoseTreatApplication.getGroupFromSharedPreferences();

            newGroup.setId(0);
            groups.getGroups().add(newGroup);
            groups.addCount();

            // saving the changes as sharedpreference
            whoseTreatApplication.saveAllGroups(groups);
            whoseTreatApplication.saveAllNames(group);

            // return the result of the request and call the previous activity
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);

            finish();

        }
    }

    /**
     * handling person deletion after user confirmed
     *
     * @param dlg
     * @param code
     */
    @Override
    public void onEditConfirmClick(DialogFragment dlg, String code) {
        // if the person is selected at the moment, will be deleted from the list
        if (mNameAdapter.getDelPerson().isSelected()) {
            mNameAdapter.getNewMembers().remove(mNameAdapter.getDelPerson());
        }
        mNameAdapter.removePerson(mNameAdapter.getDelPerson());
        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
        Group group = whoseTreatApplication.getGroupFromSharedPreferences();
        HashSet<String> namesSet = whoseTreatApplication.getNamesSet();

        for (int i = 0; i < group.getPerson().size(); i++) {
            if (group.getPerson().get(i).getId() == mNameAdapter.getDelId()) {
                namesSet.remove(group.getPerson().get(i).getName());
                group.getPerson().remove(group.getPerson().get(i));
                whoseTreatApplication.saveAllNames(group);
                whoseTreatApplication.saveAllSet(namesSet);

                if (mNameAdapter.getItemCount() == 0) {
                    mNoNameTv.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    @Override
    public void onCancelClick(DialogFragment dlg) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == CONTACT_PICK) {
            WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
            Group group = whoseTreatApplication.getGroupFromSharedPreferences();

            ArrayList<Person> listPerson = data.getParcelableArrayListExtra(GROUP_FROM_CONTACT);

            HashSet<String> namesSet = whoseTreatApplication.getNamesSet();
            List<String> duplicatedNames = new ArrayList<>();
            int count = namesSet.size();

            for (int i = 0; i < listPerson.size(); i++) {
                namesSet.add(listPerson.get(i).getName());

                if (namesSet.size() == count) {
                    duplicatedNames.add(listPerson.get(i).getName());
                } else {
                    count++;
                    if (mNoNameTv.getVisibility() == View.VISIBLE) {
                        mNoNameTv.setVisibility(View.INVISIBLE);
                    }
                    listPerson.get(i).setColor(getRandomMaterialColor(getString(R.string.material_color_code)));
                    group.addCount();
                    listPerson.get(i).setId(group.getCount());
                    group.getPerson().add(listPerson.get(i));
                    mNameAdapter.addPerson(listPerson.get(i));
                }
            }
            whoseTreatApplication.saveAllNames(group);

            if (duplicatedNames.size() == 1) {
                Snackbar.make(mContainer, duplicatedNames.get(0) + " " + getString(R.string.already_exist), Snackbar.LENGTH_SHORT).show();

            } else if (duplicatedNames.size() > 1) {
                String names = "";
                for (int i = 0; i < duplicatedNames.size() - 1; i++) {
                    names += duplicatedNames.get(i).concat(", ");
                }
                names += duplicatedNames.get(duplicatedNames.size() - 1);
                Snackbar.make(mContainer, names + " " + getString(R.string.already_exist), Snackbar.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * requesting permission for read contacts to user
     */
    public void getPermissionToReadUserContacts() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    Snackbar.make(mContainer, R.string.permission_required, Snackbar.LENGTH_SHORT).show();

                }
            }

            // This will show the standard permission request dialog UI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        } else {
            Intent intent = new Intent(AddMemberActivity.this, ContactPickerActivity.class);
            startActivityForResult(intent, CONTACT_PICK);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(AddMemberActivity.this, ContactPickerActivity.class);
                startActivityForResult(intent, CONTACT_PICK);
            } else {
                Snackbar.make(mContainer, R.string.permission_required_text, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * handles the event of manual addition of person
     *
     * @param dlg
     * @param name
     * @param phone
     */
    @Override
    public void onAddConfirmClick(DialogFragment dlg, String name, String phone) {

        WhoseTreatApplication whoseTreatApplication = (WhoseTreatApplication) getApplication();
        Group group = whoseTreatApplication.getGroupFromSharedPreferences();

        Person person = new Person();
        person.setName(name);
        person.setPhone(phone);

        HashSet<String> namesSet = whoseTreatApplication.getNamesSet();
        int size = namesSet.size();

        namesSet.add(name);
        whoseTreatApplication.saveAllSet(namesSet);

        // if the size did not changed, the name is already existing
        if (whoseTreatApplication.getNamesSet().size() == size) {
            Snackbar.make(mContainer, R.string.person_already_exits, Snackbar.LENGTH_SHORT).show();
        } else {
            group.getPerson().add(person);
            group.addCount();
            person.setId(group.getCount());
            person.setColor(getRandomMaterialColor(getString(R.string.material_color_code)));
            mNameAdapter.addPerson(person);
            whoseTreatApplication.saveAllNames(group);
            if (mNoNameTv.getVisibility() == View.VISIBLE) {
                mNoNameTv.setVisibility(View.INVISIBLE);
            }
        }
    }
}
