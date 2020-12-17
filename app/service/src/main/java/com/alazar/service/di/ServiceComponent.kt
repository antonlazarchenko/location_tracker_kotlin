package com.alazar.service.di

import android.app.Application
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

class ServiceApp : Application() {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerServiceComponent
            .builder()
            .baseModule(BaseModule(this))
            .build()
    }

    companion object {
        lateinit var appComponent: ServiceComponent
    }
}