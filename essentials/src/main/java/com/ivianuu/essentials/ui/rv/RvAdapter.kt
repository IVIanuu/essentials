package com.ivianuu.essentials.ui.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.*
import java.util.concurrent.Executor

/**
 * @author Manuel Wrage (IVIanuu)
 */
class RvAdapter(
    private val controller: RvController,
    diffingExecutor: Executor
) : RecyclerView.Adapter<RvViewHolder>() {

    private val helper = AsyncListDiffer<RvModel<*>>(
        AdapterListUpdateCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK)
            .setBackgroundThreadExecutor(diffingExecutor)
            .build()
    )

    private val models get() = helper.currentList

    var spanCount = 1

    val spanSizeLookup: GridLayoutManager.SpanSizeLookup by lazy(LazyThreadSafetyMode.NONE) {
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                models[position].getSpanSize(spanCount, position, models.size)
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        val model = models.first { it.viewType == viewType }
        val view = model.buildView(parent)
        return RvViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
        val model = models[position]
        holder.bind(model)
    }

    override fun onViewRecycled(holder: RvViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun getItemId(position: Int) = models[position].id

    override fun getItemCount() = models.size

    override fun getItemViewType(position: Int) = models[position].viewType

    override fun onViewAttachedToWindow(holder: RvViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.model!!.onViewAttachedFromWindow(holder.holder)
    }

    override fun onViewDetachedFromWindow(holder: RvViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.model!!.onViewDetachedFromWindow(holder.holder)
    }

    override fun onFailedToRecycleView(holder: RvViewHolder) =
        holder.model!!.onFailedToRecycleView(holder.holder)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        controller.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        controller.onDetachedFromRecyclerView(recyclerView)
    }

    fun setModels(models: List<RvModel<*>>) {
        helper.submitList(models)
    }

    private companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RvModel<*>>() {
            override fun areItemsTheSame(oldItem: RvModel<*>, newItem: RvModel<*>) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RvModel<*>, newItem: RvModel<*>) =
                oldItem == newItem
        }
    }
}