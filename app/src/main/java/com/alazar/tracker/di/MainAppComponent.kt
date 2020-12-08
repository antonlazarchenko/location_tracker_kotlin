package com.alazar.tracker.di

import android.app.Application
import com.alazar.authfire.di.AuthUserModule
import com.alazar.base.di.BaseComponent
import com.alazar.base.di.DaggerBaseComponent
import com.alazar.base.di.scope.MainScope
import com.alazar.tracker.MainActivity
import dagger.Component
import dagger.Module

@MainScope
@Component(
    dependencies = [
        BaseComponent::class,
    ],
    modules = [
        MainModule::class,
    ]
)

interface MainAppComponent {
    fun inject(activity: MainActivity)
}

@Module(includes = [AuthUserModule::class])
class MainModule


class MainApp : Application() {
    private lateinit var appComponent: MainAppComponent

    fun getComponent(): MainAppComponent {
        if (!this::appComponent.isInitialized) {
            initDaggerComponent()
        }
        return appComponent
    }

    private fun initDaggerComponent() {
        appComponent = DaggerMainAppComponent
            .builder()
            .baseComponent(DaggerBaseComponent.builder().build())
            .build()
    }
}
