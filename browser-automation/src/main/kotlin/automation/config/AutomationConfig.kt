package automation.config

import automation.action.ActionConfig
import java.time.Duration

data class RootConfig(val automation: AutomationConfig = AutomationConfig())
data class AutomationConfig(
    val dryRun: Boolean = false, val submitEnabled: Boolean = false,
    val randomizeScenarioOrder: Boolean = false, val randomSeed: Long = 123456,
    val browser: BrowserConfig = BrowserConfig(), val limits: LimitsConfig = LimitsConfig(),
    val artifacts: ArtifactsConfig = ArtifactsConfig(), val processes: ProcessConfig = ProcessConfig(),
    val allowedHosts: List<AllowedHost> = emptyList(), val testData: Map<String, String> = emptyMap(),
    val scenarios: LinkedHashMap<String, ScenarioConfig> = linkedMapOf()
)
data class BrowserConfig(val headless: Boolean = false, val profileDir: String = "build/browser-profile")
data class LimitsConfig(val maxRunDuration: Duration = Duration.ofMinutes(10), val maxNavigationCount: Int = 30, val maxActionCount: Int = 100)
data class ArtifactsConfig(val directory: String = "build/test-results/browser")
data class AllowedHost(val domain: String = "", val includeSubdomains: Boolean = false, val ports: Set<Int> = emptySet())
data class ProcessConfig(val cleanupOnExit: Boolean = true, val allowed: Map<String, ProcessDefinition> = emptyMap())
data class ProcessDefinition(val executable: String = "", val args: List<String> = emptyList())
data class ScenarioConfig(val enabled: Boolean = true, val actions: List<ActionConfig> = emptyList())
