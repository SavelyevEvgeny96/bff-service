package automation.browser

import automation.config.BrowserConfig
import automation.validation.AllowedUrlValidator
import com.microsoft.playwright.*
import com.microsoft.playwright.options.LoadState
import com.microsoft.playwright.options.RequestResourceType
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

class BrowserManager(private val config: BrowserConfig, private val validator: AllowedUrlValidator, private val artifacts: Path) : AutoCloseable {
    private val log = LoggerFactory.getLogger(javaClass)
    var playwright: Playwright? = null; private set
    var context: BrowserContext? = null; private set
    var currentPage: Page? = null
    fun start(): Page {
        Files.createDirectories(Path.of(config.profileDir)); Files.createDirectories(artifacts)
        playwright = Playwright.create()
        context = playwright!!.chromium().launchPersistentContext(Path.of(config.profileDir), BrowserType.LaunchPersistentContextOptions().setHeadless(config.headless))
        context!!.route("**/*") { route ->
            val request = route.request()
            if (request.isNavigationRequest && request.resourceType() == RequestResourceType.DOCUMENT && !validator.isAllowed(request.url())) {
                log.error("action=navigation url={} result=BLOCKED", request.url()); route.abort()
            } else route.resume()
        }
        context!!.onPage { page -> monitor(page); if (currentPage == null) currentPage = page }
        context!!.pages().forEach(::monitor)
        context!!.tracing().start(Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true))
        return context!!.pages().firstOrNull() ?: context!!.newPage().also { monitor(it) }.also { currentPage = it }
    }
    private fun monitor(page: Page) {
        page.onFrameNavigated { frame -> if (frame == page.mainFrame() && frame.url() != "about:blank" && !validator.isAllowed(frame.url())) { log.error("url={} result=BLOCKED_AFTER_NAVIGATION", frame.url()); page.close() } }
    }
    fun navigate(page: Page, url: String) { validator.requireAllowed(url); page.navigate(url); page.waitForLoadState(LoadState.DOMCONTENTLOADED); validator.requireAllowed(page.url()); currentPage = page }
    fun saveFailure(scenario: String, action: String, error: Throwable) {
        val dir = artifacts.resolve(safe(scenario)); Files.createDirectories(dir); val page = currentPage
        if (page != null && !page.isClosed) { runCatching { page.screenshot(Page.ScreenshotOptions().setPath(dir.resolve("error.png")).setFullPage(true)) }; runCatching { Files.writeString(dir.resolve("page.html"), page.content()) } }
        Files.writeString(dir.resolve("error.txt"), "scenario=$scenario\naction=$action\nurl=${page?.url()}\n\n${error.stackTraceToString()}")
    }
    fun stopTrace() { val c = context ?: return; runCatching { c.tracing().stop(Tracing.StopOptions().setPath(artifacts.resolve("trace.zip"))) } }
    override fun close() { stopTrace(); runCatching { context?.close() }; runCatching { playwright?.close() }; context = null; playwright = null }
    private fun safe(value: String) = value.replace(Regex("[^A-Za-z0-9._-]"), "_")
}
