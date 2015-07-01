package vigo.com.vigo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by ayushb on 19/6/15.
 */
public class MapFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static boolean SOURCE_CHOSEN = false;
    private static boolean DESTINATION_CHOSEN = false;
    private MapView mMap;
    private GoogleMap googleMap;
    private TextView mLaterButton;
    private TextView mNowButton;
    private Typeface mBree;
    private Button mConfirmButton;
    private FragmentActivity mActivity;
    private ImageView mSearchImage;
    private AutoCompleteTextView mSearchBox;
    private GoogleApiClient mGoogleApiClient;
    private ImageView mMarkerImage ;
    private LatLng sourceLatlng;
    private LatLng destLatlng;
    private Bundle argumentsBooking;
    private ImageButton mClear;
    private PlacesArrayAdapter mPlaceArrayAdapter;
    private LatLng searchLatlng;
    private CharSequence sourceString;
    private CharSequence destString;
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("Error", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            if (place.getLatLng() != null) {
                searchLatlng = place.getLatLng();
            } else {
                onResult(places);
            }
            if(SOURCE_CHOSEN==false){
                argumentsBooking.putString(Constants.SOURCE_STRING,place.getName().toString());
            }
            else if(DESTINATION_CHOSEN==false){
                destString = place.getAddress();
                argumentsBooking.putString(Constants.DEST_STRING,place.getName().toString());
            }
        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("Selected", "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i("Fetching", "Fetching details for ID: " + item.placeId);
        }
    };

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_layout, container, false);
        MapsInitializer.initialize(mActivity);
        argumentsBooking = new Bundle();
        mMap = (MapView) rootView.findViewById(R.id.map_view);
        mLaterButton = (TextView) rootView.findViewById(R.id.later_button);
        mNowButton = (TextView) rootView.findViewById(R.id.now_button);
        mConfirmButton = (Button) rootView.findViewById(R.id.confirm_button);
        mSearchBox = (AutoCompleteTextView) rootView.findViewById(R.id.search_box);
        mSearchImage = (ImageView) rootView.findViewById(R.id.search_image);
        mMarkerImage = (ImageView) rootView.findViewById(R.id.marker_image);
        mClear = (ImageButton) rootView.findViewById(R.id.clear);
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mSearchBox.getText().toString()))
                    mSearchBox.setText("");
            }
        });
        mMarkerImage.setVisibility(View.INVISIBLE);
        mSearchImage.setOnClickListener(this);
        mNowButton.setOnClickListener(this);
        mLaterButton.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
        mBree = Typeface.createFromAsset(mActivity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mConfirmButton.setTypeface(mBree);
        mLaterButton.setTypeface(mBree);
        mNowButton.setTypeface(mBree);
        mMap.onCreate(savedInstanceState);
        googleMap = mMap.getMap();
        mGoogleApiClient = new GoogleApiClient
                .Builder(mActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleMap.setMyLocationEnabled(true);
        mSearchBox.setOnItemClickListener(mAutocompleteClickListener);
        mSearchBox.setThreshold(3);
        LatLngBounds latLngBounds = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 0));

        mPlaceArrayAdapter = new PlacesArrayAdapter(mActivity, android.R.layout.simple_list_item_1,
                latLngBounds, null);

        mSearchBox.setAdapter(mPlaceArrayAdapter);
        mSearchBox.setTypeface(mBree);
        mSearchBox.setHint("Choose Pick Up Point");
        SOURCE_CHOSEN = false;
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        final ProgressDialog mDialog = new ProgressDialog(mActivity);
                        mDialog.setMessage("Fetching");
                        if (mDialog != null)
                            mDialog.show();
                        if (googleMap.getMyLocation() != null) {
                            searchLatlng = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
                            final CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()))
                                    .tilt(70).zoom(18f).build();

                            Runnable getCurrentLocation = new Runnable() {
                                @Override
                                public void run() {
                                    Geocoder geocoder = new Geocoder(mActivity, Locale.ENGLISH);
                                    try {
                                        List<Address> addressList = geocoder.getFromLocation(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude(), 1);
                                        final StringBuilder string = new StringBuilder("");
                                        for (int i = 0; i < addressList.get(0).getMaxAddressLineIndex(); i++) {
                                            string.append(addressList.get(0).getAddressLine(i));
                                        }
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mSearchBox.setText(string.toString() + " ,");
                                                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                if (mDialog.isShowing()) {
                                                    mDialog.dismiss();
                                                }
                                                mSearchImage.performClick();
                                            }
                                        });

                                        if (SOURCE_CHOSEN == false) {
                                            argumentsBooking.putString(Constants.SOURCE_STRING, addressList.get(0).getSubLocality());
                                        } else if (DESTINATION_CHOSEN == false) {

                                            argumentsBooking.putString(Constants.DEST_STRING, addressList.get(0).getSubLocality());
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            getCurrentLocation.run();

                            if (mMarkerImage.getVisibility() == View.INVISIBLE)
                                mMarkerImage.setVisibility(View.VISIBLE);
                        }
                    }
                };
                runnable.run();

                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = super.getActivity();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.now_button:

                if(SOURCE_CHOSEN==true) {
                    if (DESTINATION_CHOSEN == true) {
                        argumentsBooking.putBoolean(Constants.NOW,true);
                        BookingFragment bookingFragment = new BookingFragment();
                        bookingFragment.setArguments(argumentsBooking);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.maps_fragment, bookingFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Toast.makeText(mActivity, "Please choose your destination", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(mActivity, "Please choose your pick up point", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.later_button:
                if(SOURCE_CHOSEN==true) {
                    if (DESTINATION_CHOSEN == true) {
                        argumentsBooking.putBoolean(Constants.LATER,true);
                        BookingFragment bookingFragment = new BookingFragment();
                        bookingFragment.setArguments(argumentsBooking);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.maps_fragment, bookingFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Toast.makeText(mActivity, "Please choose your destination", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(mActivity, "Please choose your pick up point", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.search_image:
                hideSoftKeyboard(mActivity);

                if(searchLatlng!=null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(searchLatlng).tilt(70).zoom(18f).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMarkerImage.setVisibility(View.VISIBLE);
                    }
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {

                        searchLatlng = cameraPosition.target;

                    }

                });
                break;
            case R.id.confirm_button:


                if(SOURCE_CHOSEN==false){
                    SOURCE_CHOSEN=true;
                    argumentsBooking.putDouble(Constants.SOURCE_LAT,searchLatlng.latitude);
                    argumentsBooking.putDouble(Constants.SOURCE_LON,searchLatlng.longitude);
                    mSearchBox.setHint("Chose Destination");

                }
                else if(DESTINATION_CHOSEN==false){
                    DESTINATION_CHOSEN=true;
                    mConfirmButton.setVisibility(View.GONE);
                    Toast.makeText(mActivity, "Please choose Now or Later according to preference", Toast.LENGTH_SHORT).show();
                    mSearchBox.setHint("Please choose Now or Later according to preference");
                    //proceed to booking activity
                    argumentsBooking.putDouble(Constants.DEST_LAT,searchLatlng.latitude);
                    argumentsBooking.putDouble(Constants.DEST_LON,searchLatlng.longitude);

                }
                mSearchBox.setText("");

                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
