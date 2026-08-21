package automation.process

import automation.config.ProcessDefinition
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class WindowsProcessManager(private val allowed: Map<String, ProcessDefinition>) {
    private val log = LoggerFactory.getLogger(javaClass)
    val startedProcesses: MutableMap<String, ProcessHandle> = ConcurrentHashMap()
    fun start(id: String): ProcessHandle {
        require(id !in startedProcesses || !startedProcesses.getValue(id).isAlive) { "Process '$id' is already running" }
        val definition = allowed[id] ?: throw IllegalArgumentException("Process '$id' is not allowlisted")
        require(definition.executable.isNotBlank()) { "Executable for '$id' is blank" }
        val process = ProcessBuilder(listOf(definition.executable) + definition.args).start()
        return process.toHandle().also { startedProcesses[id] = it; log.info("process={} action=start pid={} result=SUCCESS", id, it.pid()) }
    }
    fun find(id: String): ProcessHandle? = startedProcesses[id]
    fun info(id: String): ProcessHandle.Info? = find(id)?.info()
    fun isRunning(id: String) = find(id)?.isAlive == true
    fun stop(id: String, timeout: Duration = Duration.ofSeconds(5)): Boolean {
        val handle = find(id) ?: return false
        if (handle.isAlive) { handle.destroy(); if (!waitForExit(id, timeout)) { handle.destroyForcibly(); waitForExit(id, timeout) } }
        startedProcesses.remove(id, handle); log.info("process={} action=stop pid={} result={}", id, handle.pid(), !handle.isAlive); return !handle.isAlive
    }
    fun waitForStart(id: String, timeout: Duration) = poll(timeout) { isRunning(id) }
    fun waitForExit(id: String, timeout: Duration) = poll(timeout) { !isRunning(id) }
    fun cleanup() = startedProcesses.keys.toList().forEach { runCatching { stop(it) } }
    private fun poll(timeout: Duration, condition: () -> Boolean): Boolean {
        val deadline = System.nanoTime() + timeout.toNanos()
        while (System.nanoTime() < deadline) { if (condition()) return true; Thread.sleep(200) }
        return condition()
    }
}
