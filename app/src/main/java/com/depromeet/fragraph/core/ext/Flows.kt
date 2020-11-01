package com.depromeet.fragraph.core.ext

import com.depromeet.fragraph.domain.model.LoadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.toLoadingState(): Flow<LoadState<T>> {
    return map<T, LoadState<T>> {
        LoadState.Loaded(it)
    }.onStart {
        @Suppress("UNCHECKED_CAST")
        emit(LoadState.Loading as LoadState<T>)
    }.catch { e ->
        emit(LoadState.Error(e))
    }
}