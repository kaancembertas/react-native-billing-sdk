/* eslint-disable @typescript-eslint/no-namespace */
export namespace BillingSdkAndroidConstants {
    export enum ProductType {
        SUBS = 'subs',
        INAPP = 'inapp',
    }

    export enum RecurrenceMode {
        INFINITE_RECURRING = 1,
        FINITE_RECURRING = 2,
        NON_RECURRING = 3,
    }

    export enum PurchaseState {
        UNSPECIFIED_STATE = 0,
        PURCHASED = 1,
        PENDING = 2,
    }

    export enum SubscriptionReplacementMode {
        CHARGE_FULL_PRICE = 5,
        CHARGE_PRORATED_PRICE = 2,
        DEFERRED = 6,
        WITHOUT_PRORATION = 3,
        WITH_TIME_PRORATION = 1,
    }

    export enum ResponseCode {
        // NATIVE RESPONSE CODES
        BILLING_UNAVAILABLE = '3',
        DEVELOPER_ERROR = '5',
        ERROR = '6',
        FEATURE_NOT_SUPPORTED = '-2',
        ITEM_ALREADY_OWNED = '7',
        ITEM_NOT_OWNED = '8',
        ITEM_UNAVAILABLE = '4',
        NETWORK_ERROR = '12',
        OK = '0',
        SERVICE_DISCONNECTED = '-1',
        SERVICE_TIMEOUT = '-3',
        SERVICE_UNAVAILABLE = '2',
        USER_CANCELLED = '1',

        // SDK BRIDGE ERROR CODES
        E_ACTIVITY_NULL = '100',
        E_PRODUCT_NOT_QUERIED = '101',
    }

    export enum ConnectionState {
        DISCONNECTED = 0,
        CONNECTING = 1,
        CONNECTED = 2,
        CLOSED = 3,
    }

    export enum BillingSdkEvent {
        PURCHASE_UPDATED = 'billing-manager-purchase-updated',
        BILLING_SERVICE_DISCONNECTED = 'billing-manager-service-disconnected',
    }
}
