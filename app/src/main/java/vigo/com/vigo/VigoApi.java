package vigo.com.vigo;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by ayushb on 25/6/15.
 */

public interface VigoApi {
    @FormUrlEncoded
    @POST(Constants.BOOK_URL)
    public void makeBooking(@Field("source") String Source,
                            @Field("destination") String destination,
                            @Field("date") String date,
                            @Field("time") String time,
                            @Field("vehicle_type") String type,
                            @Field("customer_id") String id,
                            @Field("source_lat") String s_lat,
                            @Field("source_lng") String s_lng,
                            @Field("destination_lat") String d_lat,
                            @Field("destination_lng") String d_lng,
                            retrofit.Callback<retrofit.client.Response> callback);

    @FormUrlEncoded
    @POST(Constants.FUTURE_RIDES_URL)
    public void getFutureRides(
            @Field("customer_id") String customer_id,
            retrofit.Callback<RidesClass> callback);

    @FormUrlEncoded
    @POST(Constants.PAST_RIDES_URL)
    public void getPastRides(
            @Field("customer_id") String customer_id,
            retrofit.Callback<RidesClass> callback);

    @FormUrlEncoded
    @POST(Constants.CANCEL_URL)
    public void cancelRide(
            @Field("trip_id") int trip_id,
            retrofit.Callback<retrofit.client.Response> callback);

    @FormUrlEncoded
    @POST(Constants.RIDE_SHARE_OPTION)
    public void showOptions(
            @Field("source") String source,
            @Field("destination") String dest,
            @Field("date") String date,
            @Field("time") String time,
            @Field("vehical_type") String vehical_type,
            @Field("customer_id") String cust_id,
            @Field("source_lat") String s_lat,
            @Field("source_lng") String s_lng,
            @Field("destination_lat") String d_lat,
            @Field("destination_lng") String d_lng,
            retrofit.Callback<RidesClass> callback
    );

    @FormUrlEncoded
    @POST(Constants.NEW_RIDE_SHARE)
    public void addRideShare(
            @Field("source") String source,
            @Field("destination") String dest,
            @Field("date") String date,
            @Field("time") String time,
            @Field("vehical_type") String vehical_type,
            @Field("customer_id") String cust_id,
            @Field("source_lat") String s_lat,
            @Field("source_lng") String s_lng,
            @Field("destination_lat") String d_lat,
            @Field("destination_lng") String d_lng,
            retrofit.Callback<retrofit.client.Response> callback
    );

    @FormUrlEncoded
    @POST(Constants.HITCH_A_RIDE)
    public void addChosenRide(
            @Field("trip_id") int trip_id,
            @Field("source") String source,
            @Field("destination") String dest,
            @Field("date") String date,
            @Field("time") String time,
            @Field("vehical_type") String vehical_type,
            @Field("customer_id") String cust_id,
            @Field("source_lat") String s_lat,
            @Field("source_lng") String s_lng,
            @Field("destination_lat") String d_lat,
            @Field("destination_lng") String d_lng,
            retrofit.Callback<retrofit.client.Response> callback
    );


}



