package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.PartedTextEditor
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.model.PartedText
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.mergeToPair
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.shape.Rectangle
import rx.subjects.BehaviorSubject
import tornadofx.*
import java.util.*

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

    private var blinking: Boolean = true

    private val destroy = DisposeSubject.create()

    init {
        fill = c("#000")
        visibleTick
            .takeUntil(destroy)
            .filter {
                if (blinking) return@filter true
                blinking = true
                return@filter false
            }
            .subscribe { isVisible = it }

        position.part.add(this)
        positionProperty.toRx()
            .mergeToPair { it.part.symbolBounds(it.symbolPosition) }
            .takeUntil(destroy)
            .subscribe { (position, bounds) ->
                width = 2.0
                height = bounds.height
                anchorpaneConstraints {
                    leftAnchor = bounds.minX - width / 2
                    topAnchor = 0
                }

                position.part.add(this)
            }
    }

    fun addText(text: String) {
        getTextPart().modify {
            it.insert(position.symbolPosition, text)
            return@modify true
        }
        move(text.length)
    }

    fun delete() {
        getTextPart().modify {
            if (position.symbolPosition == it.length - 1) return@modify false
            it.deleteCharAt(position.symbolPosition)
            return@modify true
        }
    }

    fun backspace() {
        if (
            getTextPart().modify {
                if (position.symbolPosition == 0) return@modify false
                it.deleteCharAt(position.symbolPosition - 1)
                return@modify true
            }
        ) left()
    }

    fun dispose() {
        destroy.emit()
        position.part.children.remove(this)
    }

    fun up() {
        skipBlinking()

    }

    fun down() {
        skipBlinking()
        if (position.line.next == null) return
        position = position.transform {
            if (line.next == null) {
                return@transform PartedTextEditor.Position(
                    line,
                    line.lastPart(),
                    line.lastPart().textPart.content.lastIndex
                )
            }
            return@transform PartedTextEditor.Position(
                line, part, symbolPosition
            )
        }
    }

    fun right() {
        move(1)
//        skipBlinking()
//        position = position.transform {
//            var currentPart: PartedTextEditor.Part = part
//
//            if (symbolPosition + 1 == currentPart.textPart.content.length) {
//                if(currentPart.next == null) {
//                    return@transform PartedTextEditor.Position(
//                        currentPart.line,
//                        currentPart,
//                        currentPart.textPart.content.lastIndex
//                    )
//                } else {
//                    currentPart = currentPart.next!!
//                    return@transform PartedTextEditor.Position(
//                        currentPart.line,
//                        currentPart,
//                        0
//                    )
//                }
//            }
//
//            return@transform PartedTextEditor.Position(line, currentPart, symbolPosition + 1)
//        }
    }

    fun left() {
        move(-1)
//        skipBlinking()
//        position = position.transform {
//            var currentPart: PartedTextEditor.Part = part
//
//            if (symbolPosition == 0) {
//                currentPart = if (currentPart.prev == null) currentPart else currentPart.prev!!
//                return@transform PartedTextEditor.Position(currentPart.line, currentPart, currentPart.textPart.content.lastIndex)
//            }
//
//            return@transform PartedTextEditor.Position(line, currentPart, symbolPosition - 1)
//        }
    }

    fun move(delta: Int) {
        skipBlinking()
        if(delta == 0) return
        position = position.transform {
            var newPart: PartedTextEditor.Part = part
            var newSymbolPosition = symbolPosition
            var d = delta + newSymbolPosition
            newSymbolPosition = 0

            if (d > 0) {
                while (d >= newPart.getLength()) {
                    d -= newPart.getLength()
                    if (newPart.next == null) {
                        newSymbolPosition = newPart.getLength() - 1
                        d = 0
                        break
                    }
                    newPart = newPart.next!!
                    newSymbolPosition = 0
                }
                newSymbolPosition += d
            } else {
                while (d + newSymbolPosition < 0) {
                    d += newSymbolPosition + 1
                    if (newPart.prev == null) {
                        newSymbolPosition = 0
                        d = 0
                        break
                    }
                    newPart = newPart.prev!!
                    newSymbolPosition = newPart.getLength() - 1
                }
                newSymbolPosition += d
            }
            return@transform PartedTextEditor.Position(newPart.line, newPart, newSymbolPosition)
        }
    }

    private fun skipBlinking() {
        isVisible = true
        blinking = false
    }

    private fun getTextPart(): PartedText.Part {
        return position.part.textPart
    }
}