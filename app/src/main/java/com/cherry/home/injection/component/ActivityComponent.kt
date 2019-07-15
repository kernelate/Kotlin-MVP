package com.cherry.home.injection.component


import com.cherry.home.injection.PerActivity
import com.cherry.home.injection.module.ActivityModule
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.add.AddDeviceActivity
import com.cherry.home.ui.device.category.remote.RemoteActivity
import com.cherry.home.ui.device.category.sensor.SensorActivity
import com.cherry.home.ui.device.category.smartswitch.SwitchActivity
import com.cherry.home.ui.device.category.socket.ListSocketActivity
import com.cherry.home.ui.device.config.AddDeviceTipActivity
import com.cherry.home.ui.device.config.ap.AddDeviceAPTipActivity
import com.cherry.home.ui.device.config.ec.ECActivity
import com.cherry.home.ui.device.config.ec.ECBindActivity
import com.cherry.home.ui.device.location.DeviceLocationActivity
import com.cherry.home.ui.device.setting.DeviceSettingActivity
import com.cherry.home.ui.device.sharing.DeviceSharingActivity
import com.cherry.home.ui.family.add.AddFamilyActivity
import com.cherry.home.ui.family.empty.EmptyFamilyActivity
import com.cherry.home.ui.family.members.FamilyMembersActivity
import com.cherry.home.ui.family.settings.FamilySettingsActivity
import com.cherry.home.ui.home.HomeFragment
import com.cherry.home.ui.login.LoginActivity
import com.cherry.home.ui.main.MainActivity
import com.cherry.home.ui.profile.ProfileActivity
import com.cherry.home.ui.profile.timezone.TimeZoneActivity
import com.cherry.home.ui.scenario.addsmart.AddSmartActivity
import com.cherry.home.ui.scenario.automation.action.AActionActivity
import com.cherry.home.ui.scenario.automation.condition.ACondActivity
import com.cherry.home.ui.scenario.automation.conditionaction.AConActionActivity
import com.cherry.home.ui.scenario.automation.control.AControlActivity
import com.cherry.home.ui.scenario.automation.function.AFunctionActivity
import com.cherry.home.ui.scenario.automation.settings.AutoActivity
import com.cherry.home.ui.scenario.autosettings.timeperiod.TimePeriodActivity
import com.cherry.home.ui.scenario.autosettings.timeperiod.repeat.RepeatActivity
import com.cherry.home.ui.scenario.scene.TimeLapseActivity
import com.cherry.home.ui.scenario.scenebg.SceneBackgroundActivity
import com.cherry.home.ui.scenario.scene.control.SControlActivity
import com.cherry.home.ui.scenario.scene.settings.SSettingsActivity
import com.cherry.home.ui.scenario.scene.action.SActionActivity
import com.cherry.home.ui.scenario.scene.function.SFunctionActivity
import com.cherry.home.ui.signup.SignupActivity
import com.cherry.home.ui.signup.forgotpass.ForgotPasswordActivity
import com.cherry.home.ui.signup.forgotpass.validate.CodeValidationActivity
import com.cherry.home.ui.signup.forgotpass.validate.SuccessActivity
import com.cherry.home.util.map.MapsActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun inject(loginActivity: LoginActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(signupActivity: SignupActivity)

    fun inject(homeFragment: HomeFragment)

    fun inject(addDevice: AddDeviceActivity)

    fun inject(listSocketActivity: ListSocketActivity)

    fun inject(switchActivity: SwitchActivity)

    fun inject(sensorActivity: SensorActivity)

    fun inject(remoteActivity: RemoteActivity)

    fun inject(ecActivity: ECActivity)

    fun inject(ecBindActivity: ECBindActivity)

    fun inject(emptyFamilyActivity: EmptyFamilyActivity)

    fun inject (addFamilyActivity: AddFamilyActivity)

    fun inject (addDeviceTipActivity: AddDeviceTipActivity)

    fun inject (addDeviceAPTipActivity: AddDeviceAPTipActivity)

    fun inject (deviceSettingActivity: DeviceSettingActivity)

    fun inject (profileActivity: ProfileActivity)

    fun inject (forgotPasswordActivity: ForgotPasswordActivity)

    fun inject (codeValidationActivity: CodeValidationActivity)

    fun inject (successActivity: SuccessActivity)

    fun inject (SSettingsActivity: SSettingsActivity)

    fun inject (SActionActivity: SActionActivity)

    fun inject (addSmartActivity: AddSmartActivity)

    fun inject (autoActivity: AutoActivity)

    fun inject (SFunctionActivity: SFunctionActivity)

    fun inject (SControlActivity: SControlActivity)

    fun inject (deviceLocationActivity: DeviceLocationActivity)

    fun inject (sceneBackgroundActivity: SceneBackgroundActivity)

    fun inject (aCondActivity: ACondActivity)

    fun inject (aActionActivity: AActionActivity)

    fun inject(AConActionActivity: AConActionActivity)

    fun inject (aFunctionActivity: AFunctionActivity)

    fun inject (aControlActivity: AControlActivity)

    fun inject(mapsActivity: MapsActivity)

//    fun inject(deviceInformation: DeviceInformation)
//
    fun inject(deviceSharingActivity: DeviceSharingActivity)
//
    fun inject(familySettingsActivity: FamilySettingsActivity)
//
    fun inject(familyMembersActivity: FamilyMembersActivity)

    fun inject(timeZoneActivity: TimeZoneActivity)

    fun inject(timeLapseActivity: TimeLapseActivity)

    fun inject(timePeriodActivity: TimePeriodActivity)

    fun inject(repeatActivity: RepeatActivity)
}