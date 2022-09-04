package by.varyvoda.lab2

import by.varyvoda.lab2.ui.view.Lab2View
import tornadofx.App
import tornadofx.launch

class AdemisDesktopApp : App(Lab2View::class)

fun main(args: Array<String>) {
    launch<AdemisDesktopApp>(args)
}