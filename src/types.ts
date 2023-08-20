import { RecurrenceMode, ProductType, PurchaseState, ResponseCode, ConnectionState } from './constants';

export type ProductDetailParams = {
    productId: string;
    productType: ProductType;
};

export type PricingPhase = {
    billingPeriod: string;
    formattedPrice: string;
    priceAmountMicros: string;
    priceCurrencyCode: string;
    recurrenceMode: RecurrenceMode;
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
    purchaseState: PurchaseState;
    purchaseTime: string;
    purchaseToken: string;
    quantity: number;
    signature: string;
    isAcknowledged: boolean;
    isAutoRenewing: boolean;
};

export interface BillingSdkError extends Error {
    code: ResponseCode;
}

export interface IBillingSdkNative {
    startConnection: () => Promise<void>;
    endConnection: () => Promise<void>;
    getConnectionState: () => Promise<ConnectionState>;
    queryProductDetails: (productIds: string, productType: ProductType) => Promise<ProductDetails[]>;
    launchBillingFlow: (productId: string, offerToken: string) => Promise<void>;
    acknowledgePurchase: (purchaseToken: string) => Promise<void>;
    queryPurchaseHistory: (productType: ProductType) => Promise<PurchaseHistoryRecord[] | null>;
    queryPurchases: (productType: ProductType) => Promise<Purchase[]>;
    consume: (purchaseToken: string) => Promise<void>;
}

export type PurchaseUpdatedListenerParams = {
    responseCode: ResponseCode;
    debugMessage: string;
    purchases: Purchase[] | null;
};

export type PurchaseUpdatedListener = (params: PurchaseUpdatedListenerParams) => void;
export type BillingServiceDisconnectedListener = () => void;
export type RemoveListener = () => void;

export interface IBillingSdk extends IBillingSdkNative {
    setPurchaseUpdatedListener: (listener: PurchaseUpdatedListener) => RemoveListener;
    setBillingServiceDisconnectedListener: (listener: BillingServiceDisconnectedListener) => RemoveListener;
}
