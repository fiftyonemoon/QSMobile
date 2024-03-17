package com.quicksolveplus.api

import android.app.Application
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

/**
 * 17/03/23.
 *
 * @author hardkgosai.
 */
abstract class QSRepo(val application: Application) {

    suspend inline fun <T> runApi(
        apiName: String = "",
        crossinline apiCall: suspend () -> Response<T>,
        responseStatus: MutableLiveData<ResponseStatus>,
        other: Any? = null
    ) {

        return try {

            //send status is running callback
            responseStatus.postValue(ResponseStatus.Running(apiName = apiName))

            //invoke function
            val result = apiCall()

            if (result.isSuccessful) {
                val data = result.body()
                if (data != null) {
                    return responseStatus.postValue(
                        ResponseStatus.Success(apiName = apiName, data = data, other = other)
                    )
                } else {
                    return responseStatus.postValue(
                        ResponseStatus.Failed(
                            apiName = apiName, msg = "Something went wrong, Please try later"
                        )
                    )
                }
            } else {
                responseStatus.postValue(
                    ResponseStatus.Failed(
                        apiName = apiName, msg = "Response error with code ${result.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            checkException(apiName = apiName, e = e, responseStatus = responseStatus)
        }
    }

    fun checkException(
        apiName: String, e: Exception, responseStatus: MutableLiveData<ResponseStatus>
    ) {
        when (e) {
            is HttpException -> {
                e.response()?.run {
                    val body = errorBody()
                    var msg = "Something went wrong"
                    if (code() != 200 && body != null) {
                        JSONObject(body.string()).run {
                            if (has("message")) {
                                msg = getString("message")
                            } else if (has("error")) {
                                msg = getString("error")
                            }
                        }
                    }
                    responseStatus.postValue(
                        ResponseStatus.Failed(
                            apiName = apiName, msg = msg, id = code()
                        )
                    )
                }
            }
            is KotlinNullPointerException -> responseStatus.postValue(
                ResponseStatus.Failed(apiName = apiName, msg = "Something went wrong")
            )
            is NoConnectivityException -> {
                responseStatus.postValue(
                    ResponseStatus.Failed(
                        apiName = apiName, msg = NoConnectivityException().message
                    )
                )
            }
            is SocketTimeoutException -> {
                responseStatus.postValue(
                    ResponseStatus.Failed(
                        apiName = apiName, msg = "Connection timeout"
                    )
                )
            }
            else -> {
                responseStatus.postValue(
                    ResponseStatus.Failed(
                        apiName = apiName, msg = "Server is temporary down, please try later"
                    )
                )
            }
        }
    }
}