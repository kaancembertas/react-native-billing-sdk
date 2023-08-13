package com.billingsdk;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class BillingManagerEventEmitter {
    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;

    public BillingManagerEventEmitter(ReactContext context){
        eventEmitter = context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }

    public void sendEvent(String eventName, @Nullable WritableMap params){
        eventEmitter.emit(eventName, params);
    }

    public void addListener(String eventName) {}
    public void removeListeners(Integer count) {}
}
