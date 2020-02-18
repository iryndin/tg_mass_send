package net.iryndin.tgmsgsender.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BaseRepository <T, ID>: CrudRepository<T, ID>, JpaRepository<T, ID>