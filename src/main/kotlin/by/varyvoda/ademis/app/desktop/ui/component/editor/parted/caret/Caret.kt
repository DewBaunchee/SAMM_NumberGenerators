package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.PartedTextEditor
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.mergeToPair
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.shape.Rectangle
import rx.subjects.BehaviorSubject
import tornadofx.add
import tornadofx.c
import tornadofx.getValue
import tornadofx.setValue
import java.util.*
import kotlin.math.min

class Caret(startPosition: PartedTextEditor.Position) : Rectangle() {

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

    private val destroy = DisposeSubject.create()

    init {
        fill = c("#000")
        visibleTick
            .takeUntil(destroy)
            .filter {
                if (blinking == 0) return@filter true
                blinking--
                return@filter false
            }
            .subscribe { isVisible = it }

        position.part().add(this)
        positionProperty.toRx()
            .switchMap { it.inlinePosition() }
            .mergeToPair { it.part.symbolBounds(it.inPartPosition) }
            .takeUntil(destroy)
            .subscribe { (position, bounds) ->
                width = 2.0
                height = bounds.height

                x = bounds.minX
                y = 0.0

                position.part.add(this)
            }
    }

    fun dispose() {
        destroy.emit()
        position.part().children.remove(this)
    }

    fun up() {
        skipBlinking()
        move {
            if (line.prev == null) {
                return@move PartedTextEditor.Position(line, 0)
            }

            return@move PartedTextEditor.Position(line.prev!!, column)
        }
    }

    fun down() {
        skipBlinking()
        move {
            if (line.next == null) {
                return@move PartedTextEditor.Position(line, line.end.getLength() - 1)
            }

            return@move PartedTextEditor.Position(line.next!!, column)
        }
    }

    fun move(moving: PartedTextEditor.Position.() -> PartedTextEditor.Position) {
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
            return@transform PartedTextEditor.Position(newLine, newSymbolPosition)
        }
    }

    fun skipBlinking() {
        isVisible = true
        blinking = min(blinking + 1, 3)
    }
}