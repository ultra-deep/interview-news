import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seven.util.rest.BaseHttpRequester

public class RepositoryImp : Repository {

    private lateinit var gson:Gson
    constructor()
    {
        gson = Gson();
    }


    override fun fetchNews(onResponse: (List<News>) -> Unit , onFail:(Throwable? , BaseHttpRequester?) ->Unit) {
        var requester = BaseHttpRequester("")
        requester.get()
        requester.url = "http://156.253.5.182:8081/api/v1/news/"
        requester.listener(object :BaseHttpRequester.HttpResponseListener {
            override fun onHttpResponse(response: String?, responseCode: Int, cached: Boolean) {
                val models: List<News> = Gson().fromJson(response, object : TypeToken<List<News?>?>() {}.getType())
                onResponse(models);
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