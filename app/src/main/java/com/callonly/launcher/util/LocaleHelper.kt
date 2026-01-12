package com.callonly.launcher.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun applyLocale(context: Context): ContextWrapper {
        val prefs = context.getSharedPreferences("callonly_prefs", Context.MODE_PRIVATE)
        val sysLocale = android.content.res.Resources.getSystem().configuration.locales[0]
        val defaultLang = if (sysLocale.language == "fr") "fr" else "en"
        val lang = prefs.getString("language", defaultLang) ?: defaultLang
        val locale = if (lang == "en") Locale.ENGLISH else Locale("fr")
        
        val config = Configuration(context.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        val updated = context.createConfigurationContext(config)
        return ContextWrapper(updated)
    }
}
