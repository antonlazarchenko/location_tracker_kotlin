package com.alazar.base.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.alazar.base.core.PreferenceProvider
import dagger.Component
import dagger.Module
import dagger.Provides
import provider.SharedPrefWrapper
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        BaseModule::class,
    ]
)

interface BaseComponent {
    fun inject(baseApp: BaseApp)
    fun inject(wrapper: SharedPrefWrapper)
}

@Module
class BaseModule constructor(private val application: Application) {
    @Provides
    fun provideContext() : Context = application.applicationContext

    @Provides
    fun provideSharedPreferences() : PreferenceProvider = SharedPrefWrapper()
}

class BaseApp : Application() {
    private lateinit var appComponent: BaseComponent

    override fun onCreate() {
        super.onCreate()
        initDaggerComponent()
        appComponent.inject(this)
    }

    fun getComponent(): BaseComponent {
        if (!this::appComponent.isInitialized) {
            initDaggerComponent()
        }
        return appComponent
    }

    private fun initDaggerComponent() {
        appComponent = DaggerBaseComponent
            .builder()
            .baseModule(BaseModule(this))
            .build()
    }
}