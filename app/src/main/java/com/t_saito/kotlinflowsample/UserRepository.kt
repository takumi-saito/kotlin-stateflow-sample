package com.t_saito.kotlinflowsample

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class UserRepository @Inject constructor() {
    val userStateFlow: MutableStateFlow<User> = MutableStateFlow(User.Normal.EMPTY)

    val shouldShowSubActivityStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    suspend fun emitUserStateFlow(user: User) {
        userStateFlow.emit(user)
    }
}