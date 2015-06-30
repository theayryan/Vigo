package vigo.com.vigo;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
 * Created by ayushb on 30/6/15.
 */
public class PastRidesActivity extends ActionBarActivity {
    ListView mMyRides;
    private VigoApi ridesApi;
    private PastRidesAdapter mPastRidesAdapter;
    private TextView mNoRides;
    private Typeface mBree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rides_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appMain)));
        mMyRides = (ListView) findViewById(R.id.rides_listView);
        mMyRides.setDividerHeight(0);
        mNoRides = (TextView) findViewById(R.id.no_rides);
        mNoRides.setText("No Future Rides Booked");
        mBree = Typeface.createFromAsset(getAssets(), "fonts/BreeSerif-Regular.ttf");
        mNoRides.setTypeface(mBree);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        ridesApi = restAdapter.create(VigoApi.class);
        ridesApi.getPastRides("46151218", new Callback<RidesClass>() {
            @Override
            public void success(RidesClass rides, Response response) {
                if(rides.ride!=null&&rides.ride.size()>0) {
                    mPastRidesAdapter = new PastRidesAdapter(PastRidesActivity.this, rides.ride, PastRidesActivity.this);
                    mMyRides.setAdapter(mPastRidesAdapter);
                }
                else{
                    mMyRides.setVisibility(View.GONE);
                    mNoRides.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
