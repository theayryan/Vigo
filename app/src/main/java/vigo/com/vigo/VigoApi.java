package vigo.com.vigo;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by ayushb on 25/6/15.
 */

public interface VigoApi {
    @FormUrlEncoded
    @POST(Constants.BOOK_URL)
    public void makeBooking(@Field(Constants.SOURCE) String Source,
                            @Field(Constants.DESTINATION) String destination,
                            @Field(Constants.TIME) String time,
                            @Field(Constants.VEHICAL_TYPE) String type,
                            @Field(Constants.CUSTOMER_ID) String id,
                            @Field(Constants.SOURCE_LAT) String s_lat,
                            @Field(Constants.SOURCE_LON) String s_lng,
                            @Field(Constants.DEST_LAT) String d_lat,
                            @Field(Constants.DEST_LON) String d_lng,
                            @Field(Constants.DISTANCE) String distance,
                            @Field(Constants.TIME_TAKEN) String time_taken,
                            retrofit.Callback<retrofit.client.Response> callback);

    @FormUrlEncoded
    @POST(Constants.FUTURE_RIDES_URL)
    public void getFutureRides(
            @Field(Constants.CUSTOMER_ID) String customer_id,
            retrofit.Callback<RidesClass> callback);

    @FormUrlEncoded
    @POST(Constants.PAST_RIDES_URL)
    public void getPastRides(
            @Field(Constants.CUSTOMER_ID) String customer_id,
            retrofit.Callback<RidesClass> callback);

    @FormUrlEncoded
    @POST(Constants.CANCEL_URL)
    public void cancelRide(
            @Field(Constants.TRIP_ID) int trip_id,
            retrofit.Callback<retrofit.client.Response> callback);

    @FormUrlEncoded
    @POST(Constants.RIDE_SHARE_OPTION)
    public void showOptions(
            @Field(Constants.SOURCE) String source,
            @Field(Constants.DESTINATION) String dest,
            @Field(Constants.TIME) String time,
            @Field(Constants.VEHICAL_TYPE) String vehical_type,
            @Field(Constants.CUSTOMER_ID) String cust_id,
            @Field(Constants.SOURCE_LAT) String s_lat,
            @Field(Constants.SOURCE_LON) String s_lng,
            @Field(Constants.DEST_LAT) String d_lat,
            @Field(Constants.DEST_LON) String d_lng,
            @Field(Constants.DISTANCE) String distance,
            @Field(Constants.TIME_TAKEN) String time_taken,
            retrofit.Callback<RidesClass> callback
    );

    @FormUrlEncoded
    @POST(Constants.NEW_RIDE_SHARE)
    public void addRideShare(
            @Field(Constants.SOURCE) String source,
            @Field(Constants.DESTINATION) String dest,
            @Field(Constants.TIME) String time,
            @Field(Constants.VEHICAL_TYPE) String vehical_type,
            @Field(Constants.CUSTOMER_ID) String cust_id,
            @Field(Constants.SOURCE_LAT) String s_lat,
            @Field(Constants.SOURCE_LON) String s_lng,
            @Field(Constants.DEST_LAT) String d_lat,
            @Field(Constants.DEST_LON) String d_lng,
            @Field(Constants.DISTANCE) String distance,
            @Field(Constants.TIME_TAKEN) String time_taken,
            retrofit.Callback<retrofit.client.Response> callback
    );

    @FormUrlEncoded
    @POST(Constants.HITCH_A_RIDE)
    public void addChosenRide(
            @Field(Constants.TRIP_ID) int trip_id,
            @Field(Constants.SOURCE) String source,
            @Field(Constants.DESTINATION) String dest,
            @Field(Constants.TIME) String time,
            @Field(Constants.VEHICAL_TYPE) String vehical_type,
            @Field(Constants.CUSTOMER_ID) String cust_id,
            @Field(Constants.SOURCE_LAT) String s_lat,
            @Field(Constants.SOURCE_LON) String s_lng,
            @Field(Constants.DEST_LAT) String d_lat,
            @Field(Constants.DEST_LON) String d_lng,
            @Field(Constants.DISTANCE) String distance,
            @Field(Constants.TIME_TAKEN) String time_taken,
            retrofit.Callback<retrofit.client.Response> callback
    );

    @FormUrlEncoded
    @POST(Constants.REGISTER)
    public void register(
            @Field(Constants.SHARE_REG_ID) String gcmId,
            @Field(Constants.CUSTOMER_ID) String cust_id,
            @Field("name") String name,
            @Field("contact") String number,
            @Field("email") String email,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST(Constants.BULK_FARE)
    public void bulkFare(
            @Field(Constants.DISTANCE) String distance,
            @Field(Constants.TYPE) String type,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST(Constants.SHARE_FARE)
    public void shareFare(
            @Field(Constants.DISTANCE) String distance,
            @Field(Constants.TYPE) String type,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST(Constants.VERIFY_OTP)
    public void verifyOtp(
            @Field("otp") String otp,
            @Field(Constants.CUSTOMER_ID) String customer_id,
            Callback<Response> callback
    );


    @GET("/distancematrix/json")
    public void getDistance(
            @Query("origins") String origin,
            @Query("destinations") String dest,
            @Query("departure_time") int secs,
            @Query("API") String api,
            Callback<Response> callback
    );

    @POST(Constants.OTP)
    public void generateOtp(
            @Query(Constants.CUSTOMER_ID) String customer_id,
            @Query("contact") String contact,
            Callback<Response> callback
    );

}



