package by.varyvoda.numbergenerators.domain.input

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import tornadofx.*
import kotlin.random.Random

abstract class NumberInput : VBox() {

    abstract var value: Number

    abstract fun random()
}

class IntegerInput(val name: String, val min: Int, val max: Int) : NumberInput() {

    val valueProperty = SimpleIntegerProperty(min)
    override var value: Number by valueProperty

    init {
        textfield {
            promptText = name
            valueProperty.addListener { _, _, new ->
                text = new.toString()
            }
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

    override fun random() {
        value = Random.nextInt(min, max)
    }
}

class DoubleInput(val name: String, val min: Double, val max: Double) : NumberInput() {

    val valueProperty = SimpleDoubleProperty(min)
    override var value: Number by valueProperty

    init {
        textfield {
            promptText = name
            valueProperty.addListener { _, _, new ->
                text = new.toString()
            }
            addEventFilter(KeyEvent.KEY_PRESSED) {
                if (it.code != KeyCode.ENTER) return@addEventFilter

                try {
                    value = text.toDouble()
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

    override fun random() {
        value = Random.nextDouble(min, max)
    }
}

