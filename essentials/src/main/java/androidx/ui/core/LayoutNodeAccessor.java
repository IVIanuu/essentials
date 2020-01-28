package androidx.ui.core;

public class LayoutNodeAccessor {

    public static long getMeasureIteration(LayoutNode node) {
        //noinspection KotlinInternalInJava
        return node.getMeasureIteration$ui_platform_release();
    }

}
