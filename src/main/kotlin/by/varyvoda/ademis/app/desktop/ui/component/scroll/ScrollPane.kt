package by.varyvoda.ademis.app.desktop.ui.component.scroll

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.combineLatest
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.AnchorPane
import rx.Observable
import tornadofx.anchorpaneConstraints
import tornadofx.getValue
import tornadofx.setValue

open class ScrollPane : AnchorPane(), Disposable {

    val horizontal = scrollBar(ScrollBar.Position.HORIZONTAL) {
        anchorpaneConstraints {
            leftAnchor = 0.0
            bottomAnchor = 0.0
            rightAnchor = 0.0
        }
    }

    val vertical = scrollBar(ScrollBar.Position.VERTICAL) {
        anchorpaneConstraints {
            topAnchor = 0.0
            rightAnchor = 0.0
            bottomAnchor = 0.0
        }
    }

    val scrollableProperty = SimpleObjectProperty<Node?>()
    var scrollable by scrollableProperty

    final override val disposer = DisposeSubject.create()

    override fun dispose() {
        vertical.dispose()
        horizontal.dispose()
        super.dispose()
    }

    init {
        heightProperty().toRx(disposer).subscribe { vertical.viewSize = it }
        widthProperty().toRx(disposer).subscribe { horizontal.viewSize = it }

        combineLatest(scrollableProperty.toRx(disposer), children.toRx(disposer))
            .subscribe { (scrollable, children) ->
                if (isOrderCorrect(scrollable, children.list)) return@subscribe
                val ordered = mutableListOf<Node?>()
                if (scrollable != null) ordered.add(scrollable)
                ordered.add(horizontal)
                ordered.add(vertical)
                this.children.setAll(ordered)
            }

        scrollableProperty.toRx(disposer).switchMap { node ->
            if (node == null)
                Observable.empty<Any>()
            else combineLatest(
                node.boundsInParentProperty().toRx().doOnNext { vertical.scrollSize = it.height },
                node.boundsInParentProperty().toRx().doOnNext { horizontal.scrollSize = it.width },
            )
        }.subscribe()

        vertical.positionProperty.toRx(disposer).subscribe {
            scrollable?.anchorpaneConstraints {
                topAnchor = -it
            }
        }
        horizontal.positionProperty.toRx(disposer).subscribe {
            scrollable?.anchorpaneConstraints {
                leftAnchor = -it
            }
        }
        vertical.viewOrder = 1000.0

        addEventHandler(ScrollEvent.SCROLL) {
            vertical.move(-it.deltaY)
            horizontal.move(-it.deltaX)
        }
    }

    private fun isOrderCorrect(scrollable: Node?, children: List<Node>): Boolean {
        var index = 0
        if (scrollable != null && children.getOrNull(index++) !== scrollable) return false
        if (children.getOrNull(index++) != horizontal) return false
        if (children.getOrNull(index) != vertical) return false

        return true
    }
}

fun EventTarget.scrollPane(builder: ScrollPane.() -> Unit) = execBuilder(this, ScrollPane(), builder)