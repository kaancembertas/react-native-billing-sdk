/* eslint-disable @typescript-eslint/no-namespace */

import { BillingSdkAndroidConstants } from './constants';

export namespace BillingSdkAndroidTypes {
    export type ProductDetailParams = {
        productId: string;
        productType: BillingSdkAndroidConstants.ProductType;
    };

    export type PricingPhase = {
        billingPeriod: string;
        formattedPrice: string;
        priceAmountMicros: string;
        priceCurrencyCode: string;
        recurrenceMode: BillingSdkAndroidConstants.RecurrenceMode;
        billingCycleCount: number;
    };

    export type SubscriptionOfferDetails = {
        basePlanId: string;
        offerToken: string;
        offerId: string | null;
        offerTags: string[];
        pricingPhases: PricingPhase[];
    };

    export type OneTimePurchaseOfferDetails = {
        formattedPrice: string;
        priceCurrencyCode: string;
        priceAmountMicros: string;
    };

    export type ProductDetails = {
        name: string;
        title: string;
        productId: string;
        productType: string;
        description: string;
        subscriptionOfferDetails: SubscriptionOfferDetails[] | null;
        oneTimePurchaseOfferDetails: OneTimePurchaseOfferDetails | null;
    };

    export type AccountIdentifiers = {
        obfuscatedAccountId: string;
        obfuscatedProfileId: string;
    };

    export type PurchaseHistoryRecord = {
        developerPayload: string;
        originalJson: string;
        productId: string;
        purchaseTime: string;
        purchaseToken: string;
        quantity: number;
        signature: string;
    };

    export type Purchase = {
        accountIdentifiers: AccountIdentifiers | null;
        developerPayload: string;
        orderId: string;
        originalJson: string;
        packageName: string;
        productId: string;
        purchaseState: BillingSdkAndroidConstants.PurchaseState;
        purchaseTime: string;
        purchaseToken: string;
        quantity: number;
        signature: string;
        isAcknowledged: boolean;
        isAutoRenewing: boolean;
    };

    export interface BillingSdkError extends Error {
        code: BillingSdkAndroidConstants.ResponseCode;
    }

    export interface IBillingSdkAndroidNative {
        startConnection: () => Promise<void>;
        endConnection: () => Promise<void>;
        getConnectionState: () => Promise<BillingSdkAndroidConstants.ConnectionState>;
        queryProductDetails: (
            productIds: string[],
            productType: BillingSdkAndroidConstants.ProductType,
        ) => Promise<ProductDetails[]>;
        launchBillingFlow: (
            productId: string,
            offerToken?: string,
            oldPurchaseToken?: string,
            subscriptionReplacementMode?: BillingSdkAndroidConstants.SubscriptionReplacementMode,
        ) => Promise<void>;
        acknowledgePurchase: (purchaseToken: string) => Promise<void>;
        queryPurchaseHistory: (
            productType: BillingSdkAndroidConstants.ProductType,
        ) => Promise<PurchaseHistoryRecord[] | null>;
        queryPurchases: (productType: BillingSdkAndroidConstants.ProductType) => Promise<Purchase[]>;
        consume: (purchaseToken: string) => Promise<void>;
    }

    export type PurchaseUpdatedListenerParams = {
        responseCode: BillingSdkAndroidConstants.ResponseCode;
        debugMessage: string;
        purchases: Purchase[] | null;
    };

    export type PurchaseUpdatedListener = (params: PurchaseUpdatedListenerParams) => void;
    export type BillingServiceDisconnectedListener = () => void;
    export type RemoveListener = () => void;

    export interface IBillingSdkAndroid extends IBillingSdkAndroidNative {
        setPurchaseUpdatedListener: (listener: PurchaseUpdatedListener) => RemoveListener;
        setBillingServiceDisconnectedListener: (listener: BillingServiceDisconnectedListener) => RemoveListener;
    }
}
