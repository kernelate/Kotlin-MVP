package com.cherry.home.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_nav_setting.*
import javax.inject.Inject

class SettingsFragment : BaseFragment(), SettingsView {

    @Inject lateinit var settingsPresenter: SettingsPresenter

    companion object {
        val TAG: String = "Settings"
    }

    fun newInstance(): SettingsFragment {
        return SettingsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        settingsPresenter.attachView(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId(), container, false)


        return view
    }

    

    override fun layoutId(): Int = R.layout.fragment_nav_setting

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun snackBarLayoutId(): ViewGroup = rl_nav_setting

}