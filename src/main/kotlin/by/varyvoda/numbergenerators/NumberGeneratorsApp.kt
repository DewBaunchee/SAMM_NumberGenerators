package by.varyvoda.numbergenerators

import by.varyvoda.numbergenerators.ui.view.NumberGeneratorsView
import tornadofx.App
import tornadofx.launch

class NumberGeneratorsApp : App(NumberGeneratorsView::class)

fun main(args: Array<String>) {
    launch<NumberGeneratorsApp>(args)
}