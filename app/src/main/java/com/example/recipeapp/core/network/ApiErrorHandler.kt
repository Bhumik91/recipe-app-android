package com.example.recipeapp.core.network

import android.util.Log
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
object ApiErrorHandler {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.Success(apiCall())
        } catch (e: HttpException) {
            Log.e("ApiError",e.toString())
            NetworkResult.Error(e.localizedMessage ?: "Server error", e.code())
        } catch (e: IOException) {
            Log.e("ApiError",e.toString())
            NetworkResult.Error("No internet connection")
        } catch (e: SerializationException) {
            Log.e("ApiError",e.toString())
            NetworkResult.Error("Unexpected response format")
        } catch (e: Exception) {
            Log.e("ApiError",e.toString())
            NetworkResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}