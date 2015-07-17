package vigo.com.vigo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by ayushb on 15/7/15.
 */
public class VerifyActivity extends Activity {

    private SharedPreferences preferences;
    private VigoApi otpApi;
    private SharedPreferences pref;
    private Typeface mButtonFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification_layout);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        TextView mHeading = (TextView) findViewById(R.id.verify_otp);
        final EditText mUserEnteredOtp = (EditText) findViewById(R.id.otp_user_entered);
        Button mVerify = (Button) findViewById(R.id.verify_button);
        Typeface mCabin = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface mComfortaa = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        mButtonFont = Typeface.createFromAsset(getAssets(), "fonts/Button_Font.ttf");
        mVerify.setTypeface(mButtonFont);
        mUserEnteredOtp.setTypeface(mComfortaa);
        mHeading.setTypeface(mCabin);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        otpApi = restAdapter.create(VigoApi.class);
        generateOtp();
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mUserEnteredOtp.getText().toString())) {
                    otpApi.verifyOtp(
                            mUserEnteredOtp.getText().toString(),
                            pref.getString(Constants.AUTH_TOKEN, ""),
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
                                    if (!result.contains("NOT_VERIFIED")) {
                                        Log.d("RECIEVED", "positive");
                                        Toast.makeText(getApplicationContext(), "Your Number Has Been Verified", Toast.LENGTH_LONG).show();
                                        pref.edit().putBoolean(Constants.OTP, true);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Your Number Could Not Get Verified Please Try Again Later", Toast.LENGTH_LONG).show();
                                    }
                                    Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                    VerifyActivity.this.finish();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    error.printStackTrace();
                                }
                            }
                    );
                } else {
                    Toast.makeText(VerifyActivity.this, "You Haven't Entered Any OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void generateOtp() {
        otpApi.generateOtp(
                pref.getString(Constants.AUTH_TOKEN, ""),
                pref.getString(Constants.USER_NUMBER, ""),
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
                            Toast.makeText(getApplicationContext(), "Verification did not succeed please try again from the menu.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        if (error.getKind() == RetrofitError.Kind.NETWORK)
                            Toast.makeText(getApplicationContext(), "Network Error Occurred Try Again From The Menu", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
