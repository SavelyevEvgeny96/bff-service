package automation.scenario

import automation.action.ActionConfig
import automation.engine.ActionExecutor
import automation.engine.AutomationContext

interface BrowserScenario { val name: String; fun actions(context: AutomationContext): List<ActionConfig>; fun run(context: AutomationContext, executor: ActionExecutor) = actions(context).forEach { executor.execute(it, context) } }
class ConfiguredScenario(override val name: String, private val configuredActions: List<ActionConfig>) : BrowserScenario { override fun actions(context: AutomationContext) = configuredActions }
