package com.quicksolveplus.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.gson.GsonBuilder
import com.quicksolveplus.QSMobile
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.qspmobile.BuildConfig
import com.quicksolveplus.qspmobile.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Initialize [Retrofit].
 */
object Client {

    private fun retrofit(): Retrofit.Builder {
        val client = getHttpClient()
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder().client(client)
            .baseUrl(Preferences.instance?.baseUrl ?: Api.defaultSever)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    fun api(): Api {
        return retrofit().build().create(Api::class.java)
    }

    /**
     * Make http client and pass into [retrofit] instance.
     */
    private fun getHttpClient(isConnectionCheck: Boolean = false): OkHttpClient {
        val context = QSMobile.instance as Context
        val client: OkHttpClient
        val builder = OkHttpClient().newBuilder()

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }

        builder.addInterceptor(ConnectivityInterceptor(context))

        if (isConnectionCheck) {
            builder.readTimeout(30, TimeUnit.SECONDS).callTimeout(30, TimeUnit.SECONDS)
        } else {
            builder.readTimeout(45, TimeUnit.SECONDS).callTimeout(45, TimeUnit.SECONDS)
        }
        client = builder.build()
        return client
    }

    /**
     * Check internet connectivity.
     */
    private class ConnectivityInterceptor(private val context: Context) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            if (!isOnline(context)) {
                throw NoConnectivityException(context.getString(R.string.msg_server_error))
            }

            val builder = chain.request().newBuilder()
            return chain.proceed(builder.build())
        }

        @Suppress("DEPRECATION")
        fun isOnline(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)

            return if (connectivityManager is ConnectivityManager) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    val networkInfo = connectivityManager.activeNetworkInfo
                    networkInfo?.isConnected ?: false
                } else {
                    val networkCapabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false || networkCapabilities?.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ) ?: false
                }
            } else false
        }
    }
}