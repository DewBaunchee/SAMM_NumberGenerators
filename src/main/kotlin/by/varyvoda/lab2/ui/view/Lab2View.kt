package by.varyvoda.lab2.ui.view

import by.varyvoda.lab2.domain.methods.Exponential
import by.varyvoda.lab2.domain.methods.Gaussian
import by.varyvoda.lab2.domain.methods.Lemer
import by.varyvoda.lab2.domain.methods.Method
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

class Lab2View : View("Lab2") {

    override fun onDock() {
        super.onDock()
//        primaryStage.width = 800.0
//        primaryStage.height = 700.0
        primaryStage.isMaximized = true
    }

    private val methods = listOf(Lemer(), Gaussian(), Exponential())

    private val activeMethod = SimpleObjectProperty<Method>()

    private val series = XYChart.Series<String, Number>()

    private lateinit var indicator: ProgressIndicator

    private lateinit var parametersBox: HBox

    private lateinit var mathField: TextField

    private lateinit var dispersionField: TextField

    private lateinit var standardDeviationField: TextField

    private lateinit var mathMethodField: TextField

    private lateinit var dispersionMethodField: TextField

    private lateinit var standardDeviationMethodField: TextField

    private lateinit var pField: TextField

    private lateinit var lField: TextField

    override val root = hbox {

        addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if (activeMethod.get() == null) return@addEventFilter
            if (event.code == KeyCode.ENTER) {
                run()
            } else if (event.code == KeyCode.R && event.isShiftDown) {
                activeMethod.get().random()
                run()
            }
        }

        vbox {
            paddingAll = 20
            spacing = 10.0
            style {
                borderColor += box(c("#AAA"))
                borderWidth += box(0.px, 1.px, 0.px, 0.px)
            }
            methods.forEach { method ->
                button {
                    style {
                        fontSize = 20.px
                    }
                    text = method.name()
                    onAction = EventHandler { openMethod(method) }
                }
            }
        }
        vbox {
            hgrow = Priority.ALWAYS
            activeMethod.addListener { _, _, method ->
                children.clear()
                if (method == null) return@addListener
                parametersBox = hbox {
                    children.setAll(method.parameters().onEach { it.hgrow = Priority.ALWAYS })
                }
                hbox {
                    vgrow = Priority.ALWAYS
                    stackpane {
                        hgrow = Priority.ALWAYS
                        barchart(method.name(), CategoryAxis(), NumberAxis()) {
                            animated = false
                            isLegendVisible = false
                            data.add(series)
                        }
                        indicator = progressindicator {
                            isVisible = false
                        }
                    }
                }
                vbox {
                    hbox {
                        mathField = createOutField(this, "m*")
                        dispersionField = createOutField(this, "D*")
                        standardDeviationField = createOutField(this, "σ*")
                    }
                    hbox {
                        mathMethodField = createOutField(this, "m")
                        dispersionMethodField = createOutField(this, "D")
                        standardDeviationMethodField = createOutField(this, "σ")
                    }
                    hbox {
                        pField = createOutField(this, "P")
                        lField = createOutField(this, "L")
                    }
                }
            }
        }
        activeMethod.set(methods.first())
    }

    private fun run() {
        indicator.isVisible = true
        indicator.progress = ProgressIndicator.INDETERMINATE_PROGRESS

        val result = activeMethod.get().summaryStatistics()
        mathField.text = result.math.first.toString()
        dispersionField.text = result.dispersion.first.toString()
        standardDeviationField.text = result.standardDeviation.first.toString()

        mathMethodField.text = result.math.second.toString()
        dispersionMethodField.text = result.dispersion.second.toString()
        standardDeviationMethodField.text = result.standardDeviation.second.toString()

        pField.text = result.p?.toString() ?: "-"
        lField.text = result.l?.toString() ?: "-"
        series.data.setAll(result.intervals.map { XYChart.Data(it.range, it.frequency) })

        indicator.isVisible = false
    }

    private fun openMethod(method: Method) {
        activeMethod.set(method)
    }

    private fun createOutField(box: HBox, name: String): TextField {
        var textField: TextField? = null
        box.hbox {
            hgrow = Priority.ALWAYS
            alignment = Pos.CENTER
            paddingAll = 10
            spacing = 10.0
            label(name) { }
            textField = textfield { }
        }
        return textField!!
    }
}