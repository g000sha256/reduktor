package g000sha256.reduktor.core

import java.util.LinkedList

class Store<ACTION, STATE>(
    initialState: STATE,
    private val reducer: Reducer<ACTION, STATE>,
    initializers: Iterable<Initializer<ACTION, STATE>> = emptyList(),
    sideEffects: Iterable<SideEffect<ACTION, STATE>> = emptyList(),
    private val logger: Logger = Logger {},
    private val newStatesCallback: (state: STATE) -> Unit
) {

    private val any = Any()
    private val initializerEnvironmentsList: List<Pair<Initializer<ACTION, STATE>, Environment<ACTION>>>
    private val sideEffectEnvironmentsList: List<Pair<SideEffect<ACTION, STATE>, Environment<ACTION>>>

    private val thread: Thread
        get() = Thread.currentThread()

    private var isReleased = false
    private var counter = 0
    private var state = initialState

    init {
        val actions = createActions()
        initializerEnvironmentsList = initializers.map { it to createEnvironment(actions) }
        sideEffectEnvironmentsList = sideEffects.map { it to createEnvironment(actions) }
        logger.invoke("--------INIT--------")
        logger.invoke("STATE  : $initialState")
        logger.invoke("THREAD : ${thread.name}")
        logger.invoke("--------------------")
        synchronized(any) {
            initializerEnvironmentsList.forEach {
                if (isReleased) return@synchronized
                it.first.apply { it.second.invoke(initialState) }
            }
        }
    }

    fun release() {
        synchronized(any) {
            if (isReleased) return@synchronized
            isReleased = true
            initializerEnvironmentsList.forEach { it.second.tasks.clear() }
            sideEffectEnvironmentsList.forEach { it.second.tasks.clear() }
            logger.invoke("------RELEASED------")
            logger.invoke("THREAD : ${thread.name}")
            logger.invoke("--------------------")
        }
    }

    private fun createActions(): Actions<ACTION> {
        return object : Actions<ACTION> {

            override fun post(action: ACTION) {
                synchronized(any) {
                    if (isReleased) return@synchronized
                    handleAction(action)
                }
            }

            override fun post(vararg actions: ACTION) {
                synchronized(any) {
                    if (isReleased) return@synchronized
                    actions.forEach(::handleAction)
                }
            }

            override fun post(actions: Iterable<ACTION>) {
                synchronized(any) {
                    if (isReleased) return@synchronized
                    actions.forEach(::handleAction)
                }
            }

            private fun handleAction(action: ACTION) {
                val oldState = state
                logger.invoke("-------ACTION-------")
                logger.invoke("ACTION > $action")
                logger.invoke("STATE  > $oldState")
                val newState = reducer.run { oldState.invoke(action) }
                if (newState == oldState) {
                    logger.invoke("STATE  : NOT CHANGED")
                    logger.invoke("THREAD : ${thread.name}")
                    logger.invoke("--------------------")
                } else {
                    state = newState
                    logger.invoke("STATE  < $newState")
                    logger.invoke("THREAD : ${thread.name}")
                    logger.invoke("--------------------")
                    newStatesCallback(newState)
                }
                sideEffectEnvironmentsList.forEach {
                    if (isReleased) return
                    it.first.apply { it.second.invoke(action, newState) }
                }
            }

        }
    }

    private fun createEnvironment(actions: Actions<ACTION>): Environment<ACTION> {
        return object : Environment<ACTION> {

            override val actions = actions
            override val tasks = createTasks()

        }
    }

    private fun createTasks(): Tasks {
        return object : Tasks {

            private val mutableList: MutableList<TaskInfo> = LinkedList()

            override fun add(task: Task) {
                synchronized(any) { add(task, key = null) }
            }

            override fun add(key: String, task: Task) {
                synchronized(any) { add(task, key) }
            }

            override fun clear() {
                synchronized(any) {
                    if (mutableList.size == 0) return
                    mutableList
                        .toMutableList()
                        .apply { mutableList.clear() }
                        .forEach {
                            it.isCleared = true
                            logTaskRemoved(it)
                            it.task.cancel()
                        }
                }
            }

            override fun clear(key: String) {
                synchronized(any) {
                    val taskInfo = mutableList.firstOrNull { it.key == key } ?: return@synchronized
                    taskInfo.isCleared = true
                    mutableList.remove(taskInfo)
                    logTaskRemoved(taskInfo)
                    taskInfo.task.cancel()
                }
            }

            private fun add(task: Task, key: String?) {
                if (isReleased) return
                val contains = mutableList.firstOrNull { it.task === task } != null
                if (contains) throw IllegalStateException("Task has already been added")
                task.status.checkNotCancelled()
                task.status.checkNotCompleted()
                task.status.checkNotStarted()
                key?.also { clear(it) }
                val taskInfo = TaskInfo(task, id = ++counter, key)
                mutableList.add(taskInfo)
                logTaskAdded(taskInfo)
                task.start {
                    synchronized(any) {
                        if (taskInfo.isCleared) return@synchronized
                        taskInfo.isCleared = true
                        val isRemoved = mutableList.remove(taskInfo)
                        if (isRemoved) logTaskRemoved(taskInfo)
                    }
                }
            }

            private fun logTaskAdded(taskInfo: TaskInfo) {
                logger.invoke("-----TASK-ADDED-----")
                logger.invoke("ID     : ${taskInfo.id}")
                taskInfo.key?.also { logger.invoke("KEY    : $it") }
                logger.invoke("THREAD : ${thread.name}")
                logger.invoke("--------------------")
            }

            private fun logTaskRemoved(taskInfo: TaskInfo) {
                logger.invoke("----TASK-REMOVED----")
                logger.invoke("ID     : ${taskInfo.id}")
                taskInfo.key?.also { logger.invoke("KEY    : $it") }
                logger.invoke("THREAD : ${thread.name}")
                logger.invoke("--------------------")
            }

        }
    }

    private class TaskInfo(val task: Task, val id: Int, val key: String?) {

        var isCleared = false

    }

}