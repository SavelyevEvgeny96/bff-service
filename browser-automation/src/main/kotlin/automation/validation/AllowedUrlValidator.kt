package automation.validation

import automation.config.AllowedHost
import java.net.URI
import java.util.Locale

class AllowedUrlValidator(private val hosts: List<AllowedHost>, private val schemes: Set<String> = setOf("http", "https")) {
    fun isAllowed(value: String?): Boolean = runCatching { requireAllowed(value ?: ""); true }.getOrDefault(false)
    fun requireAllowed(value: String): URI {
        require(value.isNotBlank()) { "URL is blank" }
        val uri = URI(value)
        val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: throw IllegalArgumentException("URL scheme is missing")
        require(scheme in schemes && uri.isAbsolute) { "Scheme is not allowed: $scheme" }
        require(uri.rawUserInfo == null) { "User info is forbidden" }
        val host = uri.host?.trimEnd('.')?.lowercase(Locale.ROOT) ?: throw IllegalArgumentException("URL host is missing")
        require(hosts.any { rule ->
            val domain = rule.domain.trim().trimEnd('.').lowercase(Locale.ROOT)
            val hostMatches = host == domain || (rule.includeSubdomains && host.endsWith(".$domain"))
            hostMatches && (rule.ports.isEmpty() || uri.port in rule.ports)
        }) { "Host or port is not allowed: $host" }
        return uri
    }
}
