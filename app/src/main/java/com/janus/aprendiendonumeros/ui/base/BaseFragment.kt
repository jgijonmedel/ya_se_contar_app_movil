package com.janus.aprendiendonumeros.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment(layout: Int) : Fragment(layout) {

    protected lateinit var mContext: BaseActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    abstract fun initUI(view: View)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as BaseActivity
    }
}