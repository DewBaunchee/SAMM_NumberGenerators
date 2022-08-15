package by.varyvoda.ademis.app.desktop

import by.varyvoda.ademis.app.desktop.ui.view.AdemisDesktopView
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.collections.FXCollections
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import tornadofx.*
import kotlin.reflect.KClass

class AdemisDesktopApp : App(AdemisDesktopView::class) {

    private lateinit var context: ConfigurableApplicationContext

    override fun init() {
        val list = FXCollections.observableArrayList(listOf(1, 2, 3, 4, 5))
        list.toRx().subscribe { change ->
            while (change.next())
                println(change)
        }
        list.removeAll(2)
        list.set(2, 123)


        this.context = SpringApplication.run(this.javaClass)
        context.autowireCapableBeanFactory.autowireBean(this)

        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T = context.getBean(type.java)
            override fun <T : Any> getInstance(type: KClass<T>, name: String): T = context.getBean(name, type.java)
        }
    }

    override fun stop() {
        super.stop()
        context.close()
    }
}

fun main(args: Array<String>) {
    launch<AdemisDesktopApp>(args)
}