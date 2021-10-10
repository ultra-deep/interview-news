package com.seven.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.seven.util.Repository
import com.seven.view.adapter.NewAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * @author Richi on 10/4/21.
 */
@HiltViewModel
public class NewsViewModel @Inject constructor(@ApplicationContext  context: Context) : BaseViewModel(context) {

    @Inject lateinit var repository: Repository;

    var progressing = MutableLiveData<Boolean>(false)
    val newsAdapter = MutableLiveData(NewAdapter())

    fun fetchNews(context: Context){

        progressing.value = true

        repository.fetchNews(context, { news ->
            progressing.value = false
            newsAdapter.value!!.setItems(news)
            newsAdapter.value!!.notifyDataSetChanged()
        },
            {  e,  equester ->
                progressing.value = false
                showError(e?.message)
            })
    }

    private fun showError(message: String?) {
        //
    }
}