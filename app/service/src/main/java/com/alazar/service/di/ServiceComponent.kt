package com.alazar.service.di

import android.app.Application
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.authfire.model.UserModel
import com.alazar.base.di.scope.ServiceScope
import com.alazar.service.RestartHelper
import dagger.Component
import dagger.Module
import dagger.Provides

@ServiceScope
@Component(
    modules = [
        MainModule::class,
    ]
)

interface ServiceComponent {
    fun inject(helper: RestartHelper)
}

@Module
class MainModule {
    @Provides
    fun provideUserManager() : UserManagerInterface = UserModel()
}

class ServiceApp : Application() {
    private lateinit var appComponent: ServiceComponent

    fun getComponent(): ServiceComponent {
        if (!this::appComponent.isInitialized) {
            initDaggerComponent()
        }
        return appComponent
    }

    private fun initDaggerComponent() {
        appComponent = DaggerServiceComponent
            .builder()
            .build()
    }
}