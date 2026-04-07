package ru.sogaz.site.bff.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("ru.sogaz.site.bff.service.properties")
class BffServiceApplication

fun main(args: Array<String>) {
    runApplication<BffServiceApplication>(*args)
}

fun <T> loggerFor(clazz: Class<T>): Logger = LoggerFactory.getLogger(clazz)
