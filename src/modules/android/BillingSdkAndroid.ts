import { NativeEventEmitter, NativeModules, Platform } from 'react-native';
import type {
    BillingServiceDisconnectedListener,
    IBillingSdkAndroid,
    IBillingSdkAndroidNative,
    PurchaseUpdatedListener,
} from './types';
import { BillingSdkEvent } from './constants';

const BillingSdk = NativeModules.BillingSdk as IBillingSdkAndroidNative;
const eventEmitter = new NativeEventEmitter(NativeModules.BillingSdk);

class BillingSdkAndroid implements IBillingSdkAndroid {
    private ensurePlatform = () => {
        if (Platform.OS === 'android') {
            return;
        }

        return () => Promise.reject('Unsupported platform.');
    };
    public startConnection = this.ensurePlatform() ?? BillingSdk.startConnection;
    public endConnection = this.ensurePlatform() ?? BillingSdk.endConnection;
    public getConnectionState = this.ensurePlatform() ?? BillingSdk.getConnectionState;
    public queryProductDetails = this.ensurePlatform() ?? BillingSdk.queryProductDetails;
    public launchBillingFlow = this.ensurePlatform() ?? BillingSdk.launchBillingFlow;
    public acknowledgePurchase = this.ensurePlatform() ?? BillingSdk.acknowledgePurchase;
    public queryPurchaseHistory = this.ensurePlatform() ?? BillingSdk.queryPurchaseHistory;
    public queryPurchases = this.ensurePlatform() ?? BillingSdk.queryPurchases;
    public consume = this.ensurePlatform() ?? BillingSdk.consume;

    public setPurchaseUpdatedListener = (listener: PurchaseUpdatedListener) => {
        const eventListener = eventEmitter.addListener(BillingSdkEvent.PURCHASE_UPDATED, listener);
        return eventListener.remove;
    };

    public setBillingServiceDisconnectedListener = (listener: BillingServiceDisconnectedListener) => {
        const eventListener = eventEmitter.addListener(BillingSdkEvent.BILLING_SERVICE_DISCONNECTED, listener);
        return eventListener.remove;
    };
}

export default new BillingSdkAndroid();