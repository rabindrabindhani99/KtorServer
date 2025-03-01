package com.rabindradev.di

import com.rabindradev.data.database.DatabaseFactory
import com.rabindradev.data.repositories.UserRepository
import com.rabindradev.data.repositoriesImpl.UserRepositoryImpl
import com.rabindradev.domain.services.UserService
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import javax.sql.DataSource

val appModule = module {
    single<DataSource> { DatabaseFactory.getDataSource() }
    single { Database.connect(get<DataSource>()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { UserService(get()) }
}