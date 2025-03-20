package com.rabindradev.di

import com.rabindradev.data.database.DatabaseFactory
import com.rabindradev.data.repositories.AuthRepository
import com.rabindradev.data.repositoriesImpl.AuthRepositoryImpl
import com.rabindradev.domain.serviceImpls.AdminServiceImpl
import com.rabindradev.domain.serviceImpls.UserServiceImpl
import com.rabindradev.domain.services.AdminService
import com.rabindradev.domain.services.UserService
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import javax.sql.DataSource

val appModule = module {
    single<DataSource> { DatabaseFactory.getDataSource() }
    single { Database.connect(get<DataSource>()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }
    single<AdminService> { AdminServiceImpl(get()) }
}