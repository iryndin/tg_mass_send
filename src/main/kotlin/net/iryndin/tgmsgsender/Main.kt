package net.iryndin.tgmsgsender

import net.iryndin.tgmsgsender.config.PersistenceConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@Import(PersistenceConfig::class)
class TgMassSenderApplication

fun main(args: Array<String>) {
    runApplication<TgMassSenderApplication>(*args)
}