package net.iryndin.tgmsgsender.db.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tg_mass_msg_error")
class TgMassMsgErrorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "create_ts", nullable = false)
    @CreationTimestamp
    var createTs: LocalDateTime = LocalDateTime.now()

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tg_mass_msg_id", nullable = false)
    var message: TgMassMsgEntity? = null

    @Column(name = "try_num", nullable = false)
    var tryNumber: Int = 1

    @Column(name = "http_status", nullable = false)
    var httpStatus: Int = 0

    @Column(name = "response", nullable = false)
    var response: String = ""
}