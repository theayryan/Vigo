package vigo.com.vigo;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ayushb on 10/7/15.
 */
public class ShowInvoiceDialog extends DialogFragment implements View.OnClickListener {

    private static ShowInvoiceDialog instance;
    private Typeface mBree;
    private Typeface mComfortaa;
    private Activity mActivity;
    private Typeface mCabin;
    private Typeface mButtonFont;

    public static ShowInvoiceDialog getInstance(String actualFare, String distance, String timeTaken, String driver_name, String driver_contact) {
        instance = new ShowInvoiceDialog();
        Bundle args = new Bundle();
        args.putString(Constants.ACTUAL_FARE, actualFare);
        args.putString(Constants.DISTANCE, distance);
        args.putString(Constants.TIME_TAKEN, timeTaken);
        args.putString(Constants.DRIVER_NAME, driver_name);
        args.putString(Constants.DRIVER_CONTACT, driver_contact);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = super.getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                dismiss();
                break;
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        //request a window without title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.show_invoice_layout, container, false);
        TextView actualFare = (TextView) rootView.findViewById(R.id.actual_fare);
        TextView actualFareValue = (TextView) rootView.findViewById(R.id.actual_fare_value);
        TextView distance = (TextView) rootView.findViewById(R.id.distance_given);
        TextView distanceValue = (TextView) rootView.findViewById(R.id.distance_value);
        TextView timeTaken = (TextView) rootView.findViewById(R.id.estimated_time);
        TextView timeTakenValue = (TextView) rootView.findViewById(R.id.estimated_time_value);
        TextView driverDetails = (TextView) rootView.findViewById(R.id.driver_details);
        TextView driverName = (TextView) rootView.findViewById(R.id.driver_name);
        TextView driverNumber = (TextView) rootView.findViewById(R.id.driver_number);
        TextView notAllocated = (TextView) rootView.findViewById(R.id.not_allocated);
        LinearLayout driverDetailsLayout = (LinearLayout) rootView.findViewById(R.id.driver_detail_layout);
        LinearLayout driverDetail = (LinearLayout) rootView.findViewById(R.id.driver_layout);
        Fragment parent = getTargetFragment();

        if (parent != null) {
            driverDetail.setVisibility(View.GONE);
        } else {
            driverDetail.setVisibility(View.VISIBLE);
        }

        Button confirm = (Button) rootView.findViewById(R.id.confirm);


        confirm.setOnClickListener(this);

        mComfortaa = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Comfortaa-Regular.ttf");
        mCabin = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Cabin-Regular.ttf");
        mButtonFont = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Button_Font.ttf");
        actualFare.setTypeface(mCabin);
        distance.setTypeface(mCabin);
        timeTaken.setTypeface(mCabin);
        confirm.setTypeface(mButtonFont);
        driverDetails.setTypeface(mCabin);

        notAllocated.setTypeface(mComfortaa);
        driverNumber.setTypeface(mComfortaa);
        driverName.setTypeface(mComfortaa);
        actualFareValue.setTypeface(mComfortaa);
        distanceValue.setTypeface(mComfortaa);
        timeTakenValue.setTypeface(mComfortaa);

        Bundle args = getArguments();

        actualFareValue.setText(args.getString(Constants.ACTUAL_FARE));
        distanceValue.setText(args.getString(Constants.DISTANCE) + " Kms");
        timeTakenValue.setText(args.getString(Constants.TIME_TAKEN));
        if (!TextUtils.isEmpty(args.getString(Constants.DRIVER_NAME))) {
            notAllocated.setVisibility(View.GONE);
            driverDetailsLayout.setVisibility(View.VISIBLE);
            driverName.setText(args.getString(Constants.DRIVER_NAME));
            driverNumber.setText(args.getString(Constants.DRIVER_CONTACT));
        } else {
            notAllocated.setVisibility(View.VISIBLE);
            driverDetailsLayout.setVisibility(View.GONE);
        }

        return rootView;
    }

}
