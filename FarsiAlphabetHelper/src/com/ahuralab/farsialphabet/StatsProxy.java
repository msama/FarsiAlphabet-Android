package com.ahuralab.farsialphabet;

import android.content.Context;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.monetization.AmazonMonetizationEventBuilder;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.monetization.GooglePlayMonetizationEventBuilder;
import com.amazonaws.regions.Regions;
import android.util.Log;

/**
 * State-full stats sender.
 *
 * @author msama (michele.sama@gmail.com)
 *
 */
public class StatsProxy {
	static final private String ANALYTICS_APP_ID = "cc06948520474102a30ecea5794ec0c0";
	static final private String AWS_ACCOUNT_ID = "656882140047";
	static final private String COGNITO_IDENTITY_POOL = "eu-west-1:61ac358e-422f-42fa-9fa2-c71d9a0a067c";
	static final private String UNAUTHENTICATED_ROLE = "arn:aws:iam::656882140047:role/Cognito_farsi_alphaUnauth_DefaultRole";
	static final private String AUTHENTICATED_ROLE = "arn:aws:iam::656882140047:role/Cognito_farsi_alphaAuth_DefaultRole";
	private MobileAnalyticsManager analytics;

	public StatsProxy(Context context) {
		this(context, ANALYTICS_APP_ID, AWS_ACCOUNT_ID, COGNITO_IDENTITY_POOL,
				UNAUTHENTICATED_ROLE, AUTHENTICATED_ROLE);
	}

	public StatsProxy(Context context, String analyticsAppId,
			String awsAccountId, String cognitoIdentityPool,
			String unauthenticatedRole, String authenticatedRole) {
		CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
				context, awsAccountId, cognitoIdentityPool,
				unauthenticatedRole, authenticatedRole, Regions.EU_WEST_1);
		try {
			AnalyticsConfig options = new AnalyticsConfig();
			options.withAllowsWANDelivery(true);
			analytics = MobileAnalyticsManager
					.getOrCreateInstance(context, analyticsAppId,
							Regions.US_EAST_1, cognitoProvider, options);
		} catch (InitializationException ex) {
			Log.e(this.getClass().getName(),
					"Failed to initialize Amazon Mobile Analytics", ex);
		}
	}

	public void onPause() {
		if (analytics != null) {
			analytics.getSessionClient().pauseSession();
			// Attempt to send any events that have been recorded to the Mobile
			// Analytics service.
			analytics.getEventClient().submitEvents();
		}
	}

	public void onResume() {
		if (analytics != null) {
			analytics.getSessionClient().resumeSession();
		}
	}

	public void traceGooglePlayPurchase(String productId, String price,
			String orderId, double quantity) {
		// If your in-app item was purchased by using Google Play, record an
		// event similar to the following, where:
		// "MY_PRODUCT_ID" is the product id of the item purchased,
		// "$4.99" is the localized price of the item (accessed from the product
		// item details in the Google getSkuDetails request),
		// 1 is the quantity of items purchased, and
		// "ORDER_ID" is the order id found in the INAPP_PURCHASE_DATA of a
		// Google purchase request.
		GooglePlayMonetizationEventBuilder builder = GooglePlayMonetizationEventBuilder
				.create(analytics.getEventClient());
		AnalyticsEvent purchaseEvent = builder.withProductId(productId)
				.withFormattedItemPrice(price).withQuantity(quantity)
				.withTransactionId(orderId).build();
		analytics.getEventClient().recordEvent(purchaseEvent);
	}

	public void traceAmazonPurchase(String productId, String price,
			double quantity) {
		// If your in-app item was purchased by using Amazon IAP, record an
		// event similar to the following, where:
		// "MY_PRODUCT_ID" is the product id of the item purchased,
		// "$1.99" is the localized price of the item (accessed from the
		// getPrice() method of the Amazon Item class), and
		// 1 is the quantity of items purchased.
		AmazonMonetizationEventBuilder builder = AmazonMonetizationEventBuilder
				.create(analytics.getEventClient());
		AnalyticsEvent purchaseEvent = builder.withProductId(productId)
				.withFormattedItemPrice(price).withQuantity(quantity).build();
		analytics.getEventClient().recordEvent(purchaseEvent);
	}

	public EventBuilder traceCustomEvent(String eventName) {
		return new EventBuilder(eventName);
	}

	public class EventBuilder {
		private final AnalyticsEvent event;

		public EventBuilder(String eventName) {
			event = analytics.getEventClient().createEvent(eventName);
		}

		public EventBuilder withAttribute(String key, String value) {
			event.withAttribute(key, value);
			return this;
		}

		public void record() {
			// Record the event.
			analytics.getEventClient().recordEvent(event);
		}
	}
}