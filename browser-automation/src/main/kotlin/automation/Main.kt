package automation

import automation.config.ConfigLoader
import automation.engine.AutomationEngine
import automation.engine.StopController
import java.nio.file.Path
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.firstOrNull()?.equals("stop", true) == true) { StopController(Path.of("build/automation.stop")).createStopFile(); println("Emergency stop requested: build/automation.stop created"); return }
    val configPath=args.firstOrNull{it.startsWith("--config=")}?.substringAfter('=')?.let(Path::of)
    val scenarios=args.firstOrNull{it.startsWith("--scenario=")}?.substringAfter('=')?.split(',')?.map(String::trim)?.filter(String::isNotEmpty)?.toSet().orEmpty()
    exitProcess(AutomationEngine(ConfigLoader.load(configPath),scenarios).run())
}
