package by.varyvoda.ademis.app.desktop.ui.behaviour.keyevent

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.util.*

class KeyEventHandlerHolder<V>(private val defaultHandler: V?) {

    private val handlers = HashMap<KeyCode, List<V?>>()

    fun register(keyCode: KeyCode, handlers: Map<MetaButton, V>): KeyEventHandlerHolder<V> {
        val metaButtons = MetaButton.values()
        this.handlers[keyCode] = List(metaButtons.size) { handlers[metaButtons[it]] }
        return this
    }

    fun register(keyCode: KeyCode, handler: V): KeyEventHandlerHolder<V> {
        register(keyCode, mapOf(MetaButton.NONE to handler))
        return this
    }

    fun getSuitableHandler(keyEvent: KeyEvent): V {
        val handlers = handlers[keyEvent.code]
        return Optional.ofNullable(
            handlers?.get(MetaButton.from(keyEvent).ordinal)
                ?: handlers?.get(MetaButton.NONE.ordinal)
                ?: defaultHandler
        ).orElseThrow { NoSuchElementException("No handler for event: $keyEvent") }
    }

    enum class MetaButton {
        NONE, CTRL, ALT, SHIFT;

        companion object {
            fun from(keyEvent: KeyEvent): MetaButton {
                if (keyEvent.isControlDown) return CTRL
                if (keyEvent.isAltDown) return ALT
                if (keyEvent.isShiftDown) return SHIFT
                return NONE
            }
        }
    }
}