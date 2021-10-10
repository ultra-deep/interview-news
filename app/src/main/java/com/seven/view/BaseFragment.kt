package com.seven.view

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Richi on 10/4/21.
 */
@AndroidEntryPoint
public open class BaseFragment() : Fragment() {
    fun getBaseActivity() : MainActivity? {
        return activity as? MainActivity
    }
}