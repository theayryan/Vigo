package vigo.com.vigo;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ayushb on 11/7/15.
 */
public class UtilityDialog extends DialogFragment implements View.OnClickListener {

    private static UtilityDialog instance;
    private Typeface mBree;
    private Typeface mComfortaa;
    private Activity mActivity;
    private boolean HAS_TRIP_ID;
    private int tripId;
    private Typeface mButtonFont;

    public static UtilityDialog getInstance(String text) {
        instance = new UtilityDialog();
        Bundle args = new Bundle();
        args.putString(Constants.TEXT, text);
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
            case R.id.confirm:
                dismiss();
                if (fragment != null && fragment instanceof UtilityInterface) {
                    UtilityInterface utilityInterface = (UtilityInterface) fragment;
                    utilityInterface.confirmUtility();
                } else if (mActivity instanceof UtilityInterface) {
                    UtilityInterface utilityInterface = (UtilityInterface) mActivity;
                    utilityInterface.confirmUtility();
                }
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
        View rootView = inflater.inflate(R.layout.utility_dialog, container, false);
        Button confirm = (Button) rootView.findViewById(R.id.confirm);
        Button cancel = (Button) rootView.findViewById(R.id.cancel);
        TextView text = (TextView) rootView.findViewById(R.id.utility_text);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MapFragment fragment = (MapFragment) getTargetFragment();
                    fragment.reset();
                } catch (ClassCastException ex) {
                    Log.e("Exception", "Class Cast Exception");
                }
                dismiss();
            }
        });

        Typeface mCabin = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Cabin-Regular.ttf");
        mButtonFont = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Button_Font.ttf");

        confirm.setTypeface(mButtonFont);
        cancel.setTypeface(mButtonFont);
        text.setTypeface(mCabin);

        Bundle args = getArguments();
        text.setText(args.getString(Constants.TEXT));

        return rootView;
    }

    public interface UtilityInterface {
        public void confirmUtility();
    }

}
