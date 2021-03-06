package com.alazar.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alazar.authfire.di.AuthUserModule
import com.alazar.base.core.NetworkProvider
import com.alazar.base.di.BaseApp
import com.alazar.base.di.BaseComponent
import com.alazar.base.di.BaseModule
import com.alazar.base.di.scope.MapScope
import com.alazar.base.di.viewmodel.ViewModelFactory
import com.alazar.base.di.viewmodel.ViewModelKey
import com.alazar.map.MapViewModel
import com.alazar.map.MapsFragment
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap

@MapScope
@Component(
    dependencies = [
        BaseComponent::class
    ],
    modules = [
        MapModule::class,
        FactoryModule::class,
    ]
)

interface MapComponent {
    fun provideNetworkProvider() : NetworkProvider
    fun inject(fragment: MapsFragment)
    fun inject(viewModel: MapViewModel)
}

@Module(includes = [AuthUserModule::class])
class MapModule


@Module
abstract class FactoryModule {
    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    internal abstract fun provideMapViewModel(viewModel: MapViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

object MapComponentProvider {
    fun getComponent(): MapComponent {
        return DaggerMapComponent
            .builder()
            .baseComponent(BaseApp.appComponent)
            .build()
    }
}