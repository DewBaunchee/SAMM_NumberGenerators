package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.selection

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.Caret
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.position.EditorPosition
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.style.PartedTextEditorStyle.Companion.selected
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.symbol.Symbol
import by.varyvoda.ademis.app.desktop.util.func.isPresent
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.collections.FXCollections
import rx.Observable
import rx.subjects.BehaviorSubject
import tornadofx.addClass
import tornadofx.removeClass
import java.util.stream.Stream
import kotlin.streams.toList

class Selection : Disposable {

    override val disposer: DisposeSubject = DisposeSubject.create()

    private val symbols = FXCollections.observableArrayList<Symbol>()

    private val caret = BehaviorSubject.create<Caret?>()

    private var anchor: EditorPosition? = null

    init {
        symbols.toRx().takeUntil(disposer).subscribe { change ->
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.removed.forEach { it.removeClass(selected) }
                }
                if (change.wasAdded()) {
                    change.addedSubList.forEach { it.addClass(selected) }
                }
            }
        }
    }

    init {
        caret
            .takeUntil(disposer)
            .doOnNext { anchor = it?.position }
            .switchMap { it?.positionProperty?.toRx() ?: Observable.just(null) }
            .filter { isPresent(it) }
            .subscribe { position ->
                val anchorInlinePosition = anchor!!.getInlinePosition()
                val inlinePosition = position!!.getInlinePosition()

                var startSymbol = anchorInlinePosition.getSymbol()
                var endSymbol = inlinePosition.getSymbol()

                val positionComparison = startSymbol.comparePosition(endSymbol)
                if (positionComparison == 0) {
                    symbols.clear()
                    return@subscribe
                }
                if (positionComparison == 1) {
                    val temp = startSymbol
                    startSymbol = endSymbol
                    endSymbol = temp
                }
                symbols.setAll(
                    Stream.iterate(startSymbol) { it.next }
                        .takeWhile { it !== endSymbol }
                        .toList()
                )
            }
    }

    fun start(caret: Caret) {
        this.caret.onNext(caret)
    }

    fun stop() {
        this.caret.onNext(null)
    }

    fun isRunning(): Boolean {
        return this.caret.value != null
    }
}