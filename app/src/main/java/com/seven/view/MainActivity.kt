package com.seven.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.seven.myapplication.R
import com.seven.myapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


class MainActivity : BaseActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        if (savedInstanceState == null) {
            showFragment(SplashFragment.newInstance())
        }
//        setContentView(R.layout.activity_main)
    }

    /**
     * showing a fragment in standard mode
     */
    public fun showFragment(fragment: BaseFragment){
        showFragment(fragment , true , true)
    }

    /**
     * show fragment by controlling some transactions and stacks
     */
    public fun showFragment(fragment: BaseFragment, addToBackStack: Boolean = true , useTransition:Boolean = true , inverseTransition :Boolean = false , clearBackStack:Boolean=false) {
        showFragment(supportFragmentManager.beginTransaction(),fragment, addToBackStack, useTransition, inverseTransition, clearBackStack)
    }

    /**
     * show fragment by controlling some transactions and stacks
     * @param clearBackStack if true, all stack of fragments will be cleared
     */
    public fun showFragment(ft: FragmentTransaction, fragment: BaseFragment, addToBackStack: Boolean = true, useTransition:Boolean = true, inverseTransition :Boolean = false, clearBackStack:Boolean=false) {
//        if(clearBackStack) clearBackStack()
        val tag = fragment.javaClass.name

//        if (useTransition) {
//            if (inverseTransition) {
//                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_left)
//            } else {
//                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
//            }
//        }

        ft.replace(R.id.frame, fragment, tag)
        if (addToBackStack) ft.addToBackStack(tag)
        ft.commit()
    }

}