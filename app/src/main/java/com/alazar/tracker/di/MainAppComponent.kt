package com.alazar.tracker.di

import android.app.Application
import com.alazar.authfire.di.AuthUserModule
import com.alazar.base.di.BaseComponent
import com.alazar.base.di.BaseModule
import com.alazar.base.di.DaggerBaseComponent
import com.alazar.base.di.scope.MainScope
import com.alazar.tracker.MainActivity
import com.alazar.tracker.MapActivity
import dagger.Component
import dagger.Module

@MainScope
@Component(
    dependencies = [
//        BaseComponent::class,
    ],
    modules = [
        MainModule::class,
        BaseModule::class,
    ]
)

interface MainAppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: MapActivity)
}

@Module(
    includes = [
        AuthUserModule::class,
        BaseModule::class,
    ]
)
class MainModule


class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerMainAppComponent
            .builder()
            .baseModule(BaseModule(this))
//            .baseComponent(DaggerBaseComponent.builder().baseModule(BaseModule(this)).build())
            .build()
    }

    companion object {
        lateinit var appComponent: MainAppComponent
    }
}
