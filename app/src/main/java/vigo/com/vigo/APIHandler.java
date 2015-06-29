package vigo.com.vigo;

import com.squareup.okhttp.Response;

import java.sql.Date;
import java.sql.Time;

import javax.security.auth.callback.Callback;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by ayushb on 25/6/15.
 */
public class APIHandler {

    public interface BookApi {
        @FormUrlEncoded
        @POST(Constants.BOOK_URL)
        public void makeBooking(@Field("source") String Source,
                                @Field("destination") String destination,
                                @Field("date") Date date,
                                @Field("time") Time time,
                                @Field("vehicle_type") String type,
                                @Field("customer_id") String id,
                                @Field("source_lat") String s_lat,
                                @Field("source_lng") String s_lng,
                                @Field("destination_lat") String d_lat,
                                @Field("destination_lng") String d_lng,
                                retrofit.Callback<retrofit.client.Response> callback);
    }



}
