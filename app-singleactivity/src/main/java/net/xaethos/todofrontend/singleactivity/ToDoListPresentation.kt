package net.xaethos.todofrontend.singleactivity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bluelinelabs.conductor.rxlifecycle.RxController
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import net.xaethos.todofrontend.singleactivity.util.bindView
import net.xaethos.todofrontend.singleactivity.util.textViewText
import javax.inject.Inject
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ToDoListScope

@ToDoListScope
@Subcomponent(modules = arrayOf(ToDoListModule::class))
interface ToDoListComponent {
    fun inject(controller: ToDoListController)
}

@Module
class ToDoListModule(toDoListController: ToDoListController) {
    @get:Provides @ToDoListScope val listPresenter: ToDoListMediator.ListPresenter = toDoListController
}

/**
 * View presenter: UI controls and events
 */
class ToDoListController : RxController(), ToDoListMediator.ListPresenter {

    @Inject lateinit var mediator: ToDoListMediator
    @Inject lateinit var adapter: ToDoListAdapter

    private val listView: RecyclerView by bindView(R.id.todo_list)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View =
            inflater.inflate(R.layout.todo_list, container, false)

    override fun onAttach(view: View) {
        super.onAttach(view)
        singletonComponent.toDoListComponent(ToDoListModule(this)).inject(this)
        listView.adapter = adapter
    }
}

@ToDoListScope
class ToDoListAdapter @Inject constructor(private val mediator: ToDoListMediator) :
        RecyclerView.Adapter<ToDoItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ToDoItemViewHolder(LayoutInflater.from(parent.context), parent)

    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) =
            mediator.onBindItemPresenter(holder, position)

    override fun getItemCount() = mediator.itemCount
}

class ToDoItemViewHolder(view: View) : RecyclerView.ViewHolder(view), ToDoListMediator.ItemPresenter {
    private val idView: TextView by bindView(R.id.id)
    private val contentView: TextView by bindView(R.id.content)

    override var urlText by textViewText(idView)
    override var titleText by textViewText(contentView)

    constructor(inflater: LayoutInflater, parent: ViewGroup) :
    this(inflater.inflate(R.layout.todo_list_content, parent, false))

    override fun toString() = super.toString() + " '" + contentView.text + "'"
}