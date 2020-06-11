package com.mad.whosetreat.activityFragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mad.whosetreat.R;
import com.mad.whosetreat.model.Chat;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

/**
 * is used for controlling individual chatting room
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    ListView listView;
    EditText editText;
    Button sendButton;
    String userName;
    String userId;
//    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public ChatFragment() {
        // Required empty public constructor
    }


    /**
     * links views and set listeners
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        listView = rootView.findViewById(R.id.chatList);
        editText = rootView.findViewById(R.id.messageEdit);
        sendButton = rootView.findViewById(R.id.sendBtn);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        userName = preferences.getString("username", "Anonymous");
        userId = "of";



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chatData = new Chat(userName, editText.getText().toString(), userId);
//                databaseReference.child("item_message").push().setValue(chatData);
                editText.setText("");
            }
        });


        return rootView;
    }

}
