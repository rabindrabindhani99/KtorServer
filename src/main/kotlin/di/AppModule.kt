package com.rabindradev.di

import com.rabindradev.data.database.DatabaseFactory
import com.rabindradev.data.repositories.AuthRepository
import com.rabindradev.data.repositoriesImpl.AuthRepositoryImpl
import com.rabindradev.domain.services.UserService
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import javax.sql.DataSource

val appModule = module {
    single<DataSource> { DatabaseFactory.getDataSource() }
    single { Database.connect(get<DataSource>()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { UserService(get()) }
}