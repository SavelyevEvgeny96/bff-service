package automation.engine

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean

class StopController(private val stopFile: Path) {
    val stopRequested = AtomicBoolean(false)
    fun initialize() { Files.createDirectories(stopFile.parent); Files.deleteIfExists(stopFile) }
    fun requestStop() { stopRequested.set(true) }
    fun check() { if (Files.exists(stopFile)) stopRequested.set(true); check(!stopRequested.get()) { "Emergency stop requested" } }
    fun createStopFile() { Files.createDirectories(stopFile.parent); if (!Files.exists(stopFile)) Files.createFile(stopFile) }
}
