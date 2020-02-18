package net.iryndin.tgmsgsender.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = ["net.iryndin.tgmsgsender.db.repository"])
@EntityScan(basePackages = ["net.iryndin.tgmsgsender.db.entity"])
class PersistenceConfig