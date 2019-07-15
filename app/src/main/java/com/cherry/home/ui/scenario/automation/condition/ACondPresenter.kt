package com.cherry.home.ui.scenario.automation.condition

import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import javax.inject.Inject

@ConfigPersistent
class ACondPresenter @Inject constructor() : BasePresenter<ACondView>() {

    private val TAG : String = "ACondPresenter"

}