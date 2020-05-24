/*
 * The MIT License (MIT)
 *
 * Copyright 2019 Yaroslav Berezanskyi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.taptalk.TapTalk.Manager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.LocaleList
import java.util.*

class TapLocaleManager private constructor() {
    /**
     * Creates and sets a [Locale] using language, country and variant information.
     *
     * See the [Locale] class description for more information about valid language, country
     * and variant values.
     */
    @JvmOverloads
    fun setLocale(context: Context, language: String, country: String = "", variant: String = "") {
        setLocale(context, Locale(language, country, variant))
    }

    /**
     * Sets a [locale] which will be used to localize all data coming from [Resources] class.
     */
    fun setLocale(context: Context, locale: Locale) {
        update(context, locale)
    }

    /**
     * Returns the active [Locale].
     */
    fun getLocale(): Locale {
        return locale
    }

    /**
     * Returns a language code which is a part of the active [Locale].
     *
     * Deprecated ISO language codes "iw", "ji", and "in" are converted
     * to "he", "yi", and "id", respectively.
     */
    fun getLanguage(): String {
        return verifyLanguage(getLocale().language)
    }

    private fun verifyLanguage(language: String): String {
        // get rid of deprecated language tags
        return when (language) {
            "iw" -> "he"
            "ji" -> "yi"
            "in" -> "id"
            else -> language
        }
    }

    internal class LocaleActivityLifecycleCallbacks(private val localeManager: TapLocaleManager) : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            localeManager.setLocaleInternal(activity)
            localeManager.resetActivityTitle(activity)
        }

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }

    internal class LocaleApplicationCallbacks(private val context: Context,
                                              private val localeManager: TapLocaleManager) : ComponentCallbacks {

        override fun onConfigurationChanged(newConfig: Configuration) {
            localeManager.setLocaleInternal(context)
        }

        override fun onLowMemory() {}
    }

    private fun setUp(application: Application) {
        application.registerActivityLifecycleCallbacks(LocaleActivityLifecycleCallbacks(this))
        application.registerComponentCallbacks(LocaleApplicationCallbacks(application, this))
    }

    internal fun setLocaleInternal(context: Context) {
        update(context, locale)
    }

    private fun update(context: Context, locale: Locale) {
        updateResources(context, locale)
        val appContext = context.applicationContext
        if (appContext !== context) {
            updateResources(appContext, locale)
        }
    }

    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, locale: Locale) {
        Locale.setDefault(locale)

        val res = context.resources
        val current = res.configuration.getLocaleCompat()

        if (current == locale) return

        val config = Configuration(res.configuration)
        when {
            isAtLeastSdkVersion(VERSION_CODES.N) -> setLocaleForApi24(config, locale)
            isAtLeastSdkVersion(VERSION_CODES.JELLY_BEAN_MR1) -> config.setLocale(locale)
            else -> config.locale = locale
        }
        res.updateConfiguration(config, res.displayMetrics)
    }

    @SuppressLint("NewApi")
    private fun setLocaleForApi24(config: Configuration, locale: Locale) {
        // bring the target locale to the front of the list
        val set = linkedSetOf(locale)

        val defaultLocales = LocaleList.getDefault()
        val all = List<Locale>(defaultLocales.size()) { defaultLocales[it] }
        // append other locales supported by the user
        set.addAll(all)

        config.setLocales(LocaleList(*set.toTypedArray()))
    }

    internal fun resetActivityTitle(activity: Activity) {
        try {
            val pm = activity.packageManager
            val info = pm.getActivityInfo(activity.componentName, GET_META_DATA)
            if (info.labelRes != 0) {
                activity.setTitle(info.labelRes)
            }
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
    }

    @Suppress("DEPRECATION")
    private fun Configuration.getLocaleCompat(): Locale {
        return if (isAtLeastSdkVersion(VERSION_CODES.N)) locales.get(0) else locale
    }

    private fun isAtLeastSdkVersion(versionCode: Int): Boolean {
        return Build.VERSION.SDK_INT >= versionCode
    }

    companion object {

        private lateinit var instance: TapLocaleManager
        private lateinit var locale: Locale

        /**
         * Returns the global instance of [TapLocaleManager] created via init method.
         *
         * @throws IllegalStateException if it was not initialized properly.
         */
        @JvmStatic
        fun getInstance(): TapLocaleManager {
            check(::instance.isInitialized) { "TapLocaleManager should be initialized first" }
            return instance
        }

        /**
         * Creates and sets up the global instance using a provided language and the default store.
         */
        @JvmStatic
        fun setLocale(application: Application, defaultLanguage: String) {
            locale = Locale(defaultLanguage)
            if (::instance.isInitialized) {
                instance.setLocale(application, locale)
            } else {
                init(application, locale)
            }
        }

        /**
         * Creates and sets up the global instance.
         *
         * This method must be called before any calls to [TapLocaleManager] and may only be called once.
         */
        @JvmStatic
        fun init(application: Application, locale: Locale): TapLocaleManager {
            check(!::instance.isInitialized) { "Already initialized" }
            val localeManager = TapLocaleManager()
            localeManager.setUp(application)
            localeManager.setLocale(application, locale)
            instance = localeManager
            return localeManager
        }
    }
}
