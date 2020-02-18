package net.iryndin.tgmsgsender.db.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "tg_mass_send_job")
class TgMassSendJobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "create_ts", nullable = false)
    @CreationTimestamp
    var createTs: LocalDateTime = LocalDateTime.now()

    @Column(name = "txt", nullable = false)
    var text: String? = null
}