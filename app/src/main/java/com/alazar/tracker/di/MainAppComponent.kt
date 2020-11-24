package com.alazar.tracker.di

import android.app.Application
import com.alazar.authfire.di.AuthAppComponent
import com.alazar.authfire.di.DaggerAuthAppComponent
import com.alazar.authfire.model.UserModel
import com.alazar.base.core.UserManagerInterface
import com.alazar.base.di.BaseComponent
import com.alazar.base.di.DaggerBaseComponent
import com.alazar.base.di.scope.AuthScope
import com.alazar.tracker.MainActivity
import dagger.Component
import dagger.Module
import dagger.Provides

@AuthScope
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

@Module
class MainModule {
    @Provides
    fun provideUserManager() : UserManagerInterface = UserModel()
}

class MainApp : Application() {
    private lateinit var appComponent: MainAppComponent

    private fun isInitialized() = this::appComponent.isInitialized

    fun getComponent(): MainAppComponent {
        if (!isInitialized()) {
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
