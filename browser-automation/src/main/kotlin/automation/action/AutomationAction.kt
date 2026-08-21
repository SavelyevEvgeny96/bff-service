package automation.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import automation.engine.AutomationContext
import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole

enum class ActionType { OPEN_PAGE, CLICK, FILL, CHECK, UNCHECK, SELECT, ASSERT_TEXT, ASSERT_URL, WAIT_FOR, BACK, OPEN_TAB, CLOSE_TAB, START_PROCESS, STOP_PROCESS, WAIT_PROCESS }
enum class SelectorType { TEST_ID, ROLE, LABEL, CSS, TEXT }
@JsonIgnoreProperties(ignoreUnknown = true)
data class SelectorConfig(val type: SelectorType = SelectorType.CSS, val value: String = "", val name: String? = null) {
    fun locator(page: Page): Locator = when (type) {
        SelectorType.TEST_ID -> page.getByTestId(value)
        SelectorType.ROLE -> page.getByRole(AriaRole.valueOf(value.uppercase()), Page.GetByRoleOptions().setName(name))
        SelectorType.LABEL -> page.getByLabel(value)
        SelectorType.CSS -> page.locator(value)
        SelectorType.TEXT -> page.getByText(value)
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
data class ActionConfig(
    val type: ActionType = ActionType.WAIT_FOR, val url: String? = null, val selector: SelectorConfig? = null,
    val value: String? = null, val dataKey: String? = null, val contains: String? = null,
    val process: String? = null, val timeout: Long = 10_000
)
fun interface ActionHandler { fun execute(action: ActionConfig, context: AutomationContext) }
