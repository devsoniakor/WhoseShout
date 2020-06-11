package com.mad.whosetreat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mad.whosetreat.R;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.firebase.ui.database.FirebaseListAdapter;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.FirebaseDatabase;


public class ChatMainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    public static final String FIREBASE_USER = "Firebase User";
    private static final String TAG = "TAG_chatActivity: ";
    private static final int REQUEST_CODE = 1001;
    private CoordinatorLayout mContainer;
    private RecyclerView mChatListRv;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    private GoogleApiClient mGoogleApiClient;
//    private FirebaseAuth mFirebaseAuth;
//    private FirebaseUser mFirebaseUser;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    private FirebaseListAdapter<Chat> mAdapter;
    private String mUsername;
    private String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.title_toolbar);
        setSupportActionBar(toolbar);
        mContainer = findViewById(R.id.chat_main_container);


//        mMsgRecyclerView.setAdapter(mTrainAdapt);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplication().getApplicationContext());
        mUsername = preferences.getString("username", "Anonymous");

        // TODO: connect and populat the list
//        mChatListAdpater = new ChatListAdapter(this, databaseList());
        mChatListRv = (RecyclerView) findViewById(R.id.chat_list_Rv);
        mChatListRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        mChatListRv.setAdapter(mChatListAdpater);

        // Initialize RecyclerView.
//        mMsgRecyclerView = (RecyclerView) findViewById(R.id.messagesRv);
//        mLinearLayoutManager = new LinearLayoutManager(this);
//        mLinearLayoutManager.setStackFromEnd(true);

//        mMsgRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

//        mLinearLayoutManager = new LinearLayoutManager(this);
//        mLinearLayoutManager.setStackFromEnd(true);
//        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Initialize Firebase Auth
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();

//        //TODO: auth
//        // https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            Log.d(TAG, "mFirebaseUser == null");
//            Intent intent = new Intent(this, SignInActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
//            return;
//        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//            if (mFirebaseUser.getPhotoUrl() != null) {
//                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//                mUsername = mFirebaseAuth.getCurrentUser().getDisplayName();
//                Toast.makeText(this, "Welcome, " + mUsername, Toast.LENGTH_SHORT).show();
//                displayChat();
//            }
//
////            //TODO: delete after testing
////            mAuthListener = new FirebaseAuth.AuthStateListener() {
////                @Override
////                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
////                    FirebaseUser user = firebaseAuth.getCurrentUser();
////                    if (user != null) {
////                        // User is signed in
////                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
////                    } else {
////                        // User is signed out
////                        Log.d(TAG, "onAuthStateChanged:signed_out");
////                    }
////                }
////            };
//
//        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    private void displayChat() {
        RecyclerView msgRv = (RecyclerView) findViewById(R.id.messagesRv);
//        mAdapter = new FirebaseListAdapter<Chat>(this, Chat.class, R.layout.activity_chat, FirebaseDatabase.getInstance().getReference()) {
//            @Override
//            protected void populateView(View v, Chat model, int position) {
//
//            }
//        };

    }

    @Override
    protected void onStart() {
        super.onStart();
//        mFirebaseAuth.addAuthStateListener(mAuthListener);
//        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            mFirebaseAuth.removeAuthStateListener(mAuthListener);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult at Chat");
        if(requestCode == REQUEST_CODE && resultCode==RESULT_OK) {
            Snackbar.make(mContainer,  "Successful", Snackbar.LENGTH_SHORT).show();
            displayChat();
        } else {
            Snackbar.make(mContainer, "failed", Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


//    private void linkAccount() {
//        // Make sure form is valid
//        if (!validateLinkForm()) {
//            return;
//        }
//
//        // Get email and password from form
//        String email = mEmailField.getText().toString();
//        String password = mPasswordField.getText().toString();
//
//        // Create EmailAuthCredential with email and password
//        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
//
//        // Link the anonymous user to the email credential
//        showProgressDialog();
//
//        // [START link_credential]
//        mAuth.getCurrentUser().linkWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "linkWithCredential:success");
//                            FirebaseUser user = task.getResult().getUser();
//                            updateUI(user);
//                        } else {
//                            Log.w(TAG, "linkWithCredential:failure", task.getException());
//                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                    }
//                });
//        // [END link_credential]
//    }
//
//    private boolean validateLinkForm() {
//        boolean valid = true;
//
//        String email = mEmailField.getText().toString();
//        if (TextUtils.isEmpty(email)) {
//            mEmailField.setError("Required.");
//            valid = false;
//        } else {
//            mEmailField.setError(null);
//        }
//
//        String password = mPasswordField.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            mPasswordField.setError("Required.");
//            valid = false;
//        } else {
//            mPasswordField.setError(null);
//        }
//
//        return valid;
//    }

//    private void updateUI(FirebaseUser user) {
//
//    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
//            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
//            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
//            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
//            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }

    }



}
