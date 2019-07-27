package com.ivianuu.essentials.ui.compose.epoxy

/*

class ListItem(val key: Any, val block: WidgetComposition.() -> Unit)

inline fun ListItem(noinline block: WidgetComposition.() -> Unit) = ListItem(sourceLocation(), block)

inline fun WidgetComposition.List(children: List<ListItem>) {
    emitView<EpoxyRecyclerView>(
        update = {
            init {
                setControllerAndBuildModels(epoxyController {
                    children.forEach {
                        ComposeModel(it).addTo(this)
                    }
                })
            }
            set(children) { requestModelBuild() }
        }
    )
}

class ComposeModel(val item: ListItem) : SimpleModel(item.key) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        holder.root.cast<ViewGroup>().setViewContent {
            item.block(WidgetComposition(composer))
        }
    }


    override fun buildView(parent: ViewGroup): View = FrameLayout(parent.context).apply {
        layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

}*/