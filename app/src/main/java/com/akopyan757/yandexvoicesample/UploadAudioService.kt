package com.akopyan757.yandexvoicesample

import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

class UploadAudioService {

    private val recognitionApi: YandexRecognitionApi by lazy { createRecognitionApi() }

    //private val mainHandler = Handler(Looper.getMainLooper())
    //private val backgroundHandler = Handler.createAsync(Looper.)

    interface RecognizeAudioListener {
        fun onSuccessRecognize(result: String)
        fun onErrorRecognize(throwable: Throwable)
    }

    fun recognizeAudio(audioFilename: String, listener: RecognizeAudioListener) {
        val byteArray = FileUtil.getByteArrayFromAudio(audioFilename)


        Log.i("TAG2", byteArray?.size.toString())

        if (byteArray == null) {
            listener.onErrorRecognize(java.lang.Exception("Audio file is incorrect"))
            return
        }

        val mediaType = MediaType.parse(MEDIA_OGG_TYPE)
        val requestBody = RequestBody.create(mediaType, byteArray)
        val recognitionCall = recognitionApi.recognizeAudio(
            "Bearer $YANDEX_IAM_TOKEN", AUDIO_FORMAT, YANDEX_FOLDER_ID, requestBody
        )
        recognitionCall.enqueue(object : Callback<AudioText> {
            override fun onResponse(call: Call<AudioText>, response: Response<AudioText>) {
                    Log.i("TAG", response.toString())
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        listener.onSuccessRecognize(body.result)
                    } else {
                        listener.onErrorRecognize(HttpException(response))
                    }
            }

            override fun onFailure(call: Call<AudioText>, t: Throwable) {
                    Log.e("TAG", "Error", t)
                    listener.onErrorRecognize(t)
            }
        })
    }

    private fun createRecognitionApi(): YandexRecognitionApi {
        return Retrofit.Builder()
            .baseUrl(YANDEX_BASE_HOST)
            .addConverterFactory(GsonConverterFactory.create())
            //.callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
            .create(YandexRecognitionApi::class.java)
    }

    data class AudioText(
        @SerializedName("result")
        val result: String
    )

    interface YandexRecognitionApi {
        @POST("speech/v1/stt:recognize")
        fun recognizeAudio(
            @Header("Authorization") token: String,
            @Query("format") format: String,
            @Query("folderId") folderId: String,
            @Body requestBody: RequestBody
        ): Call<AudioText>
    }

    companion object {
        private const val YANDEX_BASE_HOST = "https://stt.api.cloud.yandex.net"
        private const val AUDIO_FORMAT = "oggopus"
        private const val MEDIA_OGG_TYPE = "application/ogg"
        private const val YANDEX_FOLDER_ID = "b1gbk7a1rudgg20mqu89"
        private const val YANDEX_IAM_TOKEN = "t1.9euelZrHi4qMzsqMi5GKy4yNmZiTmu3rnpWamJ2QjJyOmJCalYqKzoySj8rl9Pc8VA19-e8fQ16z3fT3fAILffnvH0Nesw.bIsmbiJYFy5LDWQ5LKGFVAB3cemCbghNXCtDdxVRBHrngwtsQorkmrwR5-LRIT1sJZNIEJw_ejZfxMgprcniBg"
    }
}