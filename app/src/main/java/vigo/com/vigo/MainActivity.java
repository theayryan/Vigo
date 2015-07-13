package vigo.com.vigo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends FragmentActivity implements DrawerLayout.DrawerListener, UtilityDialog.UtilityInterface {
    private RelativeLayout mMainContainer;
    private Fragment mFragment;
    private DrawerLayout mDrawerLayout;
    private View mDrawerElementsContainer;
    private ImageView mUserImage;
    private TextView mUserName;
    private Typeface mBree;
    private Typeface mComfortaa;
    private LinearLayout mFutureRides;
    private LinearLayout mPastRides;
    private int tripId;
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mixpanel =
                MixpanelAPI.getInstance(this, Constants.MIXPANEL_NUMBER);
        JSONObject props = new JSONObject();
        try {
            props.put(Constants.CUSTOMER_ID, preferences.getString(Constants.AUTH_TOKEN, ""));
            mixpanel.track("Main Activity", props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Main Activity", props);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerElementsContainer = findViewById(R.id.drawer_root_layout);
        mUserImage = (ImageView) findViewById(R.id.user_image);
        mUserName = (TextView) findViewById(R.id.user_name);
        mBree = Typeface.createFromAsset(getAssets(), "fonts/BreeSerif-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        String utilityText = getIntent().getStringExtra(Constants.TEXT);
        tripId = getIntent().getIntExtra(Constants.TRIP_ID, 0);
        if (!TextUtils.isEmpty(utilityText)) {
            UtilityDialog dialog = UtilityDialog.getInstance(utilityText);
            dialog.setCancelable(true);
            dialog.show(getSupportFragmentManager(), "UtilityDialog");
        }

        boolean is_logged_in = preferences.getBoolean(Constants.IS_LOGGED_IN,false);
        if(is_logged_in==false){
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            finish();
        }
        else{
            mUserName.setText(preferences.getString(Constants.USER_NAME,""));
            mUserName.setTypeface(mBree);
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(getResources().getColor(R.color.appMainLight))
                    .borderWidthDp(3)
                    .cornerRadiusDp(45)
                    .oval(false)
                    .build();

            Picasso.with(getApplicationContext())
                    .load(preferences.getString(Constants.USER_PIC,""))
                    .fit()
                    .transform(transformation)
                    .into(mUserImage);
        }
        mFutureRides = (LinearLayout) findViewById(R.id.future_rides);
        mPastRides = (LinearLayout) findViewById(R.id.past_rides);
        mFutureRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FutureRidesActivity.class);
                startActivity(intent);
            }
        });
        mPastRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PastRidesActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void confirmUtility() {
        if (tripId > 0) {
            //call confirmation
        }
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

}
