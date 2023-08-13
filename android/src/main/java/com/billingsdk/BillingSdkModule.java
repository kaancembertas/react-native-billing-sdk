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
  private BillingManager billingManager;
  private BillingManagerEventEmitter eventEmitter;

  public BillingSdkModule(ReactApplicationContext context) {
    super(context);
    this.eventEmitter = new BillingManagerEventEmitter(context);
    this.billingManager = new BillingManager(context, this.eventEmitter);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void startConnection (Promise promise){
    billingManager.startConnection(promise);
  }

  @ReactMethod
  public void endConnection (Promise promise){
    billingManager.endConnection(promise);
  }

  @ReactMethod
  public void getConnectionState(Promise promise){
    billingManager.getConnectionState(promise);
  }

  @ReactMethod
  public void queryProductDetails(ReadableArray productDetailParamsList, Promise promise) {
    billingManager.queryProductDetails(BillingManagerConverter.convertArrayToProductDetailParamsList(productDetailParamsList), promise);
  }

  @ReactMethod
  public void launchBillingFlow(String productId, String offerToken, Promise promise) {
    billingManager.launchBillingFlow(productId, offerToken, promise);
  }

  @ReactMethod
  public void acknowledgePurchase(String purchaseToken, Promise promise){
    billingManager.acknowledgePurchase(purchaseToken, promise);
  }

  @ReactMethod
  public void queryPurchaseHistory(String productType, Promise promise){
    billingManager.queryPurchaseHistory(productType,promise);
  }

  @ReactMethod
  public void queryPurchases(String productType, Promise promise){
    billingManager.queryPurchases(productType,promise);
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
    billingManager.consume(purchaseToken, promise);
  }
}
