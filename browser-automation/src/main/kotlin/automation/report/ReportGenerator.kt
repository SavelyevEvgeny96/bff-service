package automation.report

import automation.model.*
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

class ReportGenerator {
    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).enable(SerializationFeature.INDENT_OUTPUT)
    @Synchronized fun write(seed: Long, results: List<ScenarioResult>, started: java.time.Instant, directory: Path): String {
        Files.createDirectories(directory); val counts=ScenarioStatus.entries.associateWith { s->results.count { it.status==s } }; val duration=Duration.between(started, java.time.Instant.now())
        val text=buildString { appendLine("========== AUTOMATION REPORT =========="); appendLine(); appendLine("Seed: $seed"); appendLine(); appendLine("Total: ${results.size}"); ScenarioStatus.entries.forEach { appendLine("${it.name.lowercase().replaceFirstChar(Char::uppercase)}: ${counts[it]}") }; appendLine(); appendLine("Duration: ${format(duration)}"); appendLine(); appendLine("Scenarios:"); appendLine(); results.forEach { appendLine("[${label(it.status)}] ${it.scenarioName.padEnd(24)} ${"%.1f".format(it.duration.toMillis()/1000.0)}s") }; appendLine(); appendLine("Artifacts:"); appendLine(directory); appendLine(); appendLine("=======================================") }
        Files.writeString(directory.resolve("report.txt"),text); mapper.writeValue(directory.resolve("report.json").toFile(),mapOf("seed" to seed,"duration" to duration,"results" to results)); println(text); return text
    }
    private fun label(s: ScenarioStatus)=when(s){ScenarioStatus.SUCCESS->"OK";ScenarioStatus.FAILED->"FAIL";ScenarioStatus.SKIPPED->"SKIP";ScenarioStatus.CANCELLED->"CANCEL"}
    private fun format(d: Duration)="%02d:%02d:%02d".format(d.toHours(),d.toMinutesPart(),d.toSecondsPart())
}
