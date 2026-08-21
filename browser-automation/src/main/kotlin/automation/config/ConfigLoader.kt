package automation.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.file.Files
import java.nio.file.Path

object ConfigLoader {
    private val mapper = jacksonObjectMapper(YAMLFactory()).registerModule(JavaTimeModule()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
    fun load(path: Path? = null): AutomationConfig {
        val text = if (path != null) Files.readString(path) else ConfigLoader::class.java.getResourceAsStream("/application.yml")!!.bufferedReader().readText()
        return mapper.readValue<RootConfig>(expandEnvironment(text)).automation
    }
    private fun expandEnvironment(value: String) = Regex("\\$\\{([A-Z0-9_]+)}").replace(value) { System.getenv(it.groupValues[1]) ?: error("Environment variable ${it.groupValues[1]} is not set") }
}
