package com.alazar.base.di

import android.app.Application
import android.content.Context
import com.alazar.base.core.NetworkProvider
import com.alazar.base.core.PreferenceProvider
import provider.NetworkWrapper
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
    fun provideContext(): Context
    fun provideNetworkProvider(): NetworkProvider
    fun provideSharedPreferences(): PreferenceProvider
    fun inject(baseApp: BaseApp)
    fun inject(wrapper: SharedPrefWrapper)
    fun inject(network: NetworkWrapper)
}

@Module
class BaseModule constructor(private val application: Application) {
    @Provides
    fun provideContext(): Context = application.applicationContext

    @Provides
    fun provideSharedPreferences(): PreferenceProvider = SharedPrefWrapper()

    @Provides
    fun provideNetworkProvider(): NetworkProvider = NetworkWrapper()
}

open class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        getComponent(this).inject(this)
    }

    companion object {
        lateinit var appComponent: BaseComponent

        fun getComponent(app: Application): BaseComponent {
            if (!::appComponent.isInitialized) {
                appComponent = DaggerBaseComponent
                    .builder()
                    .baseModule(BaseModule(app))
                    .build()
            }
            return appComponent
        }
    }
}