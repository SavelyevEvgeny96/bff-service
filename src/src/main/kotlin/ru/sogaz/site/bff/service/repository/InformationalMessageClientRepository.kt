package ru.sogaz.site.bff.service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff.service.model.InformationalMessageClient
import java.util.UUID

interface InformationalMessageClientRepository : JpaRepository<InformationalMessageClient, UUID>
