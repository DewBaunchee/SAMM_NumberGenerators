package by.varyvoda.numbergenerators.ui.view

import by.varyvoda.numbergenerators.domain.methods.*
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
import javafx.scene.layout.VBox
import tornadofx.*

class NumberGeneratorsView : View("Lab2") {

    override fun onDock() {
        super.onDock()
        primaryStage.isMaximized = true
    }

    private val methods = listOf(Lemer(), Equality(), Gaussian(), Exponential(), Gamma(), Triangle(), Simpson())

    private val activeMethod = SimpleObjectProperty<Method>()

    private val series = XYChart.Series<String, Number>()

    private lateinit var indicator: ProgressIndicator

    private lateinit var parametersBox: VBox

    private lateinit var mathField: TextField

    private lateinit var dispersionField: TextField

    private lateinit var standardDeviationField: TextField

    private lateinit var mathMethodField: TextField

    private lateinit var dispersionMethodField: TextField

    private lateinit var standardDeviationMethodField: TextField

    private lateinit var pField: TextField

    private lateinit var lField: TextField

    private lateinit var pairCheckField: TextField

    override val root = hbox {

        addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if (activeMethod.get() == null) return@addEventFilter
            if (event.code == KeyCode.ENTER) {
                run()
            } else if (event.code == KeyCode.R && event.isShiftDown) {
                activeMethod.get().random()
                run()
            }else if (event.code == KeyCode.B && event.isShiftDown) {
                activeMethod.get().best()
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
                parametersBox = vbox {
                    method.parameters().forEach { line ->
                        hbox {
                            children.setAll(line.onEach { it.hgrow = Priority.ALWAYS })
                        }
                    }
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
                        pairCheckField = createOutField(this, "(0.78539) π/4 <- ")
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
        pairCheckField.text = result.pairCheck.toString()
        series.data.setAll(result.intervals.map { XYChart.Data(it.range, it.frequency) })
        // 6524 5752 60289
        indicator.isVisible = false

        if(result.p != null && result.p > 50000) {
            print("")
        }
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