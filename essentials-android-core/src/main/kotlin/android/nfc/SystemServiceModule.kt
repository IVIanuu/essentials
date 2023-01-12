package android.nfc
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun nfcManager(context: Context): NfcManager =
    context.getSystemService(NfcManager::class.java)
}
