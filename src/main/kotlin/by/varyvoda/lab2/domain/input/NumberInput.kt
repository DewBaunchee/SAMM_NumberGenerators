package by.varyvoda.lab2.domain.input

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import tornadofx.*

class NumberInput(val name: String, val min: Int, val max: Int) : VBox() {

    val valueProperty = SimpleIntegerProperty(min)
    var value by valueProperty

    init {
        textfield {
            promptText = name
            textProperty().bind(valueProperty.asString())
            addEventFilter(KeyEvent.KEY_PRESSED) {
                if (it.code != KeyCode.ENTER) return@addEventFilter

                try {
                    value = text.toInt()
                } catch (e: Exception) {
                    text = value.toString()
                }
            }
        }
    }

    val slider = slider(min, max) { }

    init {
        paddingAll = 10
        spacing = 10.0
        slider.valueProperty().bindBidirectional(valueProperty)
    }

    fun random() {
        value = (min..max).random()
    }
}
