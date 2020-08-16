package com.bragonya.daggerdemo.ui.mainlist

import com.bragonya.daggerdemo.network.PokeAPI
import com.bragonya.daggerdemo.repositories.PokeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class MainFragmentModule {

    @Provides
    fun mainViewModelProvider(repository: PokeRepository) = MainViewModel(repository)

    @Provides
    fun repositoryProvider(pokeAPI: PokeAPI) = PokeRepository(pokeAPI)
}