package vigo.com.vigo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.android.gms.maps.model.LatLng;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 22/6/15.
 */
public class BookingFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.booking_details_layout, container, false);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        bookApi = restAdapter.create(VigoApi.class);
        argument = this.getArguments();
        mGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup);
        mName = (TextView) rootView.findViewById(R.id.name);
        mPickUpPoint = (TextView) rootView.findViewById(R.id.pickup_point);
        mDropPoint = (TextView) rootView.findViewById(R.id.drop_point);
        mDatePicker = (TextView) rootView.findViewById(R.id.date);
        mTimePicker = (TextView) rootView.findViewById(R.id.time);
        mRideShare = (Button) rootView.findViewById(R.id.ride_share);
        mModeTransport = (TextView) rootView.findViewById(R.id.mode_of_transport);
        mHeading = (TextView) rootView.findViewById(R.id.heading);

        mBree = Typeface.createFromAsset(mActivity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mHeading.setTypeface(mBree, Typeface.BOLD);
        mHeading.setText("Booking Details");


        mTimePicker.setTypeface(mBree);
        mDatePicker.setTypeface(mBree);
        mPickUpPoint.setTypeface(mBree);
        mDropPoint.setTypeface(mBree);
        mName.setTypeface(mBree);
        mRideShare.setTypeface(mBree);
        mBookButton = (Button) rootView.findViewById(R.id.Book);
        mBookButton.setTypeface(mBree);
        mBookButton.setOnClickListener(this);
        mRideShare.setOnClickListener(this);
        mModeTransport.setTypeface(mBree);


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
            mDatePicker.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
            date = (calendar.get(Calendar.YEAR) - 1900) + "/" + (calendar.get(Calendar.MONTH) - 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
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
                    datePickerDialog.setYearRange(2015, 2028);
                    datePickerDialog.show(getChildFragmentManager(), DATEPICKER_TAG);

                }
                break;
            case R.id.time:
                if (!isDetached() || isRemoving()) {
                    final TimePickerDialog timePickerDialog =
                            TimePickerDialog.newInstance(this,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE), true, false);
                    timePickerDialog.show(getChildFragmentManager(), TIMEPICKER_TAG);
                }
                break;
            case R.id.Book:
                String mode = getModeofTransport(mGroup.getCheckedRadioButtonId());
                if (TextUtils.isEmpty(mode)) {
                    Toast.makeText(mActivity, "Please choose a mode of transport", Toast.LENGTH_LONG).show();
                    break;
                }
                if (TextUtils.isEmpty(date)) {
                    Toast.makeText(mActivity, "Please pick a date", Toast.LENGTH_LONG).show();
                    break;
                }
                if (TextUtils.isEmpty(time)) {
                    Toast.makeText(mActivity, "Please pick a time", Toast.LENGTH_LONG).show();
                    break;
                }
                bookApi.makeBooking(
                        mPickUpPoint.getText().toString(),
                        mDropPoint.getText().toString(),
                        date,
                        time,
                        mode,
                        "46151218",
                        Double.toString(argument.getDouble(Constants.SOURCE_LAT)),
                        Double.toString(argument.getDouble(Constants.SOURCE_LON)),
                        Double.toString(argument.getDouble(Constants.DEST_LAT)),
                        Double.toString(argument.getDouble(Constants.DEST_LON)),
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
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                            }
                        }
                );
                break;

        }
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


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        mDatePicker.setText(day + "/" + (month + 1) + "/" + year);
        date = (year - 1900) + "/" + month + "/" + day;
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        mTimePicker.setText(hour + ":" + minute);
        time = hour + ":" + minute + ":" + "00";
    }
}
