package panyi.xyz.camerax;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class MyLocationListener implements LifecycleObserver {
    private static final String TAG = "MyLocationListener";

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(){
        Log.i(TAG , "My location Listener on start");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestory(){
        Log.i(TAG , "My location Listener on ON_DESTROY");
    }
}
