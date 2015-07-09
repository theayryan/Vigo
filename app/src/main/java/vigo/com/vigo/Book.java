package vigo.com.vigo;

import java.io.Serializable;

/**
 * Created by ayushb on 25/6/15.
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 7526471155622776147L;
    int trip_id;
    String source;
    String destination;
    String date;
    String time;
    String vehical_type;
    String customer_id;
    String vehicle_id;
    String source_lat;
    String source_lng;
    String destination_lat;
    String destination_lng;
    String distance;
    String time_taken;
    String type;
}

