package by.varyvoda.ademis.app.desktop.ui.util

import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.add
import tornadofx.addChildIfPossible

fun <N : Node> execBuilder(parent: EventTarget, buildable: N, builder: N.() -> Unit): N {
    parent.addChildIfPossible(buildable)
    builder(buildable)
    return buildable
}

fun <N : Node> EventTarget.addAndReturn(node: N): N {
    add(node)
    return node
}