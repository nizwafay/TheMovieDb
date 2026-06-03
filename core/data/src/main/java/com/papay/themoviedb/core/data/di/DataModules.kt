package com.papay.themoviedb.core.data.di

import com.papay.themoviedb.core.database.di.databaseModule
import com.papay.themoviedb.core.network.di.networkModule

fun dataModules(accessToken: String) = listOf(
    coroutineModule,
    databaseModule,
    localDataSourceModule,
    networkModule(accessToken = accessToken),
    remoteModule,
    repositoryModule
)
