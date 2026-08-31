package automation

import java.nio.file.Files
import kotlin.random.Random

class NotepadAutomation(
    private val desktop: DesktopController,
    private val random: Random = Random.Default,
) {
    fun run(config: AutomationConfig) {
        val sourceText = Files.readString(config.source)
        // Creating the file prevents Notepad from showing a blocking "create file" dialog.
        if (Files.notExists(config.output)) {
            Files.createFile(config.output)
        }

        repeat(config.switches) {
            desktop.waitFor(randomDelay(config))
            desktop.switchWindow()
        }

        desktop.putOnClipboard(sourceText)
        desktop.openNotepad(config.output)
        desktop.waitFor(config.notepadWaitMs)
        desktop.paste()
        desktop.save()
        desktop.waitFor(300)
        desktop.closeWindow()
    }

    private fun randomDelay(config: AutomationConfig): Long =
        if (config.minDelayMs == config.maxDelayMs) config.minDelayMs
        else random.nextLong(config.minDelayMs, config.maxDelayMs + 1)
}
