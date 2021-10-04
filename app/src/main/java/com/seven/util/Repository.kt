import android.content.Context
import com.seven.util.rest.BaseHttpRequester

public interface Repository {
    fun fetchNews(context: Context, onResponse : (List<News>) -> Unit , onFail:(Throwable?, BaseHttpRequester?) ->Unit);
}