package automation

import java.nio.file.Files
import java.nio.file.Path

/** Command-line configuration for one automation run. */
data class AutomationConfig(
    val source: Path,
    val output: Path,
    val switches: Int = 3,
    val minSwitchDelayMs: Long = 500,
    val maxSwitchDelayMs: Long = 1_500,
    val notepadWaitMs: Long = 1_500,
    val minDurationMinutes: Long = 180,
    val maxDurationMinutes: Long = 300,
    val typoPercent: Double = 2.0,
    val saveEveryCharacters: Int = 500,
) {
    fun validate(): AutomationConfig {
        require(Files.isRegularFile(source)) { "Source file does not exist: $source" }
        require(output.parent == null || Files.isDirectory(output.parent)) {
            "Output directory does not exist: ${output.parent}"
        }
        require(source.toAbsolutePath().normalize() != output.toAbsolutePath().normalize()) {
            "Source and output files must be different"
        }
        require(switches >= 0) { "Switch count cannot be negative" }
        require(minSwitchDelayMs >= 0 && maxSwitchDelayMs >= minSwitchDelayMs) { "Invalid switch delay range" }
        require(notepadWaitMs >= 0) { "Notepad wait cannot be negative" }
        require(minDurationMinutes >= 0 && maxDurationMinutes >= minDurationMinutes) { "Invalid duration range" }
        require(typoPercent in 0.0..25.0) { "Typo percent must be between 0 and 25" }
        require(saveEveryCharacters > 0) { "Save interval must be positive" }
        require(Files.readString(source).isNotEmpty()) { "Source file is empty" }
        return this
    }

    companion object {
        private val known = setOf(
            "--source", "--output", "--switches", "--min-switch-delay-ms", "--max-switch-delay-ms",
            "--notepad-wait-ms", "--min-duration-minutes", "--max-duration-minutes", "--typo-percent",
            "--save-every-characters",
        )

        fun fromArgs(args: Array<String>): AutomationConfig {
            require(args.size % 2 == 0) { "Arguments must be provided as --name value pairs" }
            val values = args.toList().chunked(2).associate { pair ->
                require(pair[0].startsWith("--")) { "Expected option name, got: ${pair[0]}" }
                pair[0] to pair[1]
            }
            require(values.keys.all(known::contains)) { "Unknown argument: ${values.keys.first { it !in known }}" }

            return AutomationConfig(
                source = Path.of(values.required("--source")),
                output = Path.of(values.required("--output")),
                switches = values["--switches"]?.toInt() ?: 3,
                minSwitchDelayMs = values["--min-switch-delay-ms"]?.toLong() ?: 500,
                maxSwitchDelayMs = values["--max-switch-delay-ms"]?.toLong() ?: 1_500,
                notepadWaitMs = values["--notepad-wait-ms"]?.toLong() ?: 1_500,
                minDurationMinutes = values["--min-duration-minutes"]?.toLong() ?: 180,
                maxDurationMinutes = values["--max-duration-minutes"]?.toLong() ?: 300,
                typoPercent = values["--typo-percent"]?.toDouble() ?: 2.0,
                saveEveryCharacters = values["--save-every-characters"]?.toInt() ?: 500,
            ).validate()
        }

        private fun Map<String, String>.required(name: String): String =
            get(name) ?: throw IllegalArgumentException("Required argument is missing: $name")
    }
}
