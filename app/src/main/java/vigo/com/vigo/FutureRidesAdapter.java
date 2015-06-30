package vigo.com.vigo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ayushb on 28/6/15.
 */
public class FutureRidesAdapter extends ArrayAdapter<Book> implements View.OnClickListener {

    private List<Book> objects;
    private final Activity activity;
    private final Typeface mBree;
    private final Typeface mComfortaa;
    WeakReference<Context> context;
    private VigoApi cancelApi;

    public FutureRidesAdapter(Context context, List<Book> objects, Activity activity) {
        super(context, R.layout.list_item, objects);
        this.context = new WeakReference<Context>(context);
        this.objects = objects;
        this.activity = activity;
        mBree = Typeface.createFromAsset(activity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf");
    }

    public void updateList(List<Book> objects){
        this.objects = objects;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView source = (TextView) convertView.findViewById(R.id.source);
        TextView destination = (TextView) convertView.findViewById(R.id.destination);
        TextView dateTV = (TextView) convertView.findViewById(R.id.date);
        TextView timeTV = (TextView) convertView.findViewById(R.id.time);
        LinearLayout extras = (LinearLayout) convertView.findViewById(R.id.future_rides_extra);
        extras.setVisibility(View.VISIBLE);
        ImageButton invoice = (ImageButton) convertView.findViewById(R.id.invoice);
        ImageButton cancel = (ImageButton) convertView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(Constants.BASE_URL)
                        .build();
                cancelApi = restAdapter.create(VigoApi.class);
                cancelApi.cancelRide(objects.get(position).trip_id, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        try {

                            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                            String line;

                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        String result = sb.toString();
                        Log.d("Response", result);
                        if(result.contains("TRIP_CANCEL_UNSUCCESSFUL")){
                            Toast.makeText(context.get(),"Some Error Occurred. Please Try Again.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context.get(), "Trip Cancelled", Toast.LENGTH_SHORT).show();
                            objects.remove(position);
                            updateList(objects);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        Toast.makeText(context.get(),"Some Error Occurred. Please Try Again.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        invoice.setOnClickListener(this);
        String date =objects.get(position).date;
        String time = objects.get(position).time;
        dateTV.setText(date);
        timeTV.setText(time);
        source.setText(objects.get(position).source);
        destination.setText(objects.get(position).destination);
        dateTV.setTypeface(mComfortaa);
        timeTV.setTypeface(mComfortaa);
        source.setTypeface(mBree);
        destination.setTypeface(mBree);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.invoice:
                //show invoice dialog
                break;
        }
    }
}
