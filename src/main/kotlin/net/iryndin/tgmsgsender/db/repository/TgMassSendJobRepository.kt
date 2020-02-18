package net.iryndin.tgmsgsender.db.repository

import net.iryndin.tgmsgsender.db.entity.TgMassSendJobEntity
import org.springframework.stereotype.Repository

@Repository
interface TgMassSendJobRepository: BaseRepository<TgMassSendJobEntity, Long>