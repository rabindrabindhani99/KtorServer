package com.rabindradev

import com.rabindradev.data.database.DatabaseFactory
import com.rabindradev.di.appModule
import com.rabindradev.presentation.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment.config)

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    installPlugins()
}


fun Application.installPlugins() {
    configureSerialization()
    configureSecurity()
    configureMonitoring()
    configureCORS()
    configureRouting()
}