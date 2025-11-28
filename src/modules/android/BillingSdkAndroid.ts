import { NativeEventEmitter, NativeModules, Platform } from 'react-native';
import { BillingSdkAndroidTypes } from './types';
import { BillingSdkAndroidConstants } from './constants';

const { BillingSdkEvent } = BillingSdkAndroidConstants;
const BillingSdk = NativeModules.BillingSdk as BillingSdkAndroidTypes.IBillingSdkAndroidNative;
const eventEmitter = new NativeEventEmitter(NativeModules.BillingSdk);

const isAndroid = Platform.OS === 'android';
class BillingSdkAndroid implements BillingSdkAndroidTypes.IBillingSdkAndroid {
    private ensurePlatform = () => {
        if (isAndroid) {
            return;
        }

        return () => Promise.reject('Unsupported platform.');
    };
    public startConnection = this.ensurePlatform() ?? BillingSdk.startConnection;
    public endConnection = this.ensurePlatform() ?? BillingSdk.endConnection;
    public getConnectionState = this.ensurePlatform() ?? BillingSdk.getConnectionState;
    public queryProductDetails = this.ensurePlatform() ?? BillingSdk.queryProductDetails;
    public acknowledgePurchase = this.ensurePlatform() ?? BillingSdk.acknowledgePurchase;
    public queryPurchaseHistory = this.ensurePlatform() ?? BillingSdk.queryPurchaseHistory;
    public queryPurchases = this.ensurePlatform() ?? BillingSdk.queryPurchases;
    public consume = this.ensurePlatform() ?? BillingSdk.consume;

    public setPurchaseUpdatedListener = (listener: BillingSdkAndroidTypes.PurchaseUpdatedListener) => {
        const eventListener = eventEmitter.addListener(BillingSdkEvent.PURCHASE_UPDATED, listener);
        return eventListener.remove;
    };

    public setBillingServiceDisconnectedListener = (
        listener: BillingSdkAndroidTypes.BillingServiceDisconnectedListener,
    ) => {
        const eventListener = eventEmitter.addListener(BillingSdkEvent.BILLING_SERVICE_DISCONNECTED, listener);
        return eventListener.remove;
    };

    public launchBillingFlow = async (
        productId: string,
        offerToken?: string,
        oldPurchaseToken?: string,
        subscriptionReplacementMode: BillingSdkAndroidConstants.SubscriptionReplacementMode = BillingSdkAndroidConstants
            .SubscriptionReplacementMode.UNKNOWN_REPLACEMENT_MODE,
        obfuscatedAccountId?: string,
        obfuscatedProfileId?: string,
    ): Promise<void> => {
        if (!isAndroid) {
            return Promise.reject('Unsupported platform.');
        }

        return BillingSdk.launchBillingFlow(
            productId,
            offerToken,
            oldPurchaseToken,
            subscriptionReplacementMode,
            obfuscatedAccountId,
            obfuscatedProfileId,
        );
    };
}

export default new BillingSdkAndroid();
