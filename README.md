# React Native Billing SDK

[![npm version](https://badge.fury.io/js/react-native-billing-sdk.svg)](https://badge.fury.io/js/react-native-billing-sdk)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A React Native library that exposes the native [Google Play Billing Library](https://developer.android.com/google/play/billing/integrate) functionality, implementing Android Billing Client v6.0 for handling in-app purchases and subscriptions.

> **⚠️ Important:** By August 31, 2025, all new apps and updates must use Billing Library version 7 or newer. This library is built on v6.0 and will be updated to meet Google's requirements.

## Features

- 🛒 **One-Time Products** - Handle consumable and non-consumable in-app purchases
- 🔄 **Subscriptions** - Complete subscription lifecycle with grace periods, hold states, and renewals
- 📱 **Native Android Billing** - Direct access to Google Play Billing Library v6.0 APIs
- 🎯 **TypeScript Support** - Full type safety with comprehensive TypeScript definitions
- ⚡ **Real-Time Updates** - Purchase state changes and billing service events
- 🔧 **Simple Integration** - React Native wrapper around native billing functionality
- ⏳ **Pending Transactions** - Support for delayed payment methods and pending states
- 📊 **Multi-Quantity** - Handle multiple quantities of the same product in one purchase

## Platform Support

| Platform | Status |
|----------|--------|
| Android  | ✅ Full support (Billing Client v6.0) |
| iOS      | ❌ Not implemented yet |

## Installation

```bash
npm install react-native-billing-sdk
# or
yarn add react-native-billing-sdk
```

### Android Setup

1. **Add Google Play Billing dependency** to your `android/app/build.gradle`:

```gradle
dependencies {
    implementation 'com.android.billingclient:billing:6.0.1'
    // ... other dependencies
}
```

2. **Add BILLING permission** to your `android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="com.android.vending.BILLING" />
```

3. **Configure your app** in Google Play Console with in-app products/subscriptions.

### iOS Setup

iOS support is not yet implemented. The library will reject calls on iOS with "Unsupported platform" error.

## Purchase Lifecycle

Following the [official Google Play Billing flow](https://developer.android.com/google/play/billing/integrate), this library supports the complete purchase lifecycle:

1. **Show Products** - Query and display available products to users
2. **Launch Purchase** - Initiate the billing flow for user acceptance
3. **Verify Purchase** - Validate purchases on your server (recommended)
4. **Deliver Content** - Provide the purchased content to the user
5. **Acknowledge Purchase** - Confirm delivery for non-consumables or consume for consumables

### Subscription States

Subscriptions can transition through various states as defined in the [Android Billing documentation](https://developer.android.com/google/play/billing/integrate):

- **Active** - User has access and subscription is in good standing
- **Cancelled** - User cancelled but retains access until expiration
- **Grace Period** - Payment issue occurred, user retains access while Google retries payment
- **On Hold** - Payment issue occurred, user loses access while Google retries payment
- **Paused** - User paused their subscription access

## Usage

### Basic Setup

```javascript
import { BillingSdkAndroid, BillingSdkAndroidConstants } from 'react-native-billing-sdk';

// Initialize billing connection
const initializeBilling = async () => {
  try {
    await BillingSdkAndroid.startConnection();
    console.log('Billing client connected');
  } catch (error) {
    console.error('Failed to connect billing client:', error);
  }
};

// Set up purchase listener
const removePurchaseListener = BillingSdkAndroid.setPurchaseUpdatedListener(
  ({ responseCode, purchases, debugMessage }) => {
    if (responseCode === BillingSdkAndroidConstants.ResponseCode.OK && purchases) {
      purchases.forEach(purchase => {
        console.log('Purchase received:', purchase);
        // Handle purchase (acknowledge, consume, etc.)
      });
    } else {
      console.error('Purchase failed:', debugMessage);
    }
  }
);

// Clean up listeners when component unmounts
return () => {
  removePurchaseListener();
};
```

### Querying Products

```javascript
// Query in-app products
const queryProducts = async () => {
  try {
    const products = await BillingSdkAndroid.queryProductDetails(
      ['your_product_id_1', 'your_product_id_2'],
      BillingSdkAndroidConstants.ProductType.INAPP
    );
    
    products.forEach(product => {
      console.log(`Product: ${product.title}`);
      console.log(`Price: ${product.oneTimePurchaseOfferDetails?.formattedPrice}`);
    });
  } catch (error) {
    console.error('Failed to query products:', error);
  }
};

// Query subscriptions
const querySubscriptions = async () => {
  try {
    const subscriptions = await BillingSdkAndroid.queryProductDetails(
      ['your_subscription_id'],
      BillingSdkAndroidConstants.ProductType.SUBS
    );
    
    subscriptions.forEach(subscription => {
      console.log(`Subscription: ${subscription.title}`);
      subscription.subscriptionOfferDetails?.forEach(offer => {
        console.log(`Offer: ${offer.pricingPhases[0]?.formattedPrice}`);
      });
    });
  } catch (error) {
    console.error('Failed to query subscriptions:', error);
  }
};
```

### Making Purchases

```javascript
// Purchase an in-app product
const purchaseProduct = async (productId: string) => {
  try {
    await BillingSdkAndroid.launchBillingFlow(productId);
    // Purchase result will be delivered to setPurchaseUpdatedListener
  } catch (error) {
    console.error('Failed to launch billing flow:', error);
  }
};

// Purchase a subscription with specific offer
const purchaseSubscription = async (productId: string, offerToken: string) => {
  try {
    await BillingSdkAndroid.launchBillingFlow(productId, offerToken);
  } catch (error) {
    console.error('Failed to launch billing flow:', error);
  }
};

// Upgrade/downgrade subscription
const changeSubscription = async (
  newProductId: string,
  newOfferToken: string,
  oldPurchaseToken: string
) => {
  try {
    await BillingSdkAndroid.launchBillingFlow(
      newProductId,
      newOfferToken,
      oldPurchaseToken,
      BillingSdkAndroidConstants.SubscriptionReplacementMode.CHARGE_PRORATED_PRICE
    );
  } catch (error) {
    console.error('Failed to change subscription:', error);
  }
};
```

### Managing Purchases

```javascript
// Acknowledge a purchase (required for non-consumable products)
const acknowledgePurchase = async (purchaseToken: string) => {
  try {
    await BillingSdkAndroid.acknowledgePurchase(purchaseToken);
    console.log('Purchase acknowledged');
  } catch (error) {
    console.error('Failed to acknowledge purchase:', error);
  }
};

// Consume a purchase (for consumable products)
const consumePurchase = async (purchaseToken: string) => {
  try {
    await BillingSdkAndroid.consume(purchaseToken);
    console.log('Purchase consumed');
  } catch (error) {
    console.error('Failed to consume purchase:', error);
  }
};

// Query active purchases
const queryActivePurchases = async () => {
  try {
    const purchases = await BillingSdkAndroid.queryPurchases(
      BillingSdkAndroidConstants.ProductType.INAPP
    );
    
    purchases.forEach(purchase => {
      console.log(`Active purchase: ${purchase.productId}`);
      
      if (!purchase.isAcknowledged) {
        // Acknowledge if needed
        acknowledgePurchase(purchase.purchaseToken);
      }
    });
  } catch (error) {
    console.error('Failed to query purchases:', error);
  }
};

// Query purchase history
const queryPurchaseHistory = async () => {
  try {
    const history = await BillingSdkAndroid.queryPurchaseHistory(
      BillingSdkAndroidConstants.ProductType.INAPP
    );
    
    history?.forEach(record => {
      console.log(`Past purchase: ${record.productId} at ${record.purchaseTime}`);
    });
  } catch (error) {
    console.error('Failed to query purchase history:', error);
  }
};
```

### Handling Pending Transactions

As per [Google's documentation](https://developer.android.com/google/play/billing/integrate), some payment methods may result in pending transactions that complete asynchronously:

```javascript
// Handle pending purchases in your purchase listener
const removePurchaseListener = BillingSdkAndroid.setPurchaseUpdatedListener(
  ({ responseCode, purchases, debugMessage }) => {
    if (responseCode === BillingSdkAndroidConstants.ResponseCode.OK && purchases) {
      purchases.forEach(purchase => {
        switch (purchase.purchaseState) {
          case BillingSdkAndroidConstants.PurchaseState.PURCHASED:
            // Purchase completed - deliver content and acknowledge
            console.log('Purchase completed:', purchase.productId);
            deliverContent(purchase);
            if (!purchase.isAcknowledged) {
              BillingSdkAndroid.acknowledgePurchase(purchase.purchaseToken);
            }
            break;
            
          case BillingSdkAndroidConstants.PurchaseState.PENDING:
            // Payment is pending - inform user and wait
            console.log('Purchase pending:', purchase.productId);
            showPendingPaymentUI(purchase);
            // Do NOT acknowledge pending purchases
            break;
            
          case BillingSdkAndroidConstants.PurchaseState.UNSPECIFIED_STATE:
            console.log('Unknown purchase state:', purchase.productId);
            break;
        }
      });
    }
  }
);
```

> **Important:** Only acknowledge purchases when `purchaseState` is `PURCHASED`. The 3-day acknowledgment window begins only when the purchase transitions from `PENDING` to `PURCHASED`.

### Connection Management

```javascript
// Check connection state
const checkConnection = async () => {
  try {
    const state = await BillingSdkAndroid.getConnectionState();
    console.log('Connection state:', state);
    
    if (state !== BillingSdkAndroidConstants.ConnectionState.CONNECTED) {
      await BillingSdkAndroid.startConnection();
    }
  } catch (error) {
    console.error('Connection check failed:', error);
  }
};

// Handle disconnection
const removeDisconnectionListener = BillingSdkAndroid.setBillingServiceDisconnectedListener(() => {
  console.log('Billing service disconnected, attempting to reconnect...');
  BillingSdkAndroid.startConnection();
});

// End connection when done
const cleanup = async () => {
  await BillingSdkAndroid.endConnection();
  removeDisconnectionListener();
};
```

## API Reference

### Methods

| Method | Description | Parameters | Returns |
|--------|-------------|------------|---------|
| `startConnection()` | Establishes connection to Google Play Billing | None | `Promise<void>` |
| `endConnection()` | Terminates the billing connection | None | `Promise<void>` |
| `getConnectionState()` | Gets current connection state | None | `Promise<ConnectionState>` |
| `queryProductDetails()` | Retrieves product/subscription details | `productIds: string[]`, `productType: ProductType` | `Promise<ProductDetails[]>` |
| `launchBillingFlow()` | Initiates purchase flow | `productId: string`, `offerToken?: string`, `oldPurchaseToken?: string`, `replacementMode?: SubscriptionReplacementMode` | `Promise<void>` |
| `acknowledgePurchase()` | Acknowledges a purchase | `purchaseToken: string` | `Promise<void>` |
| `consume()` | Consumes a purchase | `purchaseToken: string` | `Promise<void>` |
| `queryPurchases()` | Gets active purchases | `productType: ProductType` | `Promise<Purchase[]>` |
| `queryPurchaseHistory()` | Gets purchase history | `productType: ProductType` | `Promise<PurchaseHistoryRecord[]>` |

### Event Listeners

| Listener | Description | Callback Parameters |
|----------|-------------|-------------------|
| `setPurchaseUpdatedListener()` | Listens for purchase updates | `{ responseCode, purchases, debugMessage }` |
| `setBillingServiceDisconnectedListener()` | Listens for service disconnection | None |

### Constants

#### ProductType
- `INAPP` - In-app products
- `SUBS` - Subscriptions

#### PurchaseState
- `PURCHASED` - Purchase completed
- `PENDING` - Purchase pending
- `UNSPECIFIED_STATE` - Unknown state

#### ConnectionState
- `DISCONNECTED` - Not connected
- `CONNECTING` - Connecting
- `CONNECTED` - Connected
- `CLOSED` - Connection closed

#### ResponseCode
- `OK` - Success
- `USER_CANCELLED` - User cancelled
- `SERVICE_UNAVAILABLE` - Service unavailable
- `BILLING_UNAVAILABLE` - Billing unavailable
- `ITEM_UNAVAILABLE` - Item unavailable
- `DEVELOPER_ERROR` - Developer error
- `ERROR` - General error
- And more...

## Error Handling

This library exposes the native [Google Play Billing response codes](https://developer.android.com/google/play/billing/errors). All API calls should be wrapped in try-catch blocks to handle various error conditions:

```javascript
try {
  await BillingSdkAndroid.startConnection();
} catch (error) {
  switch (error.code) {
    case BillingSdkAndroidConstants.ResponseCode.BILLING_UNAVAILABLE:
      console.log('Billing not available on this device');
      break;
    case BillingSdkAndroidConstants.ResponseCode.SERVICE_UNAVAILABLE:
      console.log('Google Play Store service is unavailable');
      break;
    case BillingSdkAndroidConstants.ResponseCode.USER_CANCELLED:
      console.log('User cancelled the purchase');
      break;
    case BillingSdkAndroidConstants.ResponseCode.ITEM_ALREADY_OWNED:
      console.log('User already owns this item');
      break;
    case BillingSdkAndroidConstants.ResponseCode.DEVELOPER_ERROR:
      console.error('Developer error - check your configuration');
      break;
    default:
      console.error('Billing error:', error.message);
  }
}
```

### Common Error Scenarios

- **BILLING_UNAVAILABLE** - Device doesn't support billing (e.g., emulator without Google Play)
- **SERVICE_UNAVAILABLE** - Google Play Store is not available or outdated
- **ITEM_UNAVAILABLE** - Product ID not found in Google Play Console
- **DEVELOPER_ERROR** - App not properly configured in Google Play Console

## Testing

Follow the [official testing guidelines](https://developer.android.com/google/play/billing/test) from Google:

### Test Environments

1. **Google Play Console Test Tracks** - Use internal/closed testing tracks
2. **Test Accounts** - Add test accounts in Google Play Console  
3. **License Testing** - Test with special license testing accounts
4. **Test Products** - Create test products that won't charge real money

### Testing Pending Transactions

You can test pending transactions using [license testing](https://developer.android.com/google/play/billing/test) with special test payment methods that simulate delayed payment completion or cancellation.

### Test Cards and Payment Methods

- Use test credit cards provided by Google for different scenarios
- Test various payment methods including those that result in pending states
- Verify proper handling of declined payments and cancellations

> **Important:** Always test your complete purchase flow including server-side verification before releasing to production.

## Requirements

- React Native >= 0.60
- Android API level 21+
- Google Play Billing Library 6.0+
- Node.js >= 16.0.0

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## Official Google Documentation

This library is a React Native wrapper around the native Google Play Billing Library. For comprehensive information about billing concepts, best practices, and advanced features, refer to the official documentation:

- 📚 [Google Play Billing Library Integration](https://developer.android.com/google/play/billing/integrate)
- ⚠️ [Billing Error Codes and Handling](https://developer.android.com/google/play/billing/errors)
- 🧪 [Testing In-App Purchases](https://developer.android.com/google/play/billing/test)
- 🔄 [Subscription Lifecycle Management](https://developer.android.com/google/play/billing/subscriptions)
- 🏗️ [Server-Side Verification](https://developer.android.com/google/play/billing/security)
- 📋 [Play Console Product Setup](https://support.google.com/googleplay/android-developer/answer/1153481)

## Support

- 🐛 [Report bugs](https://github.com/kaancembertas/react-native-billing-sdk/issues)
- 💡 [Request features](https://github.com/kaancembertas/react-native-billing-sdk/issues)
- 📖 [View documentation](https://github.com/kaancembertas/react-native-billing-sdk)
- 📚 [Google Play Billing Docs](https://developer.android.com/google/play/billing)

## Author

**Kaan Çembertaş** - [@kaancembertas](https://github.com/kaancembertas)

---

Made with ❤️ for the React Native community
