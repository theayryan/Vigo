package vigo.com.vigo;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by ayushb on 30/6/15.
 */
public class NumberDialogFragment extends DialogFragment implements View.OnClickListener {
    private static NumberDialogFragment instance;
    private Activity mActivity;
    private EditText getNumber;
    private Button mOne;
    private Button mTwo;
    private Button mThree;
    private Button mFour;
    private Button mFive;
    private Button mSix;
    private Button mSeven;
    private Button mEight;
    private Button mNine;
    private Button mZero;
    private Typeface mComfortaa;
    private ImageButton mBack;
    private Typeface mButtonFont;

    public static NumberDialogFragment getInstance(){
        instance = new NumberDialogFragment();
        return instance;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.number_dialog, container, false);
        getNumber = (EditText) rootView.findViewById(R.id.get_number_text);
        TextView dialogText = (TextView) rootView.findViewById(R.id.mobile_number_text);
        TextView ok = (TextView) rootView.findViewById(R.id.ok_now);
        ok.setOnClickListener(this);
        TextView mPlus91 = (TextView) rootView.findViewById(R.id.plus_nine_one);
        mOne = (Button) rootView.findViewById(R.id.one);
        mTwo = (Button) rootView.findViewById(R.id.two);
        mThree = (Button) rootView.findViewById(R.id.three);
        mFour = (Button) rootView.findViewById(R.id.four);
        mFive = (Button) rootView.findViewById(R.id.five);
        mSix = (Button) rootView.findViewById(R.id.six);
        mSeven = (Button) rootView.findViewById(R.id.seven);
        mEight = (Button) rootView.findViewById(R.id.eight);
        mNine = (Button) rootView.findViewById(R.id.nine);
        mZero = (Button) rootView.findViewById(R.id.zero);
        mBack = (ImageButton) rootView.findViewById(R.id.back);

        Typeface mCabin = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Cabin-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Comfortaa-Regular.ttf");
        mButtonFont = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Button_Font.ttf");


        dialogText.setTypeface(mCabin);
        getNumber.setTypeface(mCabin);
        ok.setTypeface(mButtonFont);
        mPlus91.setTypeface(mCabin);

        mOne.setOnClickListener(this);
        mTwo.setOnClickListener(this);
        mThree.setOnClickListener(this);
        mFour.setOnClickListener(this);
        mFive.setOnClickListener(this);
        mSix.setOnClickListener(this);
        mSeven.setOnClickListener(this);
        mEight.setOnClickListener(this);
        mNine.setOnClickListener(this);
        mZero.setOnClickListener(this);
        mBack.setOnClickListener(this);

        mOne.setTypeface(mComfortaa);
        mTwo.setTypeface(mComfortaa);
        mThree.setTypeface(mComfortaa);
        mFour.setTypeface(mComfortaa);
        mFive.setTypeface(mComfortaa);
        mSix.setTypeface(mComfortaa);
        mSeven.setTypeface(mComfortaa);
        mEight.setTypeface(mComfortaa);
        mNine.setTypeface(mComfortaa);
        mZero.setTypeface(mComfortaa);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok_now:
                Number numberInterface;
                if(mActivity instanceof Number){
                    numberInterface = (Number) mActivity;
                }
                else
                    break;
                if(getNumber.getText().toString().length()==10) {
                    numberInterface.getNumber(getNumber.getText().toString());
                }
                else{
                    numberInterface.getNumber("");
                }
                break;
            case R.id.one:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("1");
                break;
            case R.id.two:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("2");
                break;
            case R.id.three:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("3");
                break;
            case R.id.four:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("4");
                break;
            case R.id.five:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("5");
                break;
            case R.id.six:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("6");
                break;
            case R.id.seven:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("7");
                break;
            case R.id.eight:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("8");
                break;
            case R.id.nine:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("9");
                break;
            case R.id.zero:
                if (getNumber.getText().toString().length() < 10)
                    getNumber.append("0");
                break;
            case R.id.back:
                getNumber.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = super.getActivity();
    }

    public interface Number {
        public void getNumber(String number);
    }
}
