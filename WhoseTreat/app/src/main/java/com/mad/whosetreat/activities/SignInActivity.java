package com.mad.whosetreat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mad.whosetreat.R;


/**
 * SignInActivity is called when there is no authenticated user found.
 * At this moment, this class offer authentication with Google account.
 */
public class SignInActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "TAG_SignInActivity";
    private static final int SIGN_IN_REQUEST = 1001;
    private static final String SIGN_IN_KEY = "signed in key";
    private RelativeLayout mGoogleLoginLayout;
    private RelativeLayout mPhoneLoginLayout;
    private RelativeLayout mEmailLoginLayout;
    private SignInButton mGoogleBtn;

    private GoogleApiClient mGoogleApiClient;
//    private FirebaseAuth mFirebaseAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    /**
     * onCreate finds and links views
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Assign fields
//        mGoogleLoginLayout = (RelativeLayout) findViewById(R.id.google_sign_layout);
//        mPhoneLoginLayout = (RelativeLayout) findViewById(R.id.phone_sign_layout);
//        mEmailLoginLayout = (RelativeLayout) findViewById(R.id.email_sign_layout);
        mGoogleBtn = findViewById(R.id.google_button);


        // Set click listeners
        mGoogleBtn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.auth_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.d(TAG, "onAuthSatateChanged: signed_in" + user.getUid());
//                } else {
//                    Log.d(TAG, "onAuthSatateChanged: signed_out" );
//                }
//            }
//        };


    }

    /**
     * handles onclick event of login options button.
     * At the moment, only Google authentication is visible and available to user
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_button:
                signInWithGoogle();
                break;
            default:
                return;
        }
    }

    /**
     * instantiate new intent to log in via google account
     */
    private void signInWithGoogle() {
        Intent gSignIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(gSignIntent, SIGN_IN_REQUEST);
    }

    /**
     * handles the result of activity.
     * At the moment, handles Google event only
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == SIGN_IN_REQUEST) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//
//            if (result.isSuccess()) {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = result.getSignInAccount();
//                firebaseAuthWithGoogle(account);
//
//            } else {
//                // Google Sign In failed
//                Log.e(TAG, "Google Sign In failed." + result.toString());
//                Toast.makeText(SignInActivity.this, "Authentication failed. \nPlease try again later",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
    }

//    /**
//     * log in to firebase by google authentication
//     * @param acct
//     */
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle called: " + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
////        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
////            @Override
////            public void onComplete(@NonNull Task<AuthResult> task) {
////                Log.d(TAG, "signInWithCredential  onComplete: " + task.isSuccessful());
////
////            }
////        });
//
//        mFirebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential isSuccessful: " + task.isSuccessful());
//
//                        if (!task.isSuccessful()) {
//                            Log.d(TAG, "signInWithCredential: "+  task.getException());
//                            Toast.makeText(SignInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
//
//                            handleFirebaseGoogleAuthResult(task.getResult());
////                            startActivity(new Intent(SignInActivity.this, ChatMainActivity.class));
////                            finish();
//                        }
//                    }
//                });
//    }

//    /**
//     * handles the result of log in trial to Firebase with google account.
//     * @param authResult
//     */
//    private void handleFirebaseGoogleAuthResult(AuthResult authResult) {
//        if (authResult != null) {
//            FirebaseUser user = authResult.getUser();
//            Toast.makeText(this, "Succeeded! ", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "User email: " + user.getEmail());
//
//            Intent intent = new Intent(this, ChatMainActivity.class);
//            intent.putExtra(SIGN_IN_KEY, true);
//            setResult(RESULT_OK, intent);
//            finish();
//        }
//    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed: " + connectionResult.toString());
    }

//   private void sendEmailVerification() {
//        // Disable button
////        findViewById(R.id.verify_email_button).setEnabled(false);
//
//        // Send verification email
//        // [START send_email_verification]
//        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // [START_EXCLUDE]
//                        // Re-enable button
////                        findViewById(R.id.verify_email_button).setEnabled(true);
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(SignInActivity.this,
//                                    "Verification email sent to " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e(TAG, "sendEmailVerification", task.getException());
//                            Toast.makeText(SignInActivity.this,
//                                    "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

//    private void signInAnonymously() {
//        mFirebaseAuth.signInAnonymously()
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInAnonymously:success");
//                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
//                        } else {
//                            // If sign in fails, display a item_message to the user.
//                            Log.w(TAG, "signInAnonymously:failure", task.getException());
//                            Toast.makeText(SignInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mFirebaseAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(mAuthListener != null) {
//            mFirebaseAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
}
