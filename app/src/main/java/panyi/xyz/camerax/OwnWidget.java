package panyi.xyz.camerax;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * Widget自行实现 LiftcycleOwner接口
 */
public class OwnWidget implements LifecycleOwner , LifecycleObserver {
    private LifecycleRegistry lifeRegistry;

    private MyLocationListener location;
    public OwnWidget(){
        lifeRegistry = new LifecycleRegistry(this);

        location = new MyLocationListener();
        getLifecycle().addObserver(location);
        lifeRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void start(){
        lifeRegistry.setCurrentState(Lifecycle.State.STARTED);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destory(){
        lifeRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifeRegistry;
    }
}//end class
