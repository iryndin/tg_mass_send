package net.iryndin.tgmsgsender.db.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

enum class TgMsgDeliveryStatus {
    TODO,
    IN_PROGRESS,
    RETRY,
    ERROR,
    OK
}

@Entity
@Table(name = "tg_mass_msg")
class TgMassMsgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "create_ts", nullable = false)
    @CreationTimestamp
    var createTs: LocalDateTime = LocalDateTime.now()

    @Column(name = "update_ts", nullable = false)
    @UpdateTimestamp
    var updateTs: LocalDateTime = LocalDateTime.now()

    @Column(name = "version", nullable = false)
    @Version
    var version: Int = 0

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tg_mass_send_job_id", nullable = false)
    var sendJob: TgMassSendJobEntity? = null

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: TgMsgDeliveryStatus = TgMsgDeliveryStatus.TODO

    @Column(name = "user_id", nullable = false)
    var userId: Long? = null

    override fun toString(): String {
        return "TgMassMsgEntity(id=$id, " +
                "createTs=$createTs, " +
                "updateTs=$updateTs, " +
                "version=$version, " +
                "sendJob.id=${sendJob?.id}, status=$status, userId=$userId)"
    }
}