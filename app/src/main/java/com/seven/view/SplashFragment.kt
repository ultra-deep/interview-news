package com.seven.view

import Repository
import RepositoryImp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.seven.myapplication.databinding.FragmentSplashBinding
import com.seven.viewmodel.NewsViewModel

/**
 * @author Richi on 10/4/21.
 */
public class SplashFragment : BaseFragment{
    constructor() : super()

    private lateinit var repostory : Repository

    lateinit var binding : FragmentSplashBinding
    lateinit var viewModel : NewsViewModel

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

        viewModel.fetchNews(requireContext())

        view.postDelayed({
            getBaseActivity()?.showFragment(NewsFragment.newInstance(viewModel.newsAdapter.value!!.getItems()))

        } , 2000)
    }


}