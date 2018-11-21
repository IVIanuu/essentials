package com.ivianuu.essentials.ui.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Manuel Wrage (IVIanuu)
 */
class RvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var model: RvModel<RvHolder>? = null
        private set

    lateinit var holder: RvHolder
        private set
    private var holderCreated = false

    fun bind(model: RvModel<*>) {
        this.model = model as RvModel<RvHolder>

        if (!holderCreated) {
            holderCreated = true
            holder = model.createNewHolder()
            holder.bindView(itemView)
        }

        model.bind(holder)
    }

    fun unbind() {
        model?.unbind(holder)
        model = null
    }

}