package automation

import java.nio.file.Files
import kotlin.math.roundToLong
import kotlin.random.Random

class NotepadAutomation(
    private val desktop: DesktopController,
    private val random: Random = Random.Default,
) {
    fun run(config: AutomationConfig) {
        val characters = Files.readString(config.source).codePoints().toArray().map { Character.toString(it) }
        if (Files.notExists(config.output)) Files.createFile(config.output)

        repeat(config.switches) {
            desktop.waitFor(randomLong(config.minSwitchDelayMs, config.maxSwitchDelayMs))
            desktop.switchWindow()
        }

        desktop.openNotepad(config.output)
        desktop.waitFor(config.notepadWaitMs)
        desktop.clearDocument()

        val targetDurationMs = randomLong(config.minDurationMinutes, config.maxDurationMinutes) * 60_000
        val delays = distributedDelays(characters.size, targetDurationMs)
        val possibleMistakes = characters.filter { it.firstOrNull()?.isLetter() == true }.ifEmpty { listOf("x") }
        characters.forEachIndexed { index, character ->
            if (random.nextDouble(100.0) < config.typoPercent) {
                desktop.typeText(possibleMistakes.random(random))
                desktop.waitFor(randomLong(80, 450))
                desktop.backspace()
                desktop.waitFor(randomLong(100, 700))
            }
            desktop.typeText(character)
            desktop.waitFor(delays[index])
            if ((index + 1) % config.saveEveryCharacters == 0) desktop.save()
        }

        desktop.save()
        desktop.waitFor(500)
        desktop.closeWindow()
    }

    private fun distributedDelays(count: Int, totalMs: Long): LongArray {
        val weights = DoubleArray(count) { random.nextDouble(0.55, 1.45) }
        val weightSum = weights.sum()
        val delays = LongArray(count) { (totalMs * weights[it] / weightSum).roundToLong() }
        delays[delays.lastIndex] += totalMs - delays.sum()
        return delays
    }

    private fun randomLong(min: Long, max: Long): Long = if (min == max) min else random.nextLong(min, max + 1)
}
