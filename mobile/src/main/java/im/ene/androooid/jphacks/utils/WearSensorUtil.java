package im.ene.androooid.jphacks.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import im.ene.androooid.jphacks.callback.WearSensorCallback;

/**
 * Created by Takahiko on 2014/12/14.
 */
public class WearSensorUtil implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {
    public static final String SENSOR_TYPE_HEART_RATE = "heartRate";
    public static final String SENSOR_TYPE_STEPS = "steps";

    private GoogleApiClient mWearApiClient;
    private WearSensorCallback mCallback;

    public WearSensorUtil(Context context) {
        mWearApiClient = new GoogleApiClient.Builder(context)
                .addApi( Wearable.API )
                .build();

        mWearApiClient.connect();
    }

    public void resume() {
        if( mWearApiClient != null && !( mWearApiClient.isConnected() || mWearApiClient.isConnecting() ) )
            mWearApiClient.connect();
    }

    public void stop() {
        if ( mWearApiClient != null ) {
            Wearable.MessageApi.removeListener( mWearApiClient, this );
            if ( mWearApiClient.isConnected() ) {
                mWearApiClient.disconnect();
            }
        }
    }

    public void destroy() {
        if( mWearApiClient != null )
            mWearApiClient.unregisterConnectionCallbacks( this );
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());

        String arr[] = message.split(":");
        String type = arr[0];
        if (SENSOR_TYPE_HEART_RATE.equals(type)) {
            float heartRate = Float.parseFloat(arr[1]);
            mCallback.onHeartRateChanged(heartRate);
        } else if(SENSOR_TYPE_STEPS.equals(type)) {
            int steps = Integer.parseInt(arr[1]);
            mCallback.onStepDetected(steps);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener( mWearApiClient, this );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void setCallback(WearSensorCallback callback) {
        mCallback = callback;
    }

    public void removeCallback() {
        mCallback = null;
    }
}
