package ai.toolio.app.di

import android.content.Intent
import androidx.core.net.toUri


actual fun openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AppSessions.appContext.startActivity(intent)
}