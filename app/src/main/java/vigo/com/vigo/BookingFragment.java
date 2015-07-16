package vigo.com.vigo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.android.gms.maps.model.LatLng;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 22/6/15.
 */
public class BookingFragment extends Fragment implements View.OnClickListener, InvoiceDialog.InvoiceInterface, UtilityDialog.UtilityInterface, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private static BookingFragment instance;
    private static LatLng sourceLatlng;
    private static LatLng destLatlng;
    private static String source;
    final Calendar calendar = Calendar.getInstance();
    private FragmentActivity mActivity;
    private TextView mName;
    private TextView mPickUpPoint;
    private TextView mDropPoint;
    private TextView mDatePicker;
    private TextView mTimePicker;
    private Button mRideShare;
    private RadioButton mBulk;
    private Typeface mBree;
    private Button mBookButton;
    private TextView mHeading;
    private String date;
    private String time;
    private VigoApi bookApi;
    private Bundle argument;
    private TextView mModeTransport;
    private RadioGroup mGroup;
    private InvoiceDialog invoiceDialog;
    private String durationValue;
    private int distanceValue;
    private boolean BOOK_PRESSED = false;
    private ProgressDialog mPDialog;
    private SharedPreferences pref;
    private ImageView mBack;
    private Typeface mButtonFont;
    private Typeface mCabin;

    public void showProgressDialog() {
        if (mPDialog == null) {
            mPDialog = new ProgressDialog(mActivity);
            mPDialog.setMessage("Fetching");
            mPDialog.show();
        } else if (!mPDialog.isShowing()) {
            mPDialog.setMessage("Fetching");
            mPDialog.show();
        }
        mPDialog.setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.booking_details_layout, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        bookApi = restAdapter.create(VigoApi.class);
        argument = this.getArguments();
        mBack = (ImageView) rootView.findViewById(R.id.back_button_image);
        mGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup);
        mName = (TextView) rootView.findViewById(R.id.name);
        mPickUpPoint = (TextView) rootView.findViewById(R.id.pickup_point);
        mDropPoint = (TextView) rootView.findViewById(R.id.drop_point);
        mDatePicker = (TextView) rootView.findViewById(R.id.date);
        mTimePicker = (TextView) rootView.findViewById(R.id.time);
        mRideShare = (Button) rootView.findViewById(R.id.ride_share);
        mModeTransport = (TextView) rootView.findViewById(R.id.mode_of_transport);
        mHeading = (TextView) rootView.findViewById(R.id.heading);

        mButtonFont = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Button_Font.ttf");
        mBree = Typeface.createFromAsset(mActivity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mCabin = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Cabin-Regular.ttf");

        mHeading.setTypeface(mBree, Typeface.BOLD);
        mHeading.setText("Booking Details");


        mTimePicker.setTypeface(mCabin);
        mDatePicker.setTypeface(mCabin);
        mPickUpPoint.setTypeface(mCabin);
        mDropPoint.setTypeface(mCabin);
        mName.setTypeface(mCabin);
        mRideShare.setTypeface(mButtonFont);
        mBookButton = (Button) rootView.findViewById(R.id.Book);
        mBookButton.setTypeface(mButtonFont);
        mBookButton.setOnClickListener(this);
        mRideShare.setOnClickListener(this);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        mModeTransport.setTypeface(mCabin);


        mPickUpPoint.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mDropPoint.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mName.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mTimePicker.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mDatePicker.setTextColor(mActivity.getResources().getColor(R.color.Black));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mName.setText(prefs.getString(Constants.USER_NAME, ""));


        if (!TextUtils.isEmpty(argument.getString(Constants.SOURCE_STRING)))
            mPickUpPoint.setText(argument.getString(Constants.SOURCE_STRING));

        if (!TextUtils.isEmpty(argument.getString(Constants.DEST_STRING)))
            mDropPoint.setText(argument.getString(Constants.DEST_STRING));
        if (argument.getBoolean(Constants.NOW)) {
            long time30 = System.currentTimeMillis() + 1000 * 60 * 30;
            java.util.Date date1 = new java.util.Date(time30);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            mTimePicker.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
            mDatePicker.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
            date = (calendar.get(Calendar.YEAR)) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
            time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + "00";
        } else if (argument.getBoolean(Constants.LATER)) {
            mDatePicker.setOnClickListener(this);
            mTimePicker.setOnClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = super.getActivity();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date:
                if (!isDetached() || !isRemoving()) {
                    final DatePickerDialog datePickerDialog =
                            DatePickerDialog.newInstance(this,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH), false);
                    datePickerDialog.setYearRange(2015, 2016);
                    datePickerDialog.show(getChildFragmentManager(), DATEPICKER_TAG);

                }
                break;
            case R.id.time:
                if (!isDetached() || isRemoving()) {
                    final TimePickerDialog timePickerDialog =
                            TimePickerDialog.newInstance(this,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE), true, false);
                    long time = System.currentTimeMillis();
                    java.util.Date date1 = new java.util.Date(time);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date1);
                    timePickerDialog.setStartTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    timePickerDialog.show(getChildFragmentManager(), TIMEPICKER_TAG);
                }
                break;
            case R.id.Book:
                BOOK_PRESSED = true;
                int time = getTime();
                int difference = time-((int)(System.currentTimeMillis()/1000));
                if(difference>3*24*60*60) {
                    Toast.makeText(mActivity,"Please choose date and time within the next 3 days",Toast.LENGTH_SHORT).show();
                    break;
                }
                sendDistance();
                showProgressDialog();
                break;
            case R.id.ride_share:
                BOOK_PRESSED = false;
                showProgressDialog();
                sendDistance();
                break;
        }
    }

    public void sendRideShare() {
        if (mPDialog.isShowing()) {
            mPDialog.dismiss();
        }
        String autoMode = getModeofTransport(mGroup.getCheckedRadioButtonId());
        if (TextUtils.isEmpty(autoMode)) {
            Toast.makeText(mActivity, R.string.choose_mode_transport, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(mActivity, R.string.pick_date_toast, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(mActivity, R.string.pick_time_toast, Toast.LENGTH_LONG).show();
            return;
        }
        Bundle arguments = new Bundle();
        Book args = new Book();
        args.source = mPickUpPoint.getText().toString();
        args.destination = mDropPoint.getText().toString();
        args.date = date;
        args.time = time;
        args.customer_id = pref.getString(Constants.AUTH_TOKEN, "");
        args.vehical_type = autoMode;
        args.source_lat = Double.toString(argument.getDouble(Constants.SOURCE_LAT));
        args.source_lng = Double.toString(argument.getDouble(Constants.SOURCE_LON));
        args.destination_lat = Double.toString(argument.getDouble(Constants.DEST_LAT));
        args.destination_lng = Double.toString(argument.getDouble(Constants.DEST_LON));
        args.distance = Integer.toString(distanceValue);
        args.time_taken = durationValue;
        arguments.putSerializable(Constants.DATA, args);
        RideShareOptions shareFragment = new RideShareOptions();
        shareFragment.setArguments(arguments);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.maps_fragment, shareFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void sendDistance() {
        RestAdapter restAdapterDistance = new RestAdapter.Builder()
                .setEndpoint(Constants.DISTANCE_MATRIX)
                .build();
        restAdapterDistance.setLogLevel(RestAdapter.LogLevel.FULL);
        VigoApi distanceApi = restAdapterDistance.create(VigoApi.class);
        distanceApi.getDistance(
                Double.toString(argument.getDouble(Constants.SOURCE_LAT)) + "," + Double.toString(argument.getDouble(Constants.SOURCE_LON)),
                Double.toString(argument.getDouble(Constants.DEST_LAT)) + "," + Double.toString(argument.getDouble(Constants.DEST_LON)),
                getTime(),
                Constants.GOOGLE_API_KEY,
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
                        try {

                            if (BOOK_PRESSED == true) {
                                getDistancefromResponse(result);
                            } else {
                                getDistanceRideShare(result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        //Toast.makeText(getApplicationContext(),"Some Error Occurred. Please Try Again.",Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void getDistanceRideShare(String response) throws JSONException {
        if (mPDialog.isShowing()) {
            mPDialog.dismiss();
        }
        JSONObject responseJson = new JSONObject(response);
        JSONArray rows = responseJson.getJSONArray("rows");
        JSONObject element = rows.getJSONObject(0);
        JSONArray elementsArray = element.getJSONArray("elements");
        JSONObject element1 = elementsArray.getJSONObject(0);
        JSONObject distanceObject = element1.getJSONObject("distance");
        distanceValue = distanceObject.getInt("value") / 1000;
        JSONObject durationObject = element1.getJSONObject("duration");
        durationValue = durationObject.getString("text");
        sendRideShare();
    }

    public void getDistancefromResponse(String response) throws JSONException {

        JSONObject responseJson = new JSONObject(response);
        JSONArray rows = responseJson.getJSONArray("rows");
        JSONObject element = rows.getJSONObject(0);
        JSONArray elementsArray = element.getJSONArray("elements");
        JSONObject element1 = elementsArray.getJSONObject(0);
        JSONObject distanceObject = element1.getJSONObject("distance");
        distanceValue = distanceObject.getInt("value") / 1000;
        JSONObject durationObject = element1.getJSONObject("duration");
        durationValue = durationObject.getString("text");
        bookApi.bulkFare(
                distanceValue + "",
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
                                if (mPDialog.isShowing()) {
                                    mPDialog.dismiss();
                                }
                                JSONObject responseJson = new JSONObject(result);
                                String actual = responseJson.getString("actual");
                                String discount = responseJson.getString("discount");
                                String fare = responseJson.getString("fare");

                                invoiceDialog = InvoiceDialog.getInstance(actual, fare, discount, durationValue);
                                invoiceDialog.setTargetFragment(BookingFragment.this, 0);
                                invoiceDialog.setCancelable(false);
                                invoiceDialog.show(mActivity.getSupportFragmentManager(), "InvoiceDialog");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(mActivity, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                                if (mPDialog.isShowing()) {
                                    mPDialog.dismiss();
                                }
                            }

                        } else {
                            Toast.makeText(mActivity, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                            if (mPDialog.isShowing()) {
                                mPDialog.dismiss();
                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        error.getKind();
                        if (mPDialog.isShowing()) {
                            mPDialog.dismiss();
                        }
                        Toast.makeText(mActivity, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private String getModeofTransport(int checkedRadioButtonId) {
        if (checkedRadioButtonId == R.id.auto)
            return "Auto";
        else if (checkedRadioButtonId == R.id.taxi)
            return "Taxi";
        else if (checkedRadioButtonId == R.id.bus)
            return "Bus";
        else
            return null;
    }

    public int getTime() {
        DateFormat df = new SimpleDateFormat("yyyy/M/d HH:m:s");
        try {
            Date myDate = df.parse(date + " " + time);
            return (int) (myDate.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        mDatePicker.setText(day + "/" + (month + 1) + "/" + year);
        date = (year) + "/" + (month + 1) + "/" + day;
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        mTimePicker.setText(hour + ":" + minute);
        time = hour + ":" + minute + ":" + "00";
    }

    @Override
    public void confirmBooking() {
        if (invoiceDialog.isVisible()) {
            invoiceDialog.dismiss();
        }
        String mode = getModeofTransport(mGroup.getCheckedRadioButtonId());
        if (TextUtils.isEmpty(mode)) {
            Toast.makeText(mActivity, R.string.choose_mode_transport, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(mActivity, R.string.pick_date_toast, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(mActivity, R.string.pick_time_toast, Toast.LENGTH_LONG).show();
            return;
        }
        bookApi.makeBooking(
                mPickUpPoint.getText().toString(),
                mDropPoint.getText().toString(),
                date,
                time,
                mode,
                pref.getString(Constants.AUTH_TOKEN, ""),
                Double.toString(argument.getDouble(Constants.SOURCE_LAT)),
                Double.toString(argument.getDouble(Constants.SOURCE_LON)),
                Double.toString(argument.getDouble(Constants.DEST_LAT)),
                Double.toString(argument.getDouble(Constants.DEST_LON)),
                Integer.toString(distanceValue),
                durationValue,
                new Callback<Response>() {
                    @Override
                    public void success(Response s, Response response) {
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        try {

                            reader = new BufferedReader(new InputStreamReader(s.getBody().in()));

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
                            Toast.makeText(mActivity, R.string.booking_successful, Toast.LENGTH_SHORT).show();
                            UtilityDialog dialog = UtilityDialog.getInstance(mActivity.getString(R.string.booking_confirmed));
                            dialog.setCancelable(true);
                            dialog.show(mActivity.getSupportFragmentManager(), "UtilityDialog");
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void cancel() {

    }

    @Override
    public void confirmUtility() {
        Intent intent = new Intent(mActivity, FutureRidesActivity.class);
        startActivity(intent);
        mActivity.finish();
    }
}
