package com.alazar.authfire.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alazar.authfire.EmailFragment
import com.alazar.authfire.PhoneFragment
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.authfire.model.UserModel
import com.alazar.authfire.viewmodel.EmailViewModel
import com.alazar.authfire.viewmodel.PhoneViewModel
import com.alazar.base.di.BaseComponent
import com.alazar.base.di.DaggerBaseComponent
import com.alazar.base.di.scope.AuthScope
import com.alazar.base.di.viewmodel.ViewModelFactory
import com.alazar.base.di.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@AuthScope
@Component(
    dependencies = [BaseComponent::class],
    modules = [
        FactoryModule::class,
        AuthUserModule::class,
    ]
)

interface AuthAppComponent {
    fun inject(viewModelFactory: ViewModelFactory)
    fun inject(fragment: EmailFragment)
    fun inject(fragment: PhoneFragment)
}

@Module
class AuthUserModule {
    @Provides
    fun provideUserManager() : UserManagerInterface = UserModel()
}

@Module
abstract class FactoryModule {

    @Binds
    @IntoMap
    @ViewModelKey(EmailViewModel::class)
    internal abstract fun provideEmailAuthViewModel(viewModel: EmailViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhoneViewModel::class)
    internal abstract fun providePhoneAuthViewModel(viewModel: PhoneViewModel) : ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}

class AuthApp : Application() {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAuthAppComponent
            .builder()
            .baseComponent(DaggerBaseComponent.builder().build())
            .build()
    }

    companion object {
        lateinit var appComponent: AuthAppComponent
    }
}
