package vigo.com.vigo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 5/7/15.
 */
public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;
    private VigoApi otpApi;
    private SharedPreferences pref;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        RestAdapter restAdapter = new RestAdapter.Builder()
                                .setEndpoint(Constants.BASE_URL)
                                .build();
                        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
                        otpApi = restAdapter.create(VigoApi.class);
                        otpApi.verifyOtp(
                                msgBody,
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
                                            Toast.makeText(context, "Your Number Has Been Verified", Toast.LENGTH_LONG).show();
                                        } else {

                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        error.printStackTrace();
                                    }
                                }
                        );

                    }
                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
