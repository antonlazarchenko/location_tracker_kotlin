package com.alazar.base.di

import android.app.Application
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        BaseModule::class,
    ]
)

interface BaseComponent {
    fun inject(baseApp: BaseApp)
}

@Module
class BaseModule {



}

class BaseApp : Application() {
    private lateinit var appComponent: BaseComponent

    override fun onCreate() {
        super.onCreate()
        initDaggerComponent()
        appComponent.inject(this)
    }

    fun getComponent(): BaseComponent {
        return appComponent
    }

    private fun initDaggerComponent() {
        appComponent = DaggerBaseComponent
            .builder()
            .build()
    }
}