package com.mad.whosetreat.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.whosetreat.R;
import com.mad.whosetreat.adapter.ContactAdapter;
import com.mad.whosetreat.model.Person;

import java.util.ArrayList;


/**
 * ContactPickerActivity is allowing user to select multiple contacts from his/her ContactAdapter list to form a group
 */
public class ContactPickerActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {

    public static final String GROUP_FROM_CONTACT = "group from ContactAdapter";
    private static final String TAG = "Contact_TAG";
    private ArrayList<String> name, phone, combined;
    private int count = 0;
    private ArrayList<Person> mSelectedPerson = new ArrayList<>();
    private ArrayList<Person> mContList = new ArrayList<>();
    private RecyclerView mContRv;
    private ContactAdapter mContAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);


        FloatingActionButton fab = findViewById(R.id.contactFab);

        // handling the event when user clicks the fab button after the selection of people from the contact list
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedPerson = mContAdapter.getSelected();

                for (int i = 0; i < mSelectedPerson.size(); i++) {
                    mSelectedPerson.get(i).setSelected(false);
                }

                Intent intent = new Intent(ContactPickerActivity.this, AddMemberActivity.class);
                intent.putParcelableArrayListExtra(GROUP_FROM_CONTACT, mSelectedPerson);
                setResult(RESULT_OK, intent);

                finish();

            }
        });


        Cursor cursor = getURI();
        int end = cursor.getCount();
        int size = 0;
        name = new ArrayList<>(end);
        phone = new ArrayList<>(end);
        combined = new ArrayList<>(end);


        String[] bbStr = cursor.getColumnNames();


        if (cursor.moveToFirst()) {
            do {
                name.add(count, cursor.getString(1));
                phone.add(count, cursor.getString(2));
                combined.add(count, name.get(count) + "\n" + phone.get(count));
                Person person = new Person();
                person.setName(cursor.getString(1));
                person.setPhone(cursor.getString(2));
                mContList.add(person);

                count++;
                size++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mContRv = findViewById(R.id.contRv);
        mContRv.setLayoutManager(layoutManager);
        mContAdapter = new ContactAdapter(this, mContList);
        mContRv.setAdapter(mContAdapter);
        mContAdapter.notifyDataSetChanged();
    }


    public Cursor getURI() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,

        };
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        return managedQuery(uri, projection, null, null, sortOrder);
    }

    public ArrayList<Person> getContactNames() {
        return mSelectedPerson;
    }

    // implemented method as a ContactAdapterListener
    @Override
    public void onContactItemClick(View itemView, int position, String name, String contact) {

    }
}
