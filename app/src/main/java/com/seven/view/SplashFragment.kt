package com.seven.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.seven.model.News
import com.seven.myapplication.databinding.FragmentSplashBinding
import com.seven.viewmodel.NewsViewModel

/**
 * @author Richi on 10/4/21.
 */
public class SplashFragment : BaseFragment{
    constructor() : super()


    private var waitingForResponse = false
    lateinit var binding : FragmentSplashBinding
    lateinit var viewModel : NewsViewModel

    val onReceiveNews : (List<News>) -> Unit = { news  ->
        if (waitingForResponse) {
            showNewsFragment()
        }
    }

    companion object{
        fun newInstance():SplashFragment{
            var f = SplashFragment();
            return f;
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(inflater , container , false)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchNews(onReceiveNews)

        view.postDelayed({
            if (viewModel.newsAdapter.value!!.itemCount > 0) {
                showNewsFragment()
            } else {
                waitingForResponse = true
            }

        } , 1000)
    }

    private fun showNewsFragment() {
            getBaseActivity()?.showFragment(NewsFragment.newInstance(viewModel.newsAdapter.value!!.getItems()))
    }


}