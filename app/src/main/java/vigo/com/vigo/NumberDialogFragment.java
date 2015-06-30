package vigo.com.vigo;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by ayushb on 30/6/15.
 */
public class NumberDialogFragment extends DialogFragment implements View.OnClickListener {
    private static NumberDialogFragment instance;
    private Activity mActivity;
    private EditText getNumber;

    public interface Number{
        public void getNumber(String number);
    }

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
}
