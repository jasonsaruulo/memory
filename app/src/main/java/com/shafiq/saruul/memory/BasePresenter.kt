package com.shafiq.saruul.memory

interface BasePresenter<T> {

    fun takeView(view: T)

    fun dropView()
}
