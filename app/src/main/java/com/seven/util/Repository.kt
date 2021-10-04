import com.seven.util.rest.BaseHttpRequester

public interface Repository {
    fun fetchNews(onResponse : (List<News>) -> Unit , onFail:(Throwable?, BaseHttpRequester?) ->Unit);
}