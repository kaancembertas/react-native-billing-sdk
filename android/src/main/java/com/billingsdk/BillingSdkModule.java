package com.billingsdk;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = BillingSdkModule.NAME)
public class BillingSdkModule extends ReactContextBaseJavaModule {
  public static final String NAME = "BillingSdk";
  private BillingSdk billingSdk;
  private BillingSdkEventEmitter eventEmitter;

  public BillingSdkModule(ReactApplicationContext context) {
    super(context);
    this.eventEmitter = new BillingSdkEventEmitter(context);
    this.billingSdk = new BillingSdk(context, this.eventEmitter);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void startConnection (Promise promise){
    billingSdk.startConnection(promise);
  }

  @ReactMethod
  public void endConnection (Promise promise){
    billingSdk.endConnection(promise);
  }

  @ReactMethod
  public void getConnectionState(Promise promise){
    billingSdk.getConnectionState(promise);
  }

  @ReactMethod
  public void queryProductDetails(ReadableArray productDetailParamsList, Promise promise) {
    billingSdk.queryProductDetails(BillingSdkConverter.convertArrayToProductDetailParamsList(productDetailParamsList), promise);
  }

  @ReactMethod
  public void launchBillingFlow(String productId, String offerToken, Promise promise) {
    billingSdk.launchBillingFlow(productId, offerToken, promise);
  }

  @ReactMethod
  public void acknowledgePurchase(String purchaseToken, Promise promise){
    billingSdk.acknowledgePurchase(purchaseToken, promise);
  }

  @ReactMethod
  public void queryPurchaseHistory(String productType, Promise promise){
    billingSdk.queryPurchaseHistory(productType,promise);
  }

  @ReactMethod
  public void queryPurchases(String productType, Promise promise){
    billingSdk.queryPurchases(productType,promise);
  }

  @ReactMethod
  public void addListener(String eventName) {
    this.eventEmitter.addListener(eventName);
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    this.eventEmitter.removeListeners(count);
  }

  @ReactMethod
  public void consume(String purchaseToken, Promise promise){
    billingSdk.consume(purchaseToken, promise);
  }
}
