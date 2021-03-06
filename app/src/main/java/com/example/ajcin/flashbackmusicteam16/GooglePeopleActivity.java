package com.example.ajcin.flashbackmusicteam16;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.PeopleScopes;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GooglePeopleActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    private static final String TAG = "GoogleActivity";

    GoogleApiClient mGoogleApiClient;

    final int RC_INTENT = 200;
    final int RC_API_CHECK = 100;

    SignInButton signInButton;
    LinearLayout frameLogin;
    Toolbar toolbar;
    ProgressBar progressBar;
    Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_google_people);

        signInButton = (SignInButton) findViewById(R.id.main_googlesigninbtn);
        signInButton.setOnClickListener(this);

        frameLogin = (LinearLayout) findViewById(R.id.frame_login);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(Constants.WEB_CLIENT_ID)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                        new Scope(PeopleScopes.CONTACTS_READONLY))
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void getIdToken() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_INTENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_INTENT:
                Log.d(TAG, "sign in result");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
                    // This is what we need to exchange with the server.
                    Log.d(TAG, "auth Code:" + acct.getServerAuthCode());
                    String personName = acct.getDisplayName();
                    resultIntent = new Intent();
                    resultIntent.putExtra("user_name", personName);
                    new PeoplesAsync().execute(acct.getServerAuthCode());

                } else {

                    Log.d(TAG, result.getStatus().toString() + "\nmsg: " + result.getStatus().getStatusMessage());
                }
                break;

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("connection", "msg: " + connectionResult.getErrorMessage());

        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = mGoogleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(), RC_API_CHECK);
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_googlesigninbtn:
                Log.d(TAG, "btn click");
                getIdToken();
                break;

        }
    }

    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    class PeoplesAsync extends AsyncTask<String, Void, ArrayList<String>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            updateUI();

        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            ArrayList<String> nameList = new ArrayList<>();

            try {
                People peopleService = PeopleHelper.setUp(getApplicationContext(), params[0]);

                ListConnectionsResponse response = peopleService.people().connections()
                        .list("people/me")
                        .setRequestMaskIncludeField("person.names")
                        .execute();
                List<Person> connections = response.getConnections();

                for (Person person : connections) {
                    if (!person.isEmpty()) {
                        List<Name> names = person.getNames();
                        if (names != null)
                            for (Name name : names)
                                nameList.add(name.getDisplayName());

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return nameList;
        }


        @Override
        protected void onPostExecute(ArrayList<String> nameList) {
            super.onPostExecute(nameList);

            progressBar.setVisibility(View.GONE);

            Toast toast = Toast.makeText(getApplicationContext(), "Friends Have Been Synced Successfully", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            resultIntent.putStringArrayListExtra("contact_names",nameList);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    void updateUI() {

        progressBar.setVisibility(View.VISIBLE);


        frameLogin.animate().alpha(0).setDuration(400).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                UIUtil.doCircularReveal(toolbar);

                ObjectAnimator animator = ObjectAnimator.ofFloat(toolbar, "elevation", 4F);
                animator.setStartDelay(400);
                animator.setDuration(400);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();


    }

}