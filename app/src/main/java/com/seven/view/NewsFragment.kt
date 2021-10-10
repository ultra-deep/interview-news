package com.seven.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.seven.model.News
import com.seven.myapplication.databinding.FragmentNewsBinding
import com.seven.myapplication.databinding.FragmentSplashBinding
import com.seven.viewmodel.NewsViewModel
import java.util.*

/**
 * @author Richi on 10/4/21.
 */
public class NewsFragment : BaseFragment{
    constructor() : super()

    lateinit var binding : FragmentNewsBinding
    lateinit var viewModel : NewsViewModel

    companion object {
        fun newInstance(news:ArrayList<News>) : NewsFragment {
            var f = NewsFragment()
            f.arguments = Bundle()
            f.requireArguments().putParcelableArrayList("news" , news);
            return f;
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater , container , false)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.newsAdapter.value!!.setItems(arguments?.getParcelableArrayList<News>("news") as? ArrayList<News>)
    }
}