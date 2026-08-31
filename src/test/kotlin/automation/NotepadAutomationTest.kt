package automation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.random.Random
import kotlin.test.assertEquals

class NotepadAutomationTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `switches windows then copies saves and closes`() {
        val source = tempDir.resolve("source.txt").also { it.writeText("copied text") }
        val output = tempDir.resolve("output.txt")
        val desktop = RecordingDesktopController()
        val config = AutomationConfig(source, output, switches = 2, minDelayMs = 10, maxDelayMs = 10, notepadWaitMs = 20)

        NotepadAutomation(desktop, Random(1)).run(config)

        assertEquals(
            listOf(
                "wait:10", "switch", "wait:10", "switch", "clipboard:copied text",
                "open:$output", "wait:20", "paste", "save", "wait:300", "close",
            ),
            desktop.events,
        )
    }

    private class RecordingDesktopController : DesktopController {
        val events = mutableListOf<String>()
        override fun switchWindow() { events += "switch" }
        override fun putOnClipboard(text: String) { events += "clipboard:$text" }
        override fun openNotepad(file: Path) { events += "open:$file" }
        override fun paste() { events += "paste" }
        override fun save() { events += "save" }
        override fun closeWindow() { events += "close" }
        override fun waitFor(milliseconds: Long) { events += "wait:$milliseconds" }
    }
}
