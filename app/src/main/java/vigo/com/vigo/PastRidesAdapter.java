package vigo.com.vigo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ayushb on 28/6/15.
 */
public class PastRidesAdapter extends ArrayAdapter<Book> {
    private final List<Book> objects;
    private final WeakReference<FragmentActivity> activity;
    private final Typeface mComfortaa;
    private final Typeface mCabin;
    WeakReference<Context> context;

    public PastRidesAdapter(Context context, List<Book> objects, FragmentActivity activity) {
        super(context, R.layout.list_item, objects);
        this.context = new WeakReference<Context>(context);
        this.objects = objects;
        this.activity = new WeakReference<FragmentActivity>(activity);
        mCabin = Typeface.createFromAsset(activity.getAssets(), "fonts/Cabin-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = activity.get().getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView source = (TextView) convertView.findViewById(R.id.source);
        TextView destination = (TextView) convertView.findViewById(R.id.destination);
        TextView dateTV = (TextView) convertView.findViewById(R.id.date);
        TextView timeTV = (TextView) convertView.findViewById(R.id.time);
        LinearLayout extras = (LinearLayout) convertView.findViewById(R.id.future_rides_extra);
        extras.setVisibility(View.VISIBLE);
        ImageButton invoice = (ImageButton) convertView.findViewById(R.id.invoice);
        LinearLayout cancel = (LinearLayout) convertView.findViewById(R.id.cancel_layout);
        cancel.setVisibility(View.GONE);
        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInvoiceDialog dialog = ShowInvoiceDialog.getInstance(
                        Integer.toString(objects.get(position).fare),
                        objects.get(position).distance,
                        objects.get(position).time_taken,
                        objects.get(position).driver_name,
                        objects.get(position).driver_contact
                );
                dialog.setCancelable(true);
                dialog.show(activity.get().getSupportFragmentManager(), "ShowInvoiceDialog");
            }
        });
        String time = objects.get(position).time;
        Calendar mydate = Calendar.getInstance();
        mydate.setTimeInMillis(Long.parseLong(time) * 1000);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        dateTV.setText(timeFormat.format(mydate));
        timeTV.setText(mydate.get(Calendar.HOUR_OF_DAY) + ":" + mydate.get(Calendar.MINUTE));
        source.setText(objects.get(position).source);
        destination.setText(objects.get(position).destination);
        dateTV.setTypeface(mComfortaa);
        timeTV.setTypeface(mComfortaa);
        source.setTypeface(mCabin);
        destination.setTypeface(mCabin);
        return convertView;
    }
}
