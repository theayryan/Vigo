package vigo.com.vigo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static vigo.com.vigo.LoginScreen.TOTP.generateTOTP;

/**
 * Created by ayushb on 26/6/15.
 */
public class LoginScreen extends ActionBarActivity implements View.OnClickListener, NumberDialogFragment.Number {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "LoginScreen";

    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
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
                        .commit();

                if(mDialog.isShowing()){
                    mDialog.dismiss();
                }

                numberDialogFragment = NumberDialogFragment.getInstance();
                numberDialogFragment.setCancelable(false);
                numberDialogFragment.show(getSupportFragmentManager(),"NumberDialogFragment");

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
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

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
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
                mActivity.startActivityForResult(userRecoverableException.getIntent(), mRequestCode);
            } catch (GoogleAuthException fatalException) {
                onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
            }
            return null;
        }
    }

    public void getRegId(){
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
                    Log.i("GCM",  msg);

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

    public static class TOTP {

        private TOTP() {}

        /**
         * This method uses the JCE to provide the crypto algorithm.
         * HMAC computes a Hashed Message Authentication Code with the
         * crypto hash algorithm as a parameter.
         *
         * @param crypto: the crypto algorithm (HmacSHA1, HmacSHA256,
         *                             HmacSHA512)
         * @param keyBytes: the bytes to use for the HMAC key
         * @param text: the message or text to be authenticated
         */
        protected static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text){
            try {

                Mac hmac;
                hmac = Mac.getInstance(crypto);


                SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
                hmac.init(macKey);
                return hmac.doFinal(text);

            } catch (GeneralSecurityException gse) {
                throw new UndeclaredThrowableException(gse);
            }
        }


        /**
         * This method converts a HEX string to Byte[]
         *
         * @param hex: the HEX string
         *
         * @return: a byte array
         */

        private static byte[] hexStr2Bytes(String hex){
            // Adding one byte to get the right conversion
            // Values starting with "0" can be converted
            byte[] bArray = new BigInteger("10" + hex,16).toByteArray();

            // Copy all the REAL bytes, not the "first"
            byte[] ret = new byte[bArray.length - 1];
            for (int i = 0; i < ret.length; i++)
                ret[i] = bArray[i+1];
            return ret;
        }

        private static final int[] DIGITS_POWER
                // 0 1  2   3    4     5      6       7        8
                = {1,10,100,1000,10000,100000,1000000,10000000,100000000 };



        /**
         * This method generates a TOTP value for the given
         * set of parameters.
         *
         * @param key: the shared secret, HEX encoded
         * @param time: a value that reflects a time
         * @param returnDigits: number of digits to return
         *
         * @return: a numeric String in base 10 that includes
         */  //            {@link truncationDigits} digits
        //*

        public static String generateTOTP(String key, String time, String returnDigits){

            return generateTOTP(key, time, returnDigits, "HmacSHA1");

        }


        /**
         * This method generates a TOTP value for the given
         * set of parameters.
         *
         * @param key: the shared secret, HEX encoded
         * @param time: a value that reflects a time
         * @param returnDigits: number of digits to return
         * @param crypto: the crypto function to use
         *
         * @return: a numeric String in base 10 that includes
         */ //             {@link truncationDigits} digits
        //*

        public static String generateTOTP(String key, String time, String returnDigits, String crypto){

            int codeDigits = Integer.decode(returnDigits).intValue();

            String result = null;

            // Using the counter
            // First 8 bytes are for the movingFactor
            // Compliant with base RFC 4226 (HOTP)

            Log.d(TAG, time);

            while (time.length() < 16 )
                time = "0" + time;

            Log.d(TAG, time);
            Log.d(TAG, key);

            // Get the HEX in a Byte[]
            byte[] msg = hexStr2Bytes(time);



            byte[] k = hexStr2Bytes(key);





            byte[] hash = hmac_sha(crypto, k, msg);

            // put selected bytes into result int
            int offset = hash[hash.length - 1] & 0xf;

            int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

            int otp = binary % DIGITS_POWER[codeDigits];

            result = Integer.toString(otp);
            while (result.length() < codeDigits) {
                result = "0" + result;
            }
            return result;
        }

    }

    public void function(){


        // Seed for HMAC-SHA1 - 20 bytes
        String seed = "3132333435363738393031323334353637383930";


        long T0 = 0;
        long X = 30;
        long testTime[] = {59L, 1111111109L, 1111111111L, 1234567890L, 2000000000L, 20000000000L};

        String steps = "0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));


        for (int i=0; i<testTime.length; i++) {

            long T = (testTime[i] - T0)/X;
            steps = Long.toHexString(T).toUpperCase();

            //Toast.makeText(getApplicationContext(), steps, Toast.LENGTH_LONG).show();


            while (steps.length() < 16) steps = "0" + steps;

            String fmtTime = String.format("%1$-11s", testTime[i]);

            String utcTime = df.format(new Date(testTime[i]*1000));

            Toast.makeText(getApplicationContext(), generateTOTP(seed, steps, "6","HmacSHA1"), Toast.LENGTH_LONG).show();


        }

    }

}
