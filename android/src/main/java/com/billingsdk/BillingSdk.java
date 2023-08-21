package com.billingsdk;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.facebook.common.internal.ImmutableList;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BillingSdk {
    private BillingClient billingClient;
    private ArrayList<ProductDetails> productDetailsList;
    private ReactApplicationContext context;
    private BillingSdkEventEmitter eventEmitter;

    public BillingSdk(ReactApplicationContext context, BillingSdkEventEmitter eventEmitter) {
        this.context = context;
        this.eventEmitter = eventEmitter;
        this.productDetailsList = new ArrayList<>();
        this.billingClient = BillingClient.newBuilder(context)
                .setListener(this.purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
            WritableMap json = new WritableNativeMap();

            int responseCode = billingResult.getResponseCode();
            json.putString("responseCode", String.valueOf(responseCode));
            json.putString("debugMessage", billingResult.getDebugMessage());

            if(responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
                json.putArray("purchases", BillingSdkConverter.convertPurchaseListToArray(purchases));
            else
                json.putNull("purchases");

            eventEmitter.sendEvent(BillingSdkConstants.PURCHASE_UPDATED, json);
        }
    };

    public void acknowledgePurchase(String purchaseToken, Promise promise){
        AcknowledgePurchaseParams.Builder builder = AcknowledgePurchaseParams.newBuilder();
        builder.setPurchaseToken(purchaseToken);
        AcknowledgePurchaseParams acknowledgePurchaseParams = builder.build();
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    promise.resolve(null);
                    return;
                }
                promise.reject(String.valueOf(billingResult.getResponseCode()), billingResult.getDebugMessage());
            }
        });
    }

    public void startConnection (Promise promise){
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    promise.resolve(null);
                    return;
                }

                promise.reject(String.valueOf(billingResult.getResponseCode()), billingResult.getDebugMessage());
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                eventEmitter.sendEvent(BillingSdkConstants.BILLING_SERVICE_DISCONNECTED, null);
            }
        });
    }

    public void endConnection (Promise promise){
        billingClient.endConnection();
        promise.resolve(null);
    }

    public void getConnectionState(Promise promise){
        promise.resolve(billingClient.getConnectionState());
    }

    public void queryProductDetails (ArrayList<String> productIds, String productType, Promise promise) {
        List<QueryProductDetailsParams.Product> params = new ArrayList<>();

        for (String productId: productIds) {
            params.add( QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .build());
        }

        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(params)
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener()  {
                    public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> result) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                            productDetailsList.addAll(result);
                            promise.resolve(BillingSdkConverter.convertProductDetailsListToArray(result));
                            return;
                        }

                        promise.reject(String.valueOf(billingResult.getResponseCode()), billingResult.getDebugMessage());
                    }
                }
        );
    }

    private ProductDetails findProductDetailById (String productId){
        for(ProductDetails productDetail : productDetailsList){
            if(productDetail.getProductId().equals(productId)){
                return productDetail;
            }
        }

        return null;
    }

    public void launchBillingFlow (String productId, @Nullable String offerToken, Promise promise){
            Activity activity = context.getCurrentActivity();

        if(activity == null){
            promise.reject(BillingSdkConstants.E_ACTIVITY_NULL, "getCurrentActivity returned null.");
            return;
        }

        BillingFlowParams.ProductDetailsParams.Builder builder = BillingFlowParams.ProductDetailsParams.newBuilder();

        ProductDetails productDetail = findProductDetailById(productId);
        if(productDetail == null){
            promise.reject(BillingSdkConstants.E_PRODUCT_NOT_QUERIED, "The in app product or subscription must be queried before calling launchBillingFlow.");
            return;
        }

        builder.setProductDetails(productDetail);

        if(offerToken != null){
          builder.setOfferToken(offerToken);
        }

        ImmutableList productDetailsParamsList = ImmutableList.of(builder.build());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        // Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        promise.resolve(null);
    }

    public void queryPurchaseHistory(String productType, Promise promise){
        QueryPurchaseHistoryParams params = QueryPurchaseHistoryParams
                .newBuilder()
                .setProductType(productType)
                .build();

        billingClient.queryPurchaseHistoryAsync(params, new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult, @Nullable List<PurchaseHistoryRecord> purchaseHistoryList) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    if(purchaseHistoryList == null){
                        promise.resolve(null);
                        return;
                    }
                    promise.resolve(BillingSdkConverter.convertPurchaseHistoryRecordListToArray(purchaseHistoryList));
                    return;
                }
                promise.reject(String.valueOf(billingResult.getResponseCode()), billingResult.getDebugMessage());
            }
        });
    }

    public void queryPurchases(String productType, Promise promise){
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder().setProductType(productType).build();

        billingClient.queryPurchasesAsync(params, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchaseList) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    promise.resolve(BillingSdkConverter.convertPurchaseListToArray(purchaseList));
                    return;
                }

                promise.reject(String.valueOf(billingResult.getResponseCode()), billingResult.getDebugMessage());
            }
        });

    }

    public void consume(String purchaseToken, Promise promise){
        ConsumeParams params = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build();
        billingClient.consumeAsync(params, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
                int responseCode = billingResult.getResponseCode();
                if(responseCode == BillingClient.BillingResponseCode.OK){
                    promise.resolve(null);
                    return;
                }
                promise.reject(String.valueOf(responseCode), billingResult.getDebugMessage());
            }
        });
    }
}
