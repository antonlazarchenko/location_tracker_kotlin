package com.alazar.tracker.di

import android.app.Application
import com.alazar.authfire.di.AuthUserModule
import com.alazar.base.core.PreferenceProvider
import com.alazar.base.di.BaseApp
import com.alazar.base.di.BaseComponent
import com.alazar.base.di.scope.MainScope
import com.alazar.tracker.MainActivity
import com.alazar.tracker.MapActivity
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
    fun provideSharedPreferences(): PreferenceProvider
    fun inject(app: MainApp)
    fun inject(activity: MainActivity)
    fun inject(activity: MapActivity)
}

@Module(
    includes = [
        AuthUserModule::class,
    ]
)
class MainModule


class MainApp : BaseApp() {

    override fun onCreate() {
        super.onCreate()

        getComponent(this).inject(this)
    }

    companion object {
        lateinit var appComponent: MainAppComponent

        fun getComponent(app: Application): MainAppComponent {
            if (!::appComponent.isInitialized) {
                appComponent = DaggerMainAppComponent
                    .builder()
                    .baseComponent(BaseApp.getComponent(app))
                    .build()
            }
            return appComponent
        }

    }
}
