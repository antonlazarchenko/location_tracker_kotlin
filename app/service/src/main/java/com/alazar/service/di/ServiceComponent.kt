package com.alazar.service.di

import android.content.Context
import com.alazar.authfire.di.AuthUserModule
import com.alazar.base.core.NetworkProvider
import com.alazar.base.di.BaseApp
import com.alazar.base.di.BaseComponent
import com.alazar.base.di.scope.ServiceScope
import com.alazar.service.*
import dagger.Component
import dagger.Module
import dagger.Provides

@ServiceScope
@Component(
    dependencies = [
        BaseComponent::class
    ],
    modules = [
        MainModule::class,
    ]
)

interface ServiceComponent {
    fun provideContext(): Context
    fun provideNetworkProvider(): NetworkProvider
    fun inject(helper: RestartHelper)
    fun inject(worker: FirebaseWorker)
    fun inject(service: TrackerService)
    fun inject(broadcast: ServiceRestart)
}

@Module(includes = [AuthUserModule::class])
class MainModule {
    @Provides
    fun provideRestartHelper(): RestartHelperInterface = RestartHelper()
}

object ServiceComponentProvider {
    fun getComponent(): ServiceComponent {
        return DaggerServiceComponent
            .builder()
            .baseComponent(BaseApp.appComponent)
            .build()
    }
}