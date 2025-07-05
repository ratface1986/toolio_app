package ai.toolio.app.di

expect object SubscriptionManager {
    fun initialize(apiKey: String)
    suspend fun getAvailablePackages(): List<SubscriptionPackage>
    suspend fun purchase(packageId: String): PurchaseResult
    suspend fun getCustomerInfo(): ToolioCustomerInfo
}

data class SubscriptionPackage(
    val id: String,
    val title: String,
    val price: String,
    val period: String,
)

data class PurchaseResult(
    val success: Boolean,
    val errorMessage: String? = null
)

data class ToolioCustomerInfo(
    val isActive: Boolean,
    val activeProductIds: List<String>
)