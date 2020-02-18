package net.iryndin.tgmsgsender.db.repository

import net.iryndin.tgmsgsender.db.entity.TgMassMsgErrorEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TgMassMsgErrorRepository: BaseRepository<TgMassMsgErrorEntity, Long> {

    @Query(
        value = """
            select * from tg_mass_msg_error
            where tg_mass_msg_error.tg_mass_msg_id = ?1
            order by create_ts desc
            limit 1 
        """,
        nativeQuery = true
    )
    fun getLatestRetry(messageId: Long): Optional<TgMassMsgErrorEntity>
}