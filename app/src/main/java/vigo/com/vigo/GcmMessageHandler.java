package vigo.com.vigo;

/**
 * Created by ayushb on 29/6/15.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandler extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String source = data.getString("source");
        String destination = data.getString("destination");
        String date = data.getString("date");
        String time = data.getString("time");
        int tripId = data.getInt("trip_id");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        if (!TextUtils.isEmpty(message)) {
            if (message.equalsIgnoreCase(Constants.DRIVER_REACHED)) {
                String notify = "The driver for your trip from " + source + " to " + destination + " on " + date + " at " + time + " is waiting for you.";
                sendNotification(getString(R.string.DRIVER_REACHED), "Message from Vigo", notify, 0);
            } else if (message.equalsIgnoreCase(Constants.TRIP_BEGAN_VERIFY)) {
                String notify = "Please verify that your trip from " + source + " to " + destination + " on " + date + " at " + time + " has started";
                sendNotification(getString(R.string.TRIP_BEGAN_VERIFY), "Message from Vigo", notify, tripId);
            } else if (message.equalsIgnoreCase(Constants.CONTRACTOR_CANCELLED)) {
                String notify = "Your trip from " + source + " to " + destination + " on " + date + " at " + time + " has been cancelled by the contractor." +
                        " We apologize for the convenience caused.";
                sendNotification("Trip Cancelled : Expand to view details", "Message from Vigo", notify, 0);
            }
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title, String longMessage, int tripId) {
        Log.d("Notification Server","Received");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.TEXT, longMessage);
        if (tripId > 0) {
            intent.putExtra(Constants.TRIP_ID, tripId);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo);
        if (!TextUtils.isEmpty(longMessage)) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(longMessage));
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}