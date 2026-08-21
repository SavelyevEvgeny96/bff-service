package automation.model

import java.time.Duration
import java.time.Instant

enum class ScenarioStatus { SUCCESS, FAILED, SKIPPED, CANCELLED }
data class ActionResult(val action: String, val status: ScenarioStatus, val duration: Duration, val error: String? = null)
data class ScenarioResult(val scenarioName: String, val status: ScenarioStatus, val startedAt: Instant, val finishedAt: Instant, val duration: Duration, val actionsExecuted: Int, val navigationCount: Int, val error: String? = null)
