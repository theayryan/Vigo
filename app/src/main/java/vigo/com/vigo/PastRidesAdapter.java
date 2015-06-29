package vigo.com.vigo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Created by ayushb on 28/6/15.
 */
public class PastRidesAdapter extends ArrayAdapter<Book> {
    private final List<Book> objects;
    private final Activity activity;
    private final Typeface mBree;
    private final Typeface mComfortaa;
    WeakReference<Context> context;
    public PastRidesAdapter(Context context, List<Book> objects, Activity activity) {
        super(context, R.layout.list_item, objects);
        this.context = new WeakReference<Context>(context);
        this.objects = objects;
        this.activity = activity;
        mBree = Typeface.createFromAsset(activity.getAssets(), "fonts/BreeSerif-Regular.ttf");
        mComfortaa = Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.autocomplete_listitem, parent, false);
        }

        TextView source = (TextView) convertView.findViewById(R.id.source);
        TextView destination = (TextView) convertView.findViewById(R.id.destination);
        TextView dateTV = (TextView) convertView.findViewById(R.id.date);
        TextView timeTV = (TextView) convertView.findViewById(R.id.time);
        Date date =objects.get(position).date;
        Time time = objects.get(position).time;
        dateTV.setText(date.getDay()+"/"+date.getMonth()+"/"+date.getYear());
        timeTV.setText(time.getHours()+":"+time.getMinutes());
        source.setText(objects.get(position).source);
        destination.setText(objects.get(position).destination);
        dateTV.setTypeface(mComfortaa);
        timeTV.setTypeface(mComfortaa);
        source.setTypeface(mBree);
        destination.setTypeface(mBree);
        return convertView;
    }
}
