package vigo.com.vigo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 26/6/15.
 */
public class LoginScreen extends ActionBarActivity implements View.OnClickListener, NumberDialogFragment.Number {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "LoginScreen";

    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
    private static final int REQUEST_RECOVER = 1001;
    private static JSONObject PROFILE_DATA;
    AccountManager mAccountManager;
    String token;
    int serverCode;
    private SignInButton btnSignIn;
    private Button btnSignOut;
    private SharedPreferences pref;
    private Typeface mComfortaa;
    private Typeface mBree;
    private TextView mHeading;
    private TextView mSubHeading;
    private LinearLayout background;
    private ProgressDialog mDialog;
    private GoogleCloudMessaging gcm;
    private String regid;
    private NumberDialogFragment numberDialogFragment;
    private VigoApi registerApi;
    private String AUTH_TOKEN;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);
        getSupportActionBar().hide();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        background = (LinearLayout) findViewById(R.id.login_background);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        mHeading = (TextView) findViewById(R.id.app_name);
        mSubHeading = (TextView) findViewById(R.id.app_subtitle);
        mDialog = new ProgressDialog(this);

        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        mBree = Typeface.createFromAsset(getAssets(), "fonts/BreeSerif-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");

        mHeading.setTypeface(mBree);
        mSubHeading.setTypeface(mComfortaa);
        }

    private String[] getAccountNames() {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager
        .getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
                names[i] = accounts[i].name;
        }
        return names;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RECOVER) {
            syncGoogleAccount();
        }
    }

    private GetNameInForeground getTask(LoginScreen activity, String email,

                                        String scope) {

        return new GetNameInForeground(activity, email, scope);
    }


    public void syncGoogleAccount() {

        if (isNetworkAvailable() == true) {
            String[] accountarrs = getAccountNames();
            if (accountarrs.length > 0) {
                    getTask(LoginScreen.this, accountarrs[0], SCOPE).execute();
            } else {
                Toast.makeText(LoginScreen.this, "No Google Account Available!",
                    Toast.LENGTH_SHORT).show();
                if(mDialog.isShowing())
                    mDialog.dismiss();
                }
        } else {
            Toast.makeText(LoginScreen.this, "Internet not available please try again.",
                Toast.LENGTH_SHORT).show();
            if(mDialog.isShowing())
                mDialog.dismiss();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
            this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "***Available***");
            return true;
        }
        Log.e("Network Testing", "***Not Available***");
        return false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                // Signin button clicked
                mDialog.setMessage("Fetching Data");
                mDialog.show();
                syncGoogleAccount();
                break;
            case R.id.btn_sign_out:
                // Signout button clicked

                break;
        }
    }


    public void getProfileInformation() {
        try {
            if (PROFILE_DATA != null) {
                String personName = null;
                String personPhotoUrl = null;

                String email = null;
                if (PROFILE_DATA.has("email"))
                    email = PROFILE_DATA.getString("email");
            if (PROFILE_DATA.has("picture")) {
                    personPhotoUrl = PROFILE_DATA.getString("picture");
                }
                if (PROFILE_DATA.has("name")) {
                    personName = PROFILE_DATA.getString("name");
                }
                if(TextUtils.isEmpty(personName)||TextUtils.isEmpty(personPhotoUrl)||TextUtils.isEmpty(regid))
                    return;

                pref.edit().putString(Constants.USER_EMAIL, email)
                        .putString(Constants.USER_NAME, personName)
                        .putString(Constants.USER_PIC, personPhotoUrl)
                        .putBoolean(Constants.IS_LOGGED_IN, true)
                        .putString(Constants.GCM_REG_ID,regid)
                        .putString(Constants.AUTH_TOKEN, AUTH_TOKEN)
                        .commit();

                if(mDialog.isShowing()){
                    mDialog.dismiss();
                }

                numberDialogFragment = NumberDialogFragment.getInstance();
                numberDialogFragment.setCancelable(false);
                numberDialogFragment.show(getSupportFragmentManager(),"NumberDialogFragment");

            } else {
               /* Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getNumber(String number) {
        if(TextUtils.isEmpty(number)){
            Toast.makeText(this, "Please Enter Valid Number",Toast.LENGTH_SHORT).show();
            if(numberDialogFragment.isVisible()){
                numberDialogFragment.dismiss();
            }
            numberDialogFragment = NumberDialogFragment.getInstance();
            numberDialogFragment.setCancelable(false);
            numberDialogFragment.show(getSupportFragmentManager(),"NumberDialogFragment");
        }
        else{
            //actually supposed to send msg here with totp
            String user_number = number;
            pref.edit().putString(Constants.USER_NUMBER,user_number).commit();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.BASE_URL)
                    .build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            registerApi = restAdapter.create(VigoApi.class);
            registerApi.register(
                    regid,
                    pref.getString(Constants.AUTH_TOKEN, ""),
                    pref.getString(Constants.USER_NAME, ""),
                    number,
                    pref.getString(Constants.USER_EMAIL, ""),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            BufferedReader reader = null;
                            StringBuilder sb = new StringBuilder();
                            try {

                                reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                                String line;

                                try {
                                    while ((line = reader.readLine()) != null) {
                                        sb.append(line);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            String result = sb.toString();
                            Log.d("Response", result);
                            if (result.contains("UNSUCCESSFUL")) {
                                Toast.makeText(getApplicationContext(), "Some Error Occurred. Please Try Again.", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                                startActivity(intent);
                                LoginScreen.this.finish();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Some Error Occurred. Please Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

        }


    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(Constants.GCM_PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                getProfileInformation();
            }
        }.execute(null, null, null);
    }

    public static class CallbackReceiver extends BroadcastReceiver {
        public static final String TAG = "CallbackReceiver";

        @Override
        public void onReceive(Context context, Intent callback) {
            Bundle extras = callback.getExtras();
            Intent intent = new Intent(context, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(extras);
            Log.i(TAG, "Received broadcast. Resurrecting activity");
            context.startActivity(intent);
        }
    }

    public abstract class AbstractGetNameTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "TokenInfoTask";
        public String GOOGLE_USER_DATA = "No_data";
        protected LoginScreen mActivity;
        protected String mScope;
        protected String mEmail;
        protected int mRequestCode;

        AbstractGetNameTask(LoginScreen activity, String email, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                fetchNameFromProfileServer();

            } catch (IOException ex) {
                onError("Following Error occured, please try again. "
                        + ex.getMessage(), ex);
            } catch (JSONException e) {
                onError("Bad response: " + e.getMessage(), e);
            }
            return null;
        }

        protected void onError(String msg, Exception e) {
            if (e != null) {
                Log.e(TAG, "Exception: ", e);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getRegId();
        }



        /**
         * Get a authentication token if one is not available. If the error is not
         * recoverable then it displays the error message on parent activity.
         */
        protected abstract String fetchToken() throws IOException;

        /**
         * Contacts the user info server to get the profile of the user and extracts
         * the first name of the user from the profile. In order to authenticate
         * with the user info server the method first fetches an access token from
         * Google Play services.
         *
         * @return
         * @throws IOException   if communication with user info server failed.
         * @throws JSONException if the response from the server could not be parsed.
         */
        private void fetchNameFromProfileServer() throws IOException, JSONException {
            String token = fetchToken();
            URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int sc = con.getResponseCode();
            if (sc == 200) {
                InputStream is = con.getInputStream();
                GOOGLE_USER_DATA = readResponse(is);
                is.close();

            /*Intent intent=new Intent(mActivity,MainActivity.class);
            intent.putExtra("email_id", mEmail);
            mActivity.startActivity(intent);
            mActivity.finish();*/

                LoginScreen.PROFILE_DATA = new JSONObject(this.GOOGLE_USER_DATA);
                AUTH_TOKEN = token;
                return;
            } else if (sc == 401) {
                GoogleAuthUtil.invalidateToken(mActivity, token);
                onError("Server auth error, please try again.", null);
                //Toast.makeText(mActivity, "Please try again", Toast.LENGTH_SHORT).show();
                //mActivity.finish();
                return;
            } else {
                onError("Server returned the following error code: " + sc, null);
                return;
            }
        }

        /**
         * Reads the response from the input stream and returns it as a string.
         */
        private String readResponse(InputStream is) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            int len = 0;
            while ((len = is.read(data, 0, data.length)) >= 0) {
                bos.write(data, 0, len);
            }
            return new String(bos.toByteArray(), "UTF-8");
        }

    }

    public class GetNameInForeground extends AbstractGetNameTask {

        public GetNameInForeground(LoginScreen activity, String email, String scope) {
            super(activity, email, scope);
        }

        /**
         * Get a authentication token if one is not available. If the error is not recoverable then
         * it displays the error message on parent activity right away.
         */
        @Override
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            } catch (GooglePlayServicesAvailabilityException playEx) {
                // GooglePlayServices.apk is either old, disabled, or not present.
            } catch (UserRecoverableAuthException userRecoverableException) {
                // Unable to authenticate, but the user can fix this.
                // Forward the user to the appropriate activity.
                mActivity.startActivityForResult(userRecoverableException.getIntent(), REQUEST_RECOVER);
            } catch (GoogleAuthException fatalException) {
                onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
            }
            return null;
        }
    }



}
