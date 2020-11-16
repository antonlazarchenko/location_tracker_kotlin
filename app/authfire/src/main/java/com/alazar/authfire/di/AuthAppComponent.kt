package com.alazar.authfire.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alazar.authfire.EmailAuthFragment
import com.alazar.authfire.PhoneAuthFragment
import com.alazar.authfire.viewmodel.EmailAuthViewModel
import com.alazar.authfire.viewmodel.PhoneAuthViewModel
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        FactoryModule::class,
    ]
)

interface AuthAppComponent {
    fun inject(viewModelFactory: ViewModelFactory)
    fun inject(fragment: EmailAuthFragment)
    fun inject(fragment: PhoneAuthFragment)
}

@Module
abstract class FactoryModule {

    @Binds
    @IntoMap
    @ViewModelKey(EmailAuthViewModel::class)
    internal abstract fun provideEmailAuthViewModel(viewModel: EmailAuthViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhoneAuthViewModel::class)
    internal abstract fun providePhoneAuthViewModel(viewModel: PhoneAuthViewModel) : ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

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
