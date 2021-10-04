package com.seven.viewmodel

import Repository
import androidx.lifecycle.MutableLiveData
import com.seven.view.adapter.NewAdapter

/**
 * @author Richi on 10/4/21.
 */
public class NewsViewModel(var repository:Repository) : BaseViewModel() {

    var progressing = MutableLiveData<Boolean>(false)
    val newsAdapter = MutableLiveData(NewAdapter())

    fun fetchNews(){

        progressing.value = true

        repository.fetchNews({ news ->
            progressing.value = false
            newsAdapter.value!!.setItems(news)
            newsAdapter.value!!.notifyDataSetChanged()
        },
            {  e,  equester ->
                progressing.value = false
                shwoError(e?.message)
            })
    }

    private fun shwoError(message: String?) {
        //
    }
}