package com.incomingcallonly.launcher.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun applyLocale(context: Context): ContextWrapper {
        val lang = try {
            val masterKey = androidx.security.crypto.MasterKey.Builder(context)
                .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                context,
                "secret_incomingcallonly_prefs",
                masterKey,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val sysLocale = android.content.res.Resources.getSystem().configuration.locales[0]
            val defaultLang = if (sysLocale.language == "fr") "fr" else "en"
            prefs.getString("language", defaultLang) ?: defaultLang
        } catch (e: Exception) {
            e.printStackTrace()
             val sysLocale = android.content.res.Resources.getSystem().configuration.locales[0]
            if (sysLocale.language == "fr") "fr" else "en"
        }

        val locale = if (lang == "en") Locale.ENGLISH else Locale("fr")
        
        val config = Configuration(context.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        val updated = context.createConfigurationContext(config)
        return ContextWrapper(updated)
    }
}
