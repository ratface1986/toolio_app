package ai.toolio.app.di

actual object SubscriptionManager {
    actual fun initialize(apiKey: String) {
    }

    actual suspend fun getAvailablePackages(): List<SubscriptionPackage> {
        TODO("Not yet implemented")
    }

    actual suspend fun purchase(packageId: String): PurchaseResult {
        TODO("Not yet implemented")
    }

    actual suspend fun getCustomerInfo(): CustomerInfo {
        TODO("Not yet implemented")
    }
}