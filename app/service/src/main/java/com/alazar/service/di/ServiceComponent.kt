package com.alazar.service.di

import android.app.Application
import com.alazar.authfire.di.AuthUserModule
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

@Module(includes = [AuthUserModule::class])
class MainModule

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