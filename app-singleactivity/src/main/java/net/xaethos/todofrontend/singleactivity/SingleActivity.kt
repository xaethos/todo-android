package net.xaethos.todofrontend.singleactivity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import dagger.Provides
import dagger.Subcomponent
import net.xaethos.todofrontend.singleactivity.tododetail.DetailController
import net.xaethos.todofrontend.singleactivity.todoedit.EditController
import net.xaethos.todofrontend.singleactivity.todolist.ListController
import net.xaethos.todofrontend.singleactivity.util.bindView
import net.xaethos.todofrontend.singleactivity.util.routerTransaction

class SingleActivity : AppCompatActivity() {

    val container by bindView<ViewGroup>(android.R.id.content)

    lateinit var router: Router
    lateinit var component: Component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = singletonComponent.activityComponent(Module())

        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(ListController().routerTransaction())
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) super.onBackPressed()
    }

    @ActivityScope @Subcomponent(modules = arrayOf(Module::class))
    interface Component {
        fun listComponentBuilder(): ListController.ViewComponent.Builder
        fun detailComponentBuilder(): DetailController.ViewComponent.Builder
        fun editComponentBuilder(): EditController.ViewComponent.Builder
    }

    @dagger.Module
    inner class Module {
        @Provides @ActivityScope fun context(): Context = this@SingleActivity
        @Provides @ActivityScope fun activity(): SingleActivity = this@SingleActivity
    }
}

val Activity.component: SingleActivity.Component
    get() = if (this is SingleActivity) component else throw IllegalStateException("Somebody set up us the bomb")
