package ru.g000sha256.reduktor.demo.screen.main

import io.reactivex.rxjava3.core.Single
import ru.g000sha256.reduktor.demo.model.User
import ru.g000sha256.schedulers_factory.SchedulersFactory

class MainRepository(
        private val api: MainApi,
        private val pageLimit: MainPageLimit,
        private val schedulersFactory: SchedulersFactory
) {

    fun loadUser(): Single<User> {
        return api
                .getUser()
                .subscribeOn(schedulersFactory.ioScheduler)
    }

    fun loadUsers(lastUserId: Long?): Single<List<User>> {
        return api
                .getUsers(pageLimit.value, lastUserId)
                .subscribeOn(schedulersFactory.ioScheduler)
    }

}