package vigo.com.vigo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 29/6/15.
 */
public class FutureRidesActivity extends ActionBarActivity {
    ListView mMyRides;
    private VigoApi ridesApi;
    private FutureRidesAdapter mFutureRidesAdapter;
    private TextView mNoRides;
    private SharedPreferences pref;
    private ProgressDialog mPDialog;
    private Typeface mCabin;

    public void showProgressDialog() {
        if (mPDialog == null) {
            mPDialog = new ProgressDialog(this);
            mPDialog.setMessage("Fetching");
            mPDialog.show();
        } else if (!mPDialog.isShowing()) {
            mPDialog.setMessage("Fetching");
            mPDialog.show();
        }
        mPDialog.setCancelable(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rides_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appMain)));
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        mMyRides = (ListView) findViewById(R.id.rides_listView);
        mNoRides = (TextView) findViewById(R.id.no_rides);
        mNoRides.setText("No Future Rides Booked");
        mCabin = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf");
        mNoRides.setTypeface(mCabin);
        mMyRides.setDividerHeight(0);
        showProgressDialog();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        ridesApi = restAdapter.create(VigoApi.class);
        ridesApi.getFutureRides(pref.getString(Constants.AUTH_TOKEN, ""), new Callback<RidesClass>() {
            @Override
            public void success(RidesClass rides, Response response) {
                if(rides.ride!=null&&rides.ride.size()>0) {
                    mFutureRidesAdapter = new FutureRidesAdapter(FutureRidesActivity.this, rides.ride, FutureRidesActivity.this);
                    mMyRides.setAdapter(mFutureRidesAdapter);
                }
                else{
                    mMyRides.setVisibility(View.GONE);
                    mNoRides.setVisibility(View.VISIBLE);
                }
                if (mPDialog.isShowing())
                    mPDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                if (mPDialog.isShowing())
                    mPDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
