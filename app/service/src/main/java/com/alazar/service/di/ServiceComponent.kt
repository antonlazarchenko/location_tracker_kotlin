package com.alazar.service.di

import com.alazar.authfire.di.AuthUserModule
import com.alazar.base.di.BaseModule
import com.alazar.base.di.scope.ServiceScope
import com.alazar.service.FirebaseWorker
import com.alazar.service.RestartHelper
import com.alazar.service.TrackerService
import dagger.Component
import dagger.Module

@ServiceScope
@Component(
    modules = [
        MainModule::class,
    ]
)

interface ServiceComponent {
    fun inject(helper: RestartHelper)
    fun inject(worker: FirebaseWorker)
    fun inject(service: TrackerService)
}

@Module(includes = [AuthUserModule::class, BaseModule::class])
class MainModule

object ServiceComponentProvider {
    fun getComponent(): ServiceComponent {
        return DaggerServiceComponent
            .builder()
            .build()
    }
}