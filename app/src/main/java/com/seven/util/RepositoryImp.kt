package com.seven.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seven.model.News
import com.seven.model.NewsDto
import com.seven.util.data.RoomHttpCacher
import com.seven.util.rest.BaseHttpRequester

public class RepositoryImp : Repository {

    private lateinit var gson:Gson
    constructor()
    {
        gson = Gson();
    }


    override fun fetchNews(context:Context, onResponse: (List<News>) -> Unit, onFail:(Throwable?, BaseHttpRequester?) ->Unit) {
        var requester = BaseHttpRequester("")
        requester.get()
        requester.setHttpCatchable(RoomHttpCacher(context))
        requester.url = "http://156.253.5.182:8081/api/v1/news/"
        requester.listener(object :BaseHttpRequester.HttpResponseListener {
            override fun onHttpResponse(response: String?, responseCode: Int, cached: Boolean) {
                var dto = Gson().fromJson<NewsDto>(response,NewsDto::class.java)
                if (dto.isSuccess) {
                    onResponse(dto.data);
                } else {
                    onFail(Exception(dto::class.java.name) , requester)
                }
            }
        })

        requester.errorListener(object :BaseHttpRequester.HttpErrorListener {
            override fun onHttpError(e: Throwable?, requester: BaseHttpRequester?) {
                onFail(e,requester)
            }
        })

        requester.request()
    }




}