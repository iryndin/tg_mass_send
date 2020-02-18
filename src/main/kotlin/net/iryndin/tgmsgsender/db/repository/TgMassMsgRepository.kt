package net.iryndin.tgmsgsender.db.repository

import net.iryndin.tgmsgsender.db.entity.TgMassMsgEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TgMassMsgRepository: BaseRepository<TgMassMsgEntity, Long> {

    @Query(
        value = """
            select * from tg_mass_msg
            where tg_mass_send_job_id = ?1
            order by user_id 
        """,
        nativeQuery = true
    )
    fun getMessagesByJobId(jobId: Long): List<TgMassMsgEntity>

    @Query(
        value = """
            select * from tg_mass_msg
            where status in ?1
            order by update_ts
            limit ?2
        """,
        nativeQuery = true
    )
    fun getMessagesToBeSent(statuses: List<String>, limit: Int): List<TgMassMsgEntity>

    @Modifying(clearAutomatically = true)
    @Query(
            value = """
                update tg_mass_msg 
                set status = ?1, update_ts = current_timestamp
                where status = ?2 and update_ts < ?3
                """,
            nativeQuery = true
    )
    fun clearInProgressStatus(statusNew: String, statusOld: String, threshold: LocalDateTime)
}