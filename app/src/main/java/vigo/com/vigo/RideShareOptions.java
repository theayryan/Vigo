package vigo.com.vigo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 2/7/15.
 */
public class RideShareOptions extends Fragment implements View.OnClickListener, InvoiceDialog.InvoiceInterface, AdapterView.OnItemClickListener, UtilityDialog.UtilityInterface {
    private FragmentActivity mActivity;
    private ListView mRidesList;
    private Button mAddRideShare;
    private VigoApi rideShareApi;
    private List<Book> ridesOption;
    private Book args;
    private Typeface mBree;
    private Typeface mComfortaa;
    private InvoiceDialog invoiceDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ride_share_layout, container, false);
        mBree = Typeface.createFromAsset(mActivity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Comfortaa-Regular.ttf");
        mRidesList = (ListView) rootView.findViewById(R.id.ride_share_listView);
        mAddRideShare = (Button) rootView.findViewById(R.id.ride_share_button);
        mAddRideShare.setOnClickListener(this);
        mAddRideShare.setTypeface(mBree);
        mRidesList.setOnItemClickListener(this);
        args = (Book) getArguments().getSerializable(Constants.DATA);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        rideShareApi = restAdapter.create(VigoApi.class);
        rideShareApi.showOptions(
                args.source,
                args.destination,
                args.date,
                args.time,
                args.vehical_type,
                args.customer_id,
                args.source_lat,
                args.source_lng,
                args.destination_lat,
                args.destination_lng,
                args.distance,
                args.time_taken,
                new Callback<RidesClass>() {
                    @Override
                    public void success(RidesClass ridesClass, Response response) {
                        if (ridesClass.ride != null && ridesClass.ride.size() > 0) {
                            ridesOption = ridesClass.ride;
                            RidesShareAdapter ridesShareAdapter = new RidesShareAdapter(mActivity, ridesClass.ride, mActivity);
                            mRidesList.setAdapter(ridesShareAdapter);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        Toast.makeText(mActivity, "Sorry, some error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = super.getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ride_share_button:
                getRideShareFare();
                break;
        }

    }

    public void makeNewRideShare() {
        rideShareApi.addRideShare(
                args.source,
                args.destination,
                args.date,
                args.time,
                args.vehical_type,
                args.customer_id,
                args.source_lat,
                args.source_lng,
                args.destination_lat,
                args.destination_lng,
                args.distance,
                args.time_taken,
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
                            Toast.makeText(mActivity, "Some Error Occurred. Please Try Again.", Toast.LENGTH_SHORT).show();
                        } else {
                            //ask what is expected
                            if (invoiceDialog.isVisible()) {
                                invoiceDialog.dismiss();
                            }
                            UtilityDialog dialog = UtilityDialog.getInstance(mActivity.getString(R.string.booking_confirmed));
                            dialog.setCancelable(true);
                            dialog.show(mActivity.getSupportFragmentManager(), "UtilityDialog");
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        Toast.makeText(mActivity, "Some Error Occurred. Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void getRideShareFare() {
        rideShareApi.shareFare(
                args.distance + "",
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
                        if (result.contains("false")) {
                            try {
                                JSONObject responseJson = new JSONObject(result);
                                String actual = responseJson.getString("actual");
                                String discount = responseJson.getString("discount");
                                String fare = responseJson.getString("fare");

                                invoiceDialog = InvoiceDialog.getInstance(actual, fare, discount, args.time_taken);
                                invoiceDialog.setTargetFragment(RideShareOptions.this, 0);
                                invoiceDialog.setCancelable(false);
                                invoiceDialog.show(mActivity.getSupportFragmentManager(), "InvoiceDialog");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(mActivity, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(mActivity, R.string.error_occurred, Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        Toast.makeText(mActivity, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book ride = ridesOption.get(position);
        rideShareApi.addChosenRide(
                ride.trip_id,
                ride.source,
                ride.destination,
                ride.date,
                ride.time,
                ride.vehical_type,
                ride.customer_id,
                ride.source_lat,
                ride.source_lng,
                ride.destination_lat,
                ride.destination_lng,
                ride.distance,
                ride.time_taken,
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
                            Toast.makeText(mActivity, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                        } else {
                            //show fare
                            Toast.makeText(mActivity, "Your ride has been booked", Toast.LENGTH_SHORT).show();
                            UtilityDialog dialog = UtilityDialog.getInstance(mActivity.getString(R.string.booking_confirmed));
                            dialog.setCancelable(true);
                            dialog.show(mActivity.getSupportFragmentManager(), "UtilityDialog");
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        Toast.makeText(mActivity, "Some Error Occurred. Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void confirmUtility() {
        Intent intent = new Intent(mActivity, FutureRidesActivity.class);
        startActivity(intent);
        mActivity.finish();
    }

    @Override
    public void confirmBooking() {
        makeNewRideShare();
    }

    @Override
    public void cancel() {

    }
}
