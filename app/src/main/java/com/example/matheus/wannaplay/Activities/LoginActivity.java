package com.example.matheus.wannaplay.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matheus.wannaplay.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;

    Button loginFacebookBtn;
    ImageView loginSloganImg;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        //Requesting Location Permission
        requestLocationPermission();

        loginSloganImg = findViewById(R.id.loginSloganImg);
        loginFacebookBtn = findViewById(R.id.loginLoginBtn);

        //Changes the Slogan and the Login Button background if the device language is Portuguese
        if (Locale.getDefault().getDisplayLanguage().equals("português")) {
            loginSloganImg.setImageResource(R.drawable.slogan);
            loginFacebookBtn.setBackgroundResource(R.drawable.btn_signin_facebook_pt);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        //Checks if there is a user already logged in
        if (userIsLogged()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            loginFacebookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                    signInWithFacebook();
                }
            });
        }

    }

    //Validates if there is a user already logged in
    public Boolean userIsLogged() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return true;
        } else {
            return false;
        }
    }

    //Function that calls and validates the login with Facebook
    private void signInWithFacebook() {

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    String email = me.optString("email");
                                    handleFacebookAccessToken(loginResult.getAccessToken(), email);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                mGraphRequest.setParameters(parameters);
                mGraphRequest.executeAsync();

                //handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    //Function that takes the credential and validates the login with Facebook
    private void handleFacebookAccessToken(final AccessToken accessToken, final String pEmail) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //Opens a login request
        firebaseAuth.fetchSignInMethodsForEmail(pEmail)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        FirebaseAuth firebaseAuth1 = FirebaseAuth.getInstance();
                        boolean check = task.getResult().getSignInMethods().isEmpty();

                        //Check if an account with the received email already exists
                        /*If it already exists, it takes the token, logs it in, and redirects it to MainActivity.
                          If it doesn't, log in and redirect to RegisterActivity*/
                        if (check) {

                            String token = AccessToken.getCurrentAccessToken().getToken();

                            final AuthCredential credential = FacebookAuthProvider.getCredential(token);
                            firebaseAuth1.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("ERROR", "" + e.getMessage());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            });

                        } else {

                            String token = AccessToken.getCurrentAccessToken().getToken();

                            final AuthCredential credential = FacebookAuthProvider.getCredential(token);
                            firebaseAuth1.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("ERROR", "" + e.getMessage());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.permissionGranted), Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.dialogTitle))
                        .setMessage(getResources().getString(R.string.dialogMessage))
                        .setPositiveButton(getResources().getString(R.string.dialogGrant), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialogDeny), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
            } else {
                Toast.makeText(this, getResources().getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
