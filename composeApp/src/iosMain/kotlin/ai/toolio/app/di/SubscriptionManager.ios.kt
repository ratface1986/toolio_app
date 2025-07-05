package ai.toolio.app.di

import cocoapods.RevenueCat.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.collections.emptyList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual object SubscriptionManager {
    @OptIn(ExperimentalForeignApi::class)
    actual fun initialize(apiKey: String) {
        RCPurchases.configureWithAPIKey(apiKey)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getAvailablePackages(): List<SubscriptionPackage> =
        suspendCancellableCoroutine { cont ->
            RCPurchases.sharedPurchases().getOfferingsWithCompletion { offerings, error ->
                if (error != null) {
                    cont.resumeWithException(Throwable(error.localizedDescription ?: "Unknown error"))
                    return@getOfferingsWithCompletion
                }

                val allPackages = offerings?.current()?.availablePackages()?.filterIsInstance<RCPackage>() ?: emptyList()

                val result = allPackages.map {
                    val unit = when (it.storeProduct().subscriptionPeriod()?.unit()) {
                        RCSubscriptionPeriodUnitDay -> "Day"
                        RCSubscriptionPeriodUnitWeek -> "Week"
                        RCSubscriptionPeriodUnitMonth -> "Month"
                        RCSubscriptionPeriodUnitYear -> "Year"
                        else -> "N/A"
                    }

                    SubscriptionPackage(
                        id = it.identifier(),
                        title = it.storeProduct().localizedTitle() ?: "Unknown",
                        price = it.storeProduct().localizedPriceString() ?: "N/A",
                        period = unit
                    )
                }
                cont.resume(result)
            }
        }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun purchase(packageId: String): PurchaseResult =
        suspendCancellableCoroutine { cont ->
            RCPurchases.sharedPurchases().getOfferingsWithCompletion { offerings, error ->
                if (error != null || offerings?.current() == null) {
                    cont.resume(PurchaseResult(false, error?.localizedDescription ?: "No offerings"))
                    return@getOfferingsWithCompletion
                }

                val allPackages = offerings.current()?.availablePackages()?.filterIsInstance<RCPackage>() ?: emptyList()
                val pkg = allPackages.firstOrNull { it.identifier() == packageId }
                val all = allPackages.joinToString(", ") { it.identifier() }
                if (pkg == null) {
                    cont.resume(PurchaseResult(false, "Package not found ${allPackages.size} - $all"))
                    return@getOfferingsWithCompletion
                }

                RCPurchases.sharedPurchases().purchasePackage(pkg,
                    withCompletion = { transaction, customerInfo, error, userCancelled ->
                        when {
                            userCancelled -> cont.resume(PurchaseResult(false, "Cancelled"))
                            error != null -> cont.resume(PurchaseResult(false, error.localizedDescription))
                            else -> cont.resume(PurchaseResult(true))
                        }
                    })
            }
        }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCustomerInfo(): ToolioCustomerInfo =
        suspendCancellableCoroutine { cont ->
            RCPurchases.sharedPurchases().getCustomerInfoWithCompletion { info, error ->
                if (error != null || info == null) {
                    cont.resume(ToolioCustomerInfo(false, emptyList()))
                    return@getCustomerInfoWithCompletion
                }

                val active = info.entitlements().active().count() > 0
                val productIds = info.activeSubscriptions()
                    .mapNotNull { it as? String }


                cont.resume(ToolioCustomerInfo(active, productIds))
            }
        }
}