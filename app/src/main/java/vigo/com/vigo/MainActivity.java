package vigo.com.vigo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapFragment;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tuenti.smsradar.Sms;
import com.tuenti.smsradar.SmsListener;
import com.tuenti.smsradar.SmsRadar;


public class MainActivity extends FragmentActivity implements DrawerLayout.DrawerListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerElementsContainer = findViewById(R.id.drawer_root_layout);
        mUserImage = (ImageView) findViewById(R.id.user_image);
        mUserName = (TextView) findViewById(R.id.user_name);
        mBree = Typeface.createFromAsset(getAssets(), "fonts/BreeSerif-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
       /* SmsRadar.initializeSmsRadarService(this, new SmsListener() {
            @Override
            public void onSmsSent(Sms sms) {

            }

            @Override
            public void onSmsReceived(Sms sms) {

            }
        });*/
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
}
