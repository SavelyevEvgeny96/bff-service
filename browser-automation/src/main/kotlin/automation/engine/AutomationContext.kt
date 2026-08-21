package automation.engine

import automation.browser.BrowserManager
import automation.config.AutomationConfig
import automation.model.ScenarioResult
import automation.process.WindowsProcessManager
import automation.validation.AllowedUrlValidator
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class AutomationContext(val configuration: AutomationConfig, val startTime: Instant, val randomSeed: Long, val artifactDirectory: Path,
    val validator: AllowedUrlValidator, val processManager: WindowsProcessManager, val browserManager: BrowserManager?, val stopController: StopController) {
    val navigationCounter = AtomicInteger(); val actionCounter = AtomicInteger(); val stopRequested: AtomicBoolean get() = stopController.stopRequested
    val results = CopyOnWriteArrayList<ScenarioResult>(); val startedProcesses get() = processManager.startedProcesses
    val playwright: Playwright? get() = browserManager?.playwright; val browserContext: BrowserContext? get() = browserManager?.context
    var currentPage: Page? get() = browserManager?.currentPage; set(value) { if (browserManager != null) browserManager.currentPage = value }
    fun beforeAction() { stopController.check(); require(Duration.between(startTime, Instant.now()) <= configuration.limits.maxRunDuration) { "Maximum run duration exceeded" }; require(actionCounter.incrementAndGet() <= configuration.limits.maxActionCount) { "Maximum action count exceeded" } }
    fun beforeNavigation() { require(navigationCounter.incrementAndGet() <= configuration.limits.maxNavigationCount) { "Maximum navigation count exceeded" } }
}
