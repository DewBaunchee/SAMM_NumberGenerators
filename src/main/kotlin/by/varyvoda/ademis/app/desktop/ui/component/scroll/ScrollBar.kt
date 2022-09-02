package by.varyvoda.ademis.app.desktop.ui.component.scroll

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.combineLatest
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventTarget
import javafx.scene.layout.AnchorPane
import tornadofx.*

abstract class ScrollBar : AnchorPane(), Disposable {

    enum class Position { HORIZONTAL, VERTICAL }

    val positionProperty = SimpleDoubleProperty()
    var position by positionProperty

    val scrollSizeProperty = SimpleDoubleProperty()
    var scrollSize by scrollSizeProperty

    val viewSizeProperty = SimpleDoubleProperty()
    var viewSize by viewSizeProperty

    final override val disposer = DisposeSubject.create()

    protected val thumb = rectangle { }

    init {
        addChildIfPossible(thumb)
        combineLatest(
            positionProperty.toRx(),
            scrollSizeProperty.toRx(),
            viewSizeProperty.toRx(),
            heightProperty().toRx(),
        )
            .takeUntil(disposer)
            .subscribe {
                if (position < 0.0) {
                    position = 0.0
                    return@subscribe
                } else if (scrollSize <= viewSize) {
                    position = 0.0
                    thumb.isVisible = false
                    return@subscribe
                } else if (position + viewSize > scrollSize) {
                    position = scrollSize - viewSize
                    return@subscribe
                }
                thumb.isVisible = true

                layoutThumb()
            }
    }

    protected abstract fun layoutThumb()

    fun move(delta: Double) {
        position += delta
    }

    class Horizontal : ScrollBar() {

        init {
            prefHeight = 10.0
            heightProperty().toRx(disposer).subscribe { thumb.height = it }
        }

        override fun layoutThumb() {
            thumb.width = width * viewSize / scrollSize
            thumb.anchorpaneConstraints {
                leftAnchor = width * position / scrollSize
            }
        }
    }

    class Vertical : ScrollBar() {

        init {
            prefWidth = 10.0
            widthProperty().toRx(disposer).subscribe { thumb.width = it }
        }

        override fun layoutThumb() {
            thumb.height = height * viewSize / scrollSize
            thumb.anchorpaneConstraints {
                topAnchor = height * position / scrollSize
            }
        }
    }
}

fun EventTarget.scrollBar(position: ScrollBar.Position, builder: ScrollBar.() -> Unit) =
    execBuilder(
        this,
        when (position) {
            ScrollBar.Position.VERTICAL -> ScrollBar.Vertical()
            ScrollBar.Position.HORIZONTAL -> ScrollBar.Horizontal()
        },
        builder
    )