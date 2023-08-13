import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
import { BillingManagerEvent } from './constants';
import type { IBillingSdkNative, IBillingSdk } from './types';
export * from './types';
export * from './constants';

const BillingManager = NativeModules.BillingManager as IBillingSdkNative;
const eventEmitter = new NativeEventEmitter(NativeModules.BillingManager);

/**
 *
 * @returns undefined if platform is Android, otherwise Promise rejection
 */
const ensurePlatform = () => {
    if (Platform.OS === 'android') {
        return;
    }

    return () => Promise.reject('Unsupported platform.');
};

export const startConnection: IBillingSdk['startConnection'] = ensurePlatform() ?? BillingManager.startConnection;
export const endConnection: IBillingSdk['endConnection'] = ensurePlatform() ?? BillingManager.endConnection;
export const getConnectionState: IBillingSdk['getConnectionState'] = ensurePlatform() ?? BillingManager.getConnectionState;
export const queryProductDetails: IBillingSdk['queryProductDetails'] = ensurePlatform() ?? BillingManager.queryProductDetails;
export const launchBillingFlow: IBillingSdk['launchBillingFlow'] = ensurePlatform() ?? BillingManager.launchBillingFlow;
export const acknowledgePurchase: IBillingSdk['acknowledgePurchase'] = ensurePlatform() ?? BillingManager.acknowledgePurchase;
export const queryPurchaseHistory: IBillingSdk['queryPurchaseHistory'] = ensurePlatform() ?? BillingManager.queryPurchaseHistory;
export const queryPurchases: IBillingSdk['queryPurchases'] = ensurePlatform() ?? BillingManager.queryPurchases;
export const consume: IBillingSdk['consume'] = ensurePlatform() ?? BillingManager.consume;

export const setPurchaseUpdatedListener: IBillingSdk['setPurchaseUpdatedListener'] = (listener) => {
    const eventListener = eventEmitter.addListener(BillingManagerEvent.PURCHASE_UPDATED, listener);
    return eventListener.remove;
};

export const setBillingServiceDisconnectedListener: IBillingSdk['setBillingServiceDisconnectedListener'] = (listener) => {
    const eventListener = eventEmitter.addListener(BillingManagerEvent.BILLING_SERVICE_DISCONNECTED, listener);
    return eventListener.remove;
};
