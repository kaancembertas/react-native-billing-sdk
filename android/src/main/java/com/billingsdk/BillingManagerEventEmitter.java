package com.billingsdk;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class BillingManagerEventEmitter {
    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = null;
    private ReactContext context;

    private DeviceEventManagerModule.RCTDeviceEventEmitter getEventEmitter (){
      if(this.eventEmitter == null)
        this.eventEmitter = context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

      return this.eventEmitter;
    }

    public BillingManagerEventEmitter(ReactContext context){
      this.context = context;
    }

    public void sendEvent(String eventName, @Nullable WritableMap params){
        this.getEventEmitter().emit(eventName, params);
    }

    public void addListener(String eventName) {}
    public void removeListeners(Integer count) {}
}
