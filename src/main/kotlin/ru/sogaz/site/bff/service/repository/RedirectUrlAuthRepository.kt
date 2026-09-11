package ru.sogaz.site.bff.service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff.service.model.RedirectUrlAuth
import java.util.UUID

interface RedirectUrlAuthRepository : JpaRepository<RedirectUrlAuth, UUID>
