package com.seven.viewmodel

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * @author Richi on 10/4/21.
 */
@HiltViewModel
open class BaseViewModel  @Inject constructor(@ApplicationContext var context: Context) : ViewModel(), LifecycleObserver {

}