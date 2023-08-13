package com.billingsdk;

import com.android.billingclient.api.AccountIdentifiers;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.ArrayList;
import java.util.List;

public class BillingManagerConverter {
    public static List<ProductDetailParams> convertArrayToProductDetailParamsList(ReadableArray jsonArray){
        List<ProductDetailParams> productDetailParamsList = new ArrayList<>();

        for(int i = 0; i < jsonArray.size(); i++){
            ReadableMap map = jsonArray.getMap(i);
            ProductDetailParams productDetailParams = new ProductDetailParams(map.getString("productId"), map.getString("productType"));
            productDetailParamsList.add(productDetailParams);
        }

        return productDetailParamsList;
    }

    private static ReadableArray stringListToArray (List<String> stringList){
        WritableArray array = new WritableNativeArray();
        for(String str: stringList) array.pushString(str);
        return array;
    }

    private static ReadableArray convertPricingPhaseListToArray (List<ProductDetails.PricingPhase> pricingPhaseList){
        WritableArray array = new WritableNativeArray();

        for(ProductDetails.PricingPhase pricingPhase: pricingPhaseList){
            WritableMap json = new WritableNativeMap();

            json.putString("billingPeriod", pricingPhase.getBillingPeriod());
            json.putString("formattedPrice", pricingPhase.getFormattedPrice());
            json.putString("priceAmountMicros", String.valueOf(pricingPhase.getPriceAmountMicros()));
            json.putString("priceCurrencyCode", pricingPhase.getPriceCurrencyCode());
            json.putInt("recurrenceMode", pricingPhase.getRecurrenceMode());
            json.putInt("billingCycleCount", pricingPhase.getBillingCycleCount());
            array.pushMap(json);
        }

        return array;
    }

    private static ReadableArray convertSubscriptionOfferDetailsListToArray (List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails) {
        WritableArray array = new WritableNativeArray();

        for(ProductDetails.SubscriptionOfferDetails details: subscriptionOfferDetails){
            WritableMap json = new WritableNativeMap();

            json.putString("basePlanId", details.getBasePlanId());
            json.putString("offerToken", details.getOfferToken());

            String offerId = details.getOfferId();
            if(offerId != null) json.putString("offerId", details.getOfferId());
            else json.putNull("offerId");

            json.putArray("offerTags", stringListToArray(details.getOfferTags()));
            json.putArray("pricingPhases", convertPricingPhaseListToArray(details.getPricingPhases().getPricingPhaseList()));
            array.pushMap(json);
        }
        return array;
    }

    private static ReadableMap convertOneTimePurchaseOfferDetailsToJson (ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails) {
        WritableMap json = new WritableNativeMap();

        json.putString("formattedPrice", oneTimePurchaseOfferDetails.getFormattedPrice());
        json.putString("priceCurrencyCode", oneTimePurchaseOfferDetails.getPriceCurrencyCode());
        json.putString("priceAmountMicros", String.valueOf(oneTimePurchaseOfferDetails.getPriceAmountMicros()));

        return json;
    }

    public static ReadableArray convertProductDetailsListToArray (List<ProductDetails> productDetailsList) {
        WritableArray array = new WritableNativeArray();

        for(ProductDetails productDetails: productDetailsList){
            WritableMap json = new WritableNativeMap();

            json.putString("name", productDetails.getName());
            json.putString("title", productDetails.getTitle());
            json.putString("productId", productDetails.getProductId());
            json.putString("productType", productDetails.getProductType());
            json.putString("description", productDetails.getDescription());

            List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetailsList = productDetails.getSubscriptionOfferDetails();
            if(subscriptionOfferDetailsList != null)
                json.putArray("subscriptionOfferDetails", convertSubscriptionOfferDetailsListToArray(subscriptionOfferDetailsList));
            else
                json.putNull("subscriptionOfferDetails");

            ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = productDetails.getOneTimePurchaseOfferDetails();
            if(oneTimePurchaseOfferDetails != null)
                json.putMap("oneTimePurchaseOfferDetails", convertOneTimePurchaseOfferDetailsToJson(oneTimePurchaseOfferDetails));
            else
                json.putNull("oneTimePurchaseOfferDetails");

            array.pushMap(json);
        }

        return array;
    }

    public static ReadableArray convertPurchaseHistoryRecordListToArray (List<PurchaseHistoryRecord> purchaseHistoryRecordList) {
        WritableArray array = new WritableNativeArray();
        for(PurchaseHistoryRecord purchaseHistoryRecord: purchaseHistoryRecordList){
            WritableMap json = new WritableNativeMap();

            json.putString("developerPayload", purchaseHistoryRecord.getDeveloperPayload());
            json.putString("originalJson", purchaseHistoryRecord.getOriginalJson());
            json.putString("productId", purchaseHistoryRecord.getProducts().get(0));
            json.putString("purchaseTime", String.valueOf(purchaseHistoryRecord.getPurchaseTime()));
            json.putString("purchaseToken", purchaseHistoryRecord.getPurchaseToken());
            json.putInt("quantity", purchaseHistoryRecord.getQuantity());
            json.putString("signature", purchaseHistoryRecord.getSignature());

            array.pushMap(json);
        }

        return array;
    }

    private static ReadableMap convertAccountIdentifiersToJson (AccountIdentifiers accountIdentifiers){
        WritableMap json = new WritableNativeMap();

        json.putString("obfuscatedAccountId", accountIdentifiers.getObfuscatedAccountId());
        json.putString("obfuscatedProfileId", accountIdentifiers.getObfuscatedProfileId());

        return json;
    }

    public static ReadableArray convertPurchaseListToArray (List<Purchase> purchaseList) {
        WritableArray array = new WritableNativeArray();

        for(Purchase purchase: purchaseList){
            WritableMap json = new WritableNativeMap();

            AccountIdentifiers accountIdentifiers = purchase.getAccountIdentifiers();
            if(accountIdentifiers != null) json.putMap("accountIdentifiers", convertAccountIdentifiersToJson(accountIdentifiers));
            else json.putNull("accountIdentifiers");

            json.putString("developerPayload", purchase.getDeveloperPayload());
            json.putString("orderId", purchase.getOrderId());
            json.putString("originalJson", purchase.getOriginalJson());
            json.putString("packageName", purchase.getPackageName());
            json.putString("productId", purchase.getProducts().get(0));
            json.putInt("purchaseState", purchase.getPurchaseState());
            json.putString("purchaseTime", String.valueOf(purchase.getPurchaseTime()));
            json.putString("purchaseToken", purchase.getPurchaseToken());
            json.putInt("quantity", purchase.getQuantity());
            json.putString("signature", purchase.getSignature());
            json.putBoolean("isAcknowledged", purchase.isAcknowledged());
            json.putBoolean("isAutoRenewing", purchase.isAutoRenewing());

            array.pushMap(json);
        }

        return array;
    }
}
