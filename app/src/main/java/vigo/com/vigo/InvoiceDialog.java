package vigo.com.vigo;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ayushb on 7/7/15.
 */
public class InvoiceDialog extends DialogFragment implements View.OnClickListener {

    private static InvoiceDialog instance;
    private Typeface mBree;
    private Typeface mComfortaa;
    private Activity mActivity;

    public static InvoiceDialog getInstance(String actualFare, String totalFare, String discount, String timeTaken) {
        instance = new InvoiceDialog();
        Bundle args = new Bundle();
        args.putString(Constants.ACTUAL_FARE, actualFare);
        args.putString(Constants.TOTAL_FARE, totalFare);
        args.putString(Constants.DISCOUNT, discount);
        args.putString(Constants.TIME_TAKEN, timeTaken);
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
        Fragment fragment = getTargetFragment();
        switch (v.getId()) {
            case R.id.cancel:
                if (fragment != null && fragment instanceof InvoiceInterface) {
                    InvoiceInterface invoiceInterface;
                    invoiceInterface = (InvoiceInterface) fragment;
                    invoiceInterface.cancel();
                }
                break;
            case R.id.confirm:
                if (fragment != null && fragment instanceof InvoiceInterface) {
                    InvoiceInterface invoiceInterface;
                    invoiceInterface = (InvoiceInterface) fragment;
                    invoiceInterface.confirmBooking();
                }
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
        View rootView = inflater.inflate(R.layout.invoice_dialog, container, false);
        TextView actualFare = (TextView) rootView.findViewById(R.id.actual_fare);
        TextView actualFareValue = (TextView) rootView.findViewById(R.id.actual_fare_value);
        TextView totalFare = (TextView) rootView.findViewById(R.id.total_fare);
        TextView totalFareValue = (TextView) rootView.findViewById(R.id.total_value);
        TextView discount = (TextView) rootView.findViewById(R.id.discount_given);
        TextView discountValue = (TextView) rootView.findViewById(R.id.discount_value);
        TextView timeTaken = (TextView) rootView.findViewById(R.id.estimated_time);
        TextView timeTakenValue = (TextView) rootView.findViewById(R.id.estimated_time_value);
        Button confirm = (Button) rootView.findViewById(R.id.confirm);
        Button cancel = (Button) rootView.findViewById(R.id.cancel);

        confirm.setTypeface(mBree);
        cancel.setTypeface(mBree);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        mBree = Typeface.createFromAsset(mActivity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Comfortaa-Regular.ttf");

        actualFare.setTypeface(mBree);
        totalFare.setTypeface(mBree);
        discount.setTypeface(mBree);
        timeTaken.setTypeface(mBree);

        actualFareValue.setTypeface(mComfortaa);
        totalFareValue.setTypeface(mComfortaa);
        discountValue.setTypeface(mComfortaa);
        timeTakenValue.setTypeface(mComfortaa);

        Bundle args = getArguments();

        actualFareValue.setText(args.getString(Constants.ACTUAL_FARE));
        totalFareValue.setText(args.getString(Constants.TOTAL_FARE));
        discountValue.setText(args.getString(Constants.DISCOUNT));
        timeTakenValue.setText(args.getString(Constants.TIME_TAKEN));


        return rootView;
    }

    public interface InvoiceInterface {
        public void confirmBooking();

        public void cancel();
    }
}
