package ai.toolio.app.di

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.StoreTransaction
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressLint("StaticFieldLeak")
actual object SubscriptionManager {
    private lateinit var context: Context

    actual fun initialize(apiKey: String) {
        Purchases.configure(
            PurchasesConfiguration.Builder(context, apiKey).build()
        )
        Purchases.logLevel = LogLevel.DEBUG
    }

    fun setContext(appContext: Context) {
        context = appContext
    }

    actual suspend fun getAvailablePackages(): List<SubscriptionPackage> =
        suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getOfferingsWith({
                Log.e("SubscriptionManager", "Error getting offerings: $it")
                cont.resumeWithException(Throwable(it.message))
            }) { offerings: Offerings ->
                val allPackages = offerings.current?.availablePackages ?: emptyList()
                val result = allPackages.map {
                    SubscriptionPackage(
                        id = it.identifier,
                        title = it.product.title,
                        price = it.product.price.toString(),
                        period = it.product.period?.toString() ?: "N/A"
                    )
                }
                cont.resume(result)
            }
        }

    actual suspend fun purchase(packageId: String): PurchaseResult =
        suspendCancellableCoroutine { cont ->
            try {
                val activity = context as? Activity
                if (activity == null) {
                    cont.resume(PurchaseResult(false, "Context is not Activity"))
                    return@suspendCancellableCoroutine
                }

                Purchases.sharedInstance.getOfferingsWith(
                    onError = {
                        cont.resume(PurchaseResult(false, it.message))
                    },
                    onSuccess = { offerings ->
                        val availablePackages = offerings.current?.availablePackages
                        if (availablePackages == null || availablePackages.isEmpty()) {
                            cont.resume(PurchaseResult(false, "No available packages"))
                            return@getOfferingsWith
                        }

                        val pkg = availablePackages.firstOrNull { it.identifier == packageId }
                        val all = availablePackages.joinToString(", ") { it.identifier }

                        if (pkg == null) {
                            cont.resume(PurchaseResult(false, "Package not found $all"))
                            return@getOfferingsWith
                        }

                        val purchaseParams = try {
                            PurchaseParams.Builder(activity, pkg).build()
                        } catch (e: Exception) {
                            cont.resume(PurchaseResult(false, "Failed to build purchase params"))
                            return@getOfferingsWith
                        }

                        Purchases.sharedInstance.purchase(
                            purchaseParams,
                            object : PurchaseCallback {
                                override fun onCompleted(
                                    storeTransaction: StoreTransaction,
                                    customerInfo: CustomerInfo
                                ) {
                                    cont.resume(PurchaseResult(true))
                                }

                                override fun onError(
                                    error: com.revenuecat.purchases.PurchasesError,
                                    userCancelled: Boolean
                                ) {
                                    val message = if (userCancelled) {
                                        "Cancelled"
                                    } else {
                                        error.message
                                    }
                                    cont.resume(PurchaseResult(false, message))
                                }
                            }
                        )
                    }
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                cont.resume(PurchaseResult(false, e.message ?: "Unknown fatal error"))
            }
        }


    actual suspend fun getCustomerInfo(): ToolioCustomerInfo =
        suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getCustomerInfoWith({
                cont.resume(ToolioCustomerInfo(false, listOf<String>()))
            }) { info: CustomerInfo ->
                val isActive = info.entitlements.active.isNotEmpty()
                val productIds = info.activeSubscriptions.toList()
                cont.resume(ToolioCustomerInfo(isActive, productIds))
            }
        }
}