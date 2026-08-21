package automation.engine

import automation.action.*
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.LoadState
import com.microsoft.playwright.options.WaitForSelectorState
import org.slf4j.LoggerFactory
import java.time.Duration

class ActionExecutor {
    private val log = LoggerFactory.getLogger(javaClass)
    private val handlers: Map<ActionType, ActionHandler> = mapOf(
        ActionType.OPEN_PAGE to ActionHandler { a, c -> c.beforeNavigation(); c.browserManager!!.navigate(page(c), required(a.url, "url")) },
        ActionType.CLICK to ActionHandler { a, c ->
            val locator = selector(a, page(c)); locator.getAttribute("href")?.let { href -> c.validator.requireAllowed(java.net.URI(page(c).url()).resolve(href).toString()); c.beforeNavigation() }
            val oldUrl = page(c).url(); locator.click(); page(c).waitForLoadState(LoadState.DOMCONTENTLOADED); if (page(c).url() != oldUrl) { if (locator.getAttribute("href") == null) c.beforeNavigation(); c.validator.requireAllowed(page(c).url()) }
        },
        ActionType.FILL to ActionHandler { a, c -> selector(a, page(c)).fill(resolveValue(a, c)) },
        ActionType.CHECK to ActionHandler { a, c -> selector(a, page(c)).check() },
        ActionType.UNCHECK to ActionHandler { a, c -> selector(a, page(c)).uncheck() },
        ActionType.SELECT to ActionHandler { a, c -> selector(a, page(c)).selectOption(resolveValue(a, c)) },
        ActionType.ASSERT_TEXT to ActionHandler { a, c -> require(selector(a, page(c)).textContent().orEmpty().contains(required(a.value, "value"))) { "Expected text not found" } },
        ActionType.ASSERT_URL to ActionHandler { a, c -> require(page(c).url().contains(required(a.contains, "contains"))) { "URL assertion failed: ${page(c).url()}" } },
        ActionType.WAIT_FOR to ActionHandler { a, c -> selector(a, page(c)).waitFor(com.microsoft.playwright.Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(a.timeout.toDouble())) },
        ActionType.BACK to ActionHandler { _, c -> c.beforeNavigation(); page(c).goBack(); page(c).waitForLoadState(LoadState.DOMCONTENTLOADED); c.validator.requireAllowed(page(c).url()) },
        ActionType.OPEN_TAB to ActionHandler { a, c -> c.beforeNavigation(); val p = c.browserContext!!.newPage(); c.browserManager!!.navigate(p, required(a.url, "url")); c.currentPage = p },
        ActionType.CLOSE_TAB to ActionHandler { _, c -> val p = page(c); p.close(); c.currentPage = c.browserContext!!.pages().lastOrNull { !it.isClosed } ?: error("No page remains") },
        ActionType.START_PROCESS to ActionHandler { a, c -> c.processManager.start(required(a.process, "process")) },
        ActionType.STOP_PROCESS to ActionHandler { a, c -> require(c.processManager.stop(required(a.process, "process"), Duration.ofMillis(a.timeout))) { "Process did not stop" } },
        ActionType.WAIT_PROCESS to ActionHandler { a, c -> require(c.processManager.waitForStart(required(a.process, "process"), Duration.ofMillis(a.timeout))) { "Process did not start" } }
    )
    fun execute(action: ActionConfig, context: AutomationContext) {
        context.beforeAction(); val started = System.nanoTime()
        try { handlers[action.type]?.execute(action, context) ?: error("No handler for ${action.type}"); log.info("action={} url={} selector={} result=SUCCESS durationMs={} actionCount={} navigationCount={}", action.type, action.url ?: context.currentPage?.url(), action.selector, (System.nanoTime()-started)/1_000_000, context.actionCounter, context.navigationCounter) }
        catch (e: Exception) { log.error("action={} result=FAILED error={}", action.type, e.message); throw e }
    }
    private fun page(c: AutomationContext) = c.currentPage ?: error("Browser is not started")
    private fun selector(a: ActionConfig, p: Page) = (a.selector ?: error("selector is required")).locator(p)
    private fun resolveValue(a: ActionConfig, c: AutomationContext) = a.dataKey?.let { c.configuration.testData[it] ?: error("Unknown test-data key '$it'") } ?: required(a.value, "value")
    private fun required(value: String?, field: String) = value?.takeIf(String::isNotBlank) ?: error("$field is required")
}
