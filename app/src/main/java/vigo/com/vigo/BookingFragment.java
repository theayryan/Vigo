package vigo.com.vigo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RadioButton;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.android.gms.maps.model.LatLng;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 22/6/15.
 */
public class BookingFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static BookingFragment instance;
    private static LatLng sourceLatlng;
    private static LatLng destLatlng;
    private static String source;
    private FragmentActivity mActivity;
    private TextView mName;
    private TextView mPickUpPoint;
    private TextView mDropPoint;
    private TextView mDatePicker;
    private TextView mTimePicker;
    private RadioButton mRideShare;
    private RadioButton mBulk;
    private Typeface mBree;
    private Button mBookButton;
    final Calendar calendar = Calendar.getInstance();
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private TextView mHeading;
    private Date date;
    private Time time;
    private APIHandler.BookApi bookApi;
    private Bundle argument;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.booking_details_layout, container, false);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        bookApi = restAdapter.create(APIHandler.BookApi.class);
        argument = this.getArguments();
        mName = (TextView) rootView.findViewById(R.id.name);
        mPickUpPoint = (TextView) rootView.findViewById(R.id.pickup_point);
        mDropPoint = (TextView) rootView.findViewById(R.id.drop_point);
        mDatePicker = (TextView) rootView.findViewById(R.id.date);
        mTimePicker = (TextView) rootView.findViewById(R.id.time);
        mRideShare = (RadioButton) rootView.findViewById(R.id.ride_share);
        mBulk = (RadioButton) rootView.findViewById(R.id.bulk);
        mHeading = (TextView) rootView.findViewById(R.id.heading);

        mBree = Typeface.createFromAsset(mActivity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mHeading.setTypeface(mBree, Typeface.BOLD);
        mHeading.setText("Booking Details");

        mBulk.setTypeface(mBree);
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
        mBulk.setOnClickListener(this);

        mPickUpPoint.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mDropPoint.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mName.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mTimePicker.setTextColor(mActivity.getResources().getColor(R.color.Black));
        mDatePicker.setTextColor(mActivity.getResources().getColor(R.color.Black));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mName.setText(prefs.getString(Constants.USER_NAME,""));

        if(!TextUtils.isEmpty(argument.getString(Constants.SOURCE_STRING)))
            mPickUpPoint.setText(argument.getString(Constants.SOURCE_STRING));

        if(!TextUtils.isEmpty(argument.getString(Constants.DEST_STRING)))
            mDropPoint.setText(argument.getString(Constants.DEST_STRING));
        if(argument.getBoolean(Constants.NOW)){
            long time30 = System.currentTimeMillis()+1000*60*30;
            java.util.Date date1 = new java.util.Date(time30);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            mTimePicker.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            mDatePicker.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR));
            date = new Date(calendar.get(Calendar.YEAR)-1900,calendar.get(Calendar.MONTH)-1,calendar.get(Calendar.DAY_OF_MONTH));
            time = new Time(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),00);
        }
        else if(argument.getBoolean(Constants.LATER)){
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
        switch (v.getId()){
            case R.id.date:
                if(!isDetached()||!isRemoving()) {
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
                if(!isDetached()||isRemoving()) {
                    final TimePickerDialog timePickerDialog =
                            TimePickerDialog.newInstance(this,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE), true, false);
                    timePickerDialog.show(getChildFragmentManager(), TIMEPICKER_TAG);
                }
                break;
            case R.id.Book:
                bookApi.makeBooking(
                        mPickUpPoint.getText().toString(),
                        mDropPoint.getText().toString(),
                        date,
                        time,
                        "Auto",
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
                                Log.d("Response",result);
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
    

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        mDatePicker.setText(day+"/"+(month+1)+"/"+year);
        date = new Date(year-1900,month,day);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        mTimePicker.setText(hour+":"+minute);
        time = new Time(hour,minute,00);
    }
}
