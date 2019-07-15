package com.cherry.home.injection.component

import com.cherry.home.injection.PerFragment
import com.cherry.home.injection.module.FragmentModule
import com.cherry.home.ui.aboutus.AboutUs
import com.cherry.home.ui.home.HomeFragment
import com.cherry.home.ui.homemanagement.HomeManagementFragment
import com.cherry.home.ui.room.bedroom.BedRoomFragment
import com.cherry.home.ui.room.diningroom.DiningRoomFragment
import com.cherry.home.ui.room.kitchen.KitchenFragment
import com.cherry.home.ui.room.livingroom.LivingRoomFragment
import com.cherry.home.ui.scenario.ScenarioFragment
import com.cherry.home.ui.settings.SettingsFragment
import dagger.Subcomponent

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {

    fun inject(homeFragment: HomeFragment)

    fun inject(scenarioFragment: ScenarioFragment)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(aboutUs: AboutUs)

    fun inject(livingRoomFragment: LivingRoomFragment)

    fun inject(diningRoomFragment: DiningRoomFragment)

    fun inject(bedRoomFragment: BedRoomFragment)

    fun inject(kitchenFragment: KitchenFragment)

    fun inject(homeManagementFragment: HomeManagementFragment)

}
