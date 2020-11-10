package com.alazar.authfire.di

import android.app.Application
import com.alazar.authfire.EmailAuthFragment
import com.alazar.authfire.PhoneAuthFragment
import com.alazar.authfire.model.UserModel
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AuthAppModule::class
    ]
)

interface AuthAppComponent {
    fun inject(viewModelFactory: ViewModelFactory)
    fun inject(fragment: EmailAuthFragment)
    fun inject(fragment: PhoneAuthFragment)
}

@Module
class AuthAppModule {

    @Provides
    fun provideUserModel(): UserModel = UserModel()

}

class AuthApp : Application() {
    private lateinit var appComponent: AuthAppComponent

    private fun isInitialized() = this::appComponent.isInitialized

    fun getComponent(): AuthAppComponent {
        if (!isInitialized()) {
            initDaggerComponent()
        }
        return appComponent
    }

    private fun initDaggerComponent() {
        appComponent = DaggerAuthAppComponent
            .builder()
            .build()
    }
}
