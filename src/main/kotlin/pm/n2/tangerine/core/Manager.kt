package pm.n2.tangerine.core

abstract class Manager<T> {
    open val items = listOf<T>()

    abstract fun init()
}
