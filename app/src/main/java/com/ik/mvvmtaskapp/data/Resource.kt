package com.ik.mvvmtaskapp.data

sealed class Resource<T> {
  data class Loading<T>(val data: T? = null) : Resource<T>()
  data class Error<T>(val error: Throwable? = null) :Resource<T>()
  data class Success<T>(val data: T): Resource<T>()
}

fun <T> T.asSuccess() = Resource.Success(this)
fun <T> T.asLoading() = Resource.Loading(this)
fun <T> T.asError(error: Throwable? = null) = Resource.Error<T>(error)