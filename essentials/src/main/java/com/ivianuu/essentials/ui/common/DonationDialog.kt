/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.ivianuu.essentials.R
import com.ivianuu.essentials.R2
import com.ivianuu.essentials.injection.ForApp
import com.ivianuu.essentials.ui.base.BaseViewModelDialogFragment
import com.ivianuu.essentials.ui.epoxy.KtEpoxyHolder
import com.ivianuu.essentials.util.ext.listEpoxyController
import com.ivianuu.essentials.util.ext.main
import com.ivianuu.kommonextensions.toast
import com.ivianuu.rxplaybilling.RxPlayBilling
import com.ivianuu.rxplaybilling.model.Response
import com.ivianuu.traveler.keys.DialogFragmentKey
import com.ivianuu.traveler.keys.requireKey
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.item_sku.*
import javax.inject.Inject

/**
 * Dialog for donations
 */
class DonationDialog : BaseViewModelDialogFragment<DonationViewModel>() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val key = requireKey<DonationKey>()

        viewModel.setSkus(key.skus)

        val controller = listEpoxyController<SkuDetails> {
            sku {
                id(it.sku)
                sku(it)
                activity(requireActivity())
                viewModel(viewModel)
            }
        }

        viewModel.dismiss
            .main()
            .subscribe { dismiss() }
            .addTo(disposables)

        viewModel.skus
            .main()
            .subscribe(controller::setData)
            .addTo(disposables)

        return MaterialDialog.Builder(requireContext())
            .title(R.string.dialog_title_donation)
            .adapter(controller.adapter, LinearLayoutManager(requireContext()))
            .negativeText(R.string.action_cancel)
            .build()
    }

}

/**
 * Key for the [DonationDialog]
 */
@Parcelize
data class DonationKey(val skus: List<String>) : DialogFragmentKey(), Parcelable {
    override fun createDialogFragment() = DonationDialog()
}

@EpoxyModelClass(layout = R2.layout.item_sku)
abstract class SkuModel : EpoxyModelWithHolder<KtEpoxyHolder>() {

    @EpoxyAttribute lateinit var sku: SkuDetails
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) lateinit var activity: Activity
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) lateinit var viewModel: DonationViewModel

    override fun bind(holder: KtEpoxyHolder) {
        super.bind(holder)
        with(holder) {
            val titleText = sku.title.replace(" (${getAppName(containerView.context)})", "")
            title.text = titleText
            desc.text = sku.description
            price.text = sku.price

            containerView.setOnClickListener { viewModel.skuClicked(activity, sku) }
        }
    }

    private companion object {

        private var appName = ""

        private fun getAppName(context: Context): String {
            if (appName.isEmpty()) {
                appName = context.applicationInfo.loadLabel(context.packageManager).toString()
            }
            return appName
        }

    }
}

/**
 * View model for the [DonationDialog]
 */
@SuppressLint("StaticFieldLeak")
class DonationViewModel @Inject constructor(
    @ForApp private val context: Context
) : RxViewModel() {

    val dismiss: Observable<Unit>
        get() = _dismiss
    private val _dismiss = BehaviorSubject.create<Unit>()

    val skus: Observable<List<SkuDetails>>
        get() = _skus
    private val _skus = BehaviorSubject.create<List<SkuDetails>>()

    private val billingClient = RxPlayBilling.create(context)

    private var skusSet = false

    override fun onCleared() {
        billingClient.release()
        super.onCleared()
    }

    fun setSkus(skus: List<String>) {
        if (skusSet) return
        skusSet = true

        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(skus)
            .build()

        billingClient.querySkuDetails(params)
            .main()
            .subscribeBy(
                onSuccess = {
                    if (it.success()) {
                        _skus.onNext(
                            it.skuDetailsList()!!
                                .distinctBy(SkuDetails::getSku)
                                .sortedBy(SkuDetails::getPriceAmountMicros)
                        )
                    } else {
                        dismissWithMessage(R.string.msg_error_donate)
                    }
                },
                onError = {
                    it.printStackTrace()
                    dismissWithMessage(R.string.msg_error_donate)
                }
            )
    }

    fun skuClicked(activity: Activity, sku: SkuDetails) {
        val params = BillingFlowParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSku(sku.sku)
            .build()

        billingClient.launchBillingFlow(activity, params)
            .map(Response::success)
            .doOnSuccess { if (!it) throw IllegalStateException() }
            .flatMap { billingClient.purchaseUpdates().take(1).singleOrError() }
            .map(Response::success)
            .onErrorReturn { false }
            .main()
            .subscribeBy(
                onSuccess = {
                    dismissWithMessage(if (it) R.string.msg_success_donate else R.string.msg_error_donate)
                },
                onError = {
                    it.printStackTrace()
                    dismissWithMessage(R.string.msg_error_donate)
                }
            )
            .addTo(disposables)
    }

    private fun dismissWithMessage(resId: Int) {
        context.toast(resId)
        _dismiss.onNext(Unit)
    }
}