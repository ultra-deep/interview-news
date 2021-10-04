package com.seven.view

import androidx.fragment.app.Fragment

/**
 * @author Richi on 10/4/21.
 */
public open class BaseFragment() : Fragment() {
    fun getBaseActivity() : MainActivity? {
        return activity as? MainActivity
    }


}