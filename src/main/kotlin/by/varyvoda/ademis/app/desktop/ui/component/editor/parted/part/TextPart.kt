package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.symbol.Symbol
import by.varyvoda.ademis.app.desktop.util.rx.combineLatest
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import rx.Observable
import tornadofx.*

open class TextPart(initialContent: String = "") : EditorPart() {

    val contentProperty = SimpleStringProperty(initialContent)
    var content: String by contentProperty

    protected val symbols: ObservableList<Symbol> = FXCollections.observableArrayList()

    private val textBox = hbox {
        anchorpaneConstraints {
            leftAnchor = 0.0
            topAnchor = 0.0
            rightAnchor = 0.0
            bottomAnchor = 0.0
        }
        spacing = 1.0
    }

    init {
        textBox.children.bind(symbols) { it }
        contentProperty.toRx(disposer).subscribe { handleContent(it) }
    }

    protected fun createSymbol(char: Char, index: Int): Symbol {
        val symbol = Symbol(char, this, index)
        symbol.style {
            fontFamily = "Courier New"
            fontSize = 100.px
        }
        return symbol
    }

    protected open fun handleContent(content: String) {
        symbols.setAll(content.toCharArray().mapIndexed { index, it -> createSymbol(it, index) })
        length.set(content.length)
    }

    fun symbolBounds(position: Int): Observable<Bounds> {
        return textBox.children.toRx()
            .filter { position < it.list.size }
            .map { it.list[position] as Symbol }
            .switchMap {
                combineLatest(
                    it.layoutXProperty().toRx(),
                    it.layoutYProperty().toRx(),
                    it.layoutBoundsProperty().toRx(),
                )
            }.map { BoundingBox(it.first, it.second, it.third.width, it.third.height) }
    }

    fun split(at: Int) {
        val left = content.substring(0, at)
        val right = content.substring(at)

        content = left
        addAfter(TextPart(right))
    }

    fun modify(modifier: (contentBuilder: StringBuilder) -> Unit) {
        val contentBuilder = StringBuilder(content)
        modifier(contentBuilder)
        content = contentBuilder.toString()
    }

    override fun isText(): Boolean {
        return true
    }

    override fun toString(): String {
        return content
    }

    fun getClosestSymbol(screenX: Double): Symbol {
        return symbols.stream().filter { screenX < it.localToScreen(it.boundsInLocal).maxX }.findFirst().orElse(symbols.last())
    }

    fun getSymbols(): List<Symbol> {
        return symbols.asUnmodifiable()
    }
}