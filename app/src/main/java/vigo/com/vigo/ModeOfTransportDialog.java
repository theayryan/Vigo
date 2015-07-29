package vigo.com.vigo;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ayushb on 29/7/15.
 */
public class ModeOfTransportDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {
    private static ModeOfTransportDialog instance;
    private Activity mActivity;

    public static ModeOfTransportDialog getInstance() {
        instance = new ModeOfTransportDialog();
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = super.getActivity();
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
        View rootView = inflater.inflate(R.layout.ride_mode_chooser, container, false);
        LinearLayout goStretch = (LinearLayout) rootView.findViewById(R.id.go_stretch_layout);
        LinearLayout goCompact = (LinearLayout) rootView.findViewById(R.id.go_compact_layout);
        LinearLayout goSupreme = (LinearLayout) rootView.findViewById(R.id.go_supreme_layout);
        LinearLayout goAuto = (LinearLayout) rootView.findViewById(R.id.go_auto_layout);

        TextView goStretchTv = (TextView) rootView.findViewById(R.id.go_stretch_textview);
        TextView goCompactTv = (TextView) rootView.findViewById(R.id.go_compact_textview);
        TextView goSupremeTv = (TextView) rootView.findViewById(R.id.go_supreme_textview);
        TextView goAutoTv = (TextView) rootView.findViewById(R.id.go_auto_textview);

        Typeface mCabin = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Cabin-Regular.ttf");

        goAutoTv.setTypeface(mCabin);
        goStretchTv.setTypeface(mCabin);
        goSupremeTv.setTypeface(mCabin);
        goCompactTv.setTypeface(mCabin);

        goAuto.setOnClickListener(this);
        goCompact.setOnClickListener(this);
        goStretch.setOnClickListener(this);
        goSupreme.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment parent = getTargetFragment();
        ChooseVehicleMode chooseVehicleMode = null;
        if (parent != null && parent instanceof ChooseVehicleMode)
            chooseVehicleMode = (ChooseVehicleMode) parent;
        switch (v.getId()) {
            case R.id.go_stretch_layout:
                chooseVehicleMode.chooseVehicleMode(Constants.GO_STRETCH);
                break;
            case R.id.go_compact_layout:
                chooseVehicleMode.chooseVehicleMode(Constants.GO_COMPACT);
                break;
            case R.id.go_auto_layout:
                chooseVehicleMode.chooseVehicleMode(Constants.GO_AUTO);
                break;
            case R.id.go_supreme_layout:
                chooseVehicleMode.chooseVehicleMode(Constants.GO_SUPREME);
                break;
        }
        dismiss();
    }

    public interface ChooseVehicleMode {
        public void chooseVehicleMode(String mode);
    }
}
