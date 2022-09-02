package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.EditorPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.position.EditorPosition
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.mergeToPair
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.shape.Rectangle
import rx.subjects.BehaviorSubject
import tornadofx.c
import tornadofx.getValue
import tornadofx.setValue
import java.util.*
import kotlin.math.min

class Caret(startPosition: EditorPosition) : Rectangle(), Disposable {

    companion object {

        private val visibleTick = BehaviorSubject.create(false)

        init {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    visibleTick.onNext(!visibleTick.value)
                }
            }, 0L, 500L)
        }
    }

    val positionProperty = SimpleObjectProperty(startPosition)
    var position by positionProperty

    private var blinking: Int = 3

    override val disposer = DisposeSubject.create()

    init {
        fill = c("#000")
        visibleTick
            .takeUntil(disposer)
            .filter {
                if (blinking == 0) return@filter true
                blinking--
                return@filter false
            }
            .subscribe { isVisible = it }

        y = 0.0
        width = 2.0
        positionProperty.toRx(disposer)
            .switchMap { it.inlinePosition() }
            .mergeToPair { it.part.symbolBounds(it.inPartPosition) }
            .takeUntil(disposer)
            .subscribe { (position, bounds) ->
                height = bounds.height
                x = bounds.minX
                val t = this
                if(parent !== position.part) {
                    remove()
                    position.part.children.add(this)
                }
            }
    }

    override fun dispose() {
        super.dispose()
        remove()
    }

    fun remove() {
        (parent as EditorPart?)?.children?.remove(this)
    }

    fun up() {
        skipBlinking()
        move {
            if (line.prev == null) {
                return@move EditorPosition(line, 0)
            }

            return@move EditorPosition(line.prev!!, column)
        }
    }

    fun down() {
        skipBlinking()
        move {
            if (line.next == null) {
                return@move EditorPosition(line, line.end().getLength() - 1)
            }

            return@move EditorPosition(line.next!!, column)
        }
    }

    fun move(moving: EditorPosition.() -> EditorPosition) {
        skipBlinking()
        position = position.transform(moving)
    }

    fun move(delta: Int) {
        skipBlinking()
        if (delta == 0) return
        position = position.transform {
            var newLine = line
            var newSymbolPosition = getTrueColumn()
            var d = delta + newSymbolPosition
            newSymbolPosition = 0

            if (d > 0) {
                while (d >= newLine.length) {
                    if (newLine.next == null) {
                        newSymbolPosition = newLine.length - 1
                        d = 0
                        break
                    }
                    d -= newLine.length
                    newLine = newLine.next!!
                    newSymbolPosition = 0
                }
                newSymbolPosition += d
            } else {
                while (d + newSymbolPosition < 0) {
                    if (newLine.prev == null) {
                        newSymbolPosition = 0
                        d = 0
                        break
                    }
                    d += newSymbolPosition + 1
                    newLine = newLine.prev!!
                    newSymbolPosition = newLine.length - 1
                }
                newSymbolPosition += d
            }
            return@transform EditorPosition(newLine, newSymbolPosition)
        }
    }

    fun skipBlinking() {
        isVisible = true
        blinking = min(blinking + 1, 3)
    }
}