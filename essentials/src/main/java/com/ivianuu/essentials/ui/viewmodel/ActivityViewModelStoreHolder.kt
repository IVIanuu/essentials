package com.ivianuu.essentials.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

private val viewModelStores = mutableMapOf<String, ViewModelStore>()

/**
 * Activity view model store holder
 */
class ActivityViewModelStoreHolder(
    private val activity: Activity,
    private val savedInstanceState: Bundle?
) : ViewModelStoreHolder {

    override lateinit var viewModels: ViewModelStore

    private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            // save the instance id and view models state
            if (this@ActivityViewModelStoreHolder.activity == activity) {
                outState.putString(KEY_INSTANCE_ID, instanceId)
                outState.putBundle(KEY_VIEW_MODEL_STORE, viewModels.saveInstanceState())
            }
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            // remove and clear the store if we don't change configurations
            if (this@ActivityViewModelStoreHolder.activity == activity
                && !activity.isChangingConfigurations
            ) {
                viewModelStores.remove(instanceId)
                viewModels.clear()
                activity.application.unregisterActivityLifecycleCallbacks(this)
            }
        }

    }

    private val instanceId: String

    init {
        // get or create the instance id
        instanceId = if (savedInstanceState != null) {
            savedInstanceState.getString(KEY_INSTANCE_ID)!!
        } else {
            UUID.randomUUID().toString()
        }

        // get and restore the view model store
        viewModels = viewModelStores.getOrPut(instanceId) { ViewModelStore() }
        viewModels.restoreInstanceState(savedInstanceState)

        // register lifecycle callbacks
        activity.application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    private companion object {
        private const val KEY_INSTANCE_ID = "ActivityViewModelStoreHolder.instanceId"
        private const val KEY_VIEW_MODEL_STORE = "ActivityViewModelStoreHolder.viewModelStore"
    }
}