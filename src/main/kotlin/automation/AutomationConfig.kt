package automation

import java.nio.file.Files
import java.nio.file.Path

/** Command-line configuration for one automation run. */
data class AutomationConfig(
    val source: Path,
    val output: Path,
    val switches: Int = 3,
    val minDelayMs: Long = 500,
    val maxDelayMs: Long = 1_500,
    val notepadWaitMs: Long = 1_200,
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
        require(minDelayMs >= 0) { "Minimum delay cannot be negative" }
        require(maxDelayMs >= minDelayMs) { "Maximum delay cannot be less than minimum delay" }
        require(notepadWaitMs >= 0) { "Notepad wait cannot be negative" }
        return this
    }

    companion object {
        fun fromArgs(args: Array<String>): AutomationConfig {
            val values = args.toList().chunked(2).associate { pair ->
                require(pair.size == 2 && pair[0].startsWith("--")) {
                    "Arguments must be provided as --name value pairs"
                }
                pair[0] to pair[1]
            }
            val known = setOf(
                "--source", "--output", "--switches", "--min-delay-ms", "--max-delay-ms", "--notepad-wait-ms",
            )
            require(values.keys.all(known::contains)) { "Unknown argument: ${values.keys.first { it !in known }}" }

            return AutomationConfig(
                source = Path.of(values.required("--source")),
                output = Path.of(values.required("--output")),
                switches = values["--switches"]?.toInt() ?: 3,
                minDelayMs = values["--min-delay-ms"]?.toLong() ?: 500,
                maxDelayMs = values["--max-delay-ms"]?.toLong() ?: 1_500,
                notepadWaitMs = values["--notepad-wait-ms"]?.toLong() ?: 1_200,
            ).validate()
        }

        private fun Map<String, String>.required(name: String): String =
            get(name) ?: throw IllegalArgumentException("Required argument is missing: $name")
    }
}
