package automation.scenario

import automation.action.*
import automation.engine.ActionExecutor
import automation.engine.AutomationContext
import com.microsoft.playwright.options.LoadState

class OpenPageScenario(private val url: String, private val expectedText: String) : BrowserScenario {
    override val name = "OpenPageScenario"
    override fun actions(context: AutomationContext) = listOf(ActionConfig(ActionType.OPEN_PAGE, url=url), ActionConfig(ActionType.ASSERT_TEXT, selector=SelectorConfig(SelectorType.CSS, "body"), value=expectedText))
}
class NavigationScenario(private val url: String, private val link: SelectorConfig) : BrowserScenario {
    override val name = "NavigationScenario"
    override fun actions(context: AutomationContext) = listOf(ActionConfig(ActionType.OPEN_PAGE,url=url), ActionConfig(ActionType.CLICK,selector=link), ActionConfig(ActionType.BACK), ActionConfig(ActionType.ASSERT_URL,contains=url))
}
class FormScenario(private val url: String) : BrowserScenario {
    override val name = "FormScenario"
    override fun actions(context: AutomationContext): List<ActionConfig> = buildList {
        add(ActionConfig(ActionType.OPEN_PAGE,url=url)); add(ActionConfig(ActionType.FILL,selector=SelectorConfig(SelectorType.TEST_ID,"first-name"),dataKey="first-name"))
        add(ActionConfig(ActionType.FILL,selector=SelectorConfig(SelectorType.ROLE,"TEXTBOX","Last name"),dataKey="last-name")); add(ActionConfig(ActionType.CHECK,selector=SelectorConfig(SelectorType.LABEL,"Accept terms")))
        add(ActionConfig(ActionType.SELECT,selector=SelectorConfig(SelectorType.CSS,"#city"),dataKey="city")); add(ActionConfig(ActionType.WAIT_FOR,selector=SelectorConfig(SelectorType.TEXT,"Ready")))
        if (context.configuration.submitEnabled) add(ActionConfig(ActionType.CLICK,selector=SelectorConfig(SelectorType.ROLE,"BUTTON","Submit")))
    }
}
class MultiPageScenario(private val url: String, private val popupSelector: SelectorConfig? = null) : BrowserScenario {
    override val name = "MultiPageScenario"; override fun actions(context: AutomationContext) = emptyList<ActionConfig>()
    override fun run(context: AutomationContext, executor: ActionExecutor) {
        executor.execute(ActionConfig(ActionType.OPEN_PAGE,url=url),context); val original=context.currentPage!!
        val popup = if (popupSelector != null) original.waitForPopup { popupSelector.locator(original).click() } else { executor.execute(ActionConfig(ActionType.OPEN_TAB,url=url),context); context.currentPage!! }
        popup.waitForLoadState(LoadState.DOMCONTENTLOADED); context.validator.requireAllowed(popup.url()); context.browserContext!!.pages().forEach { if (it.url() != "about:blank") context.validator.requireAllowed(it.url()) }
        context.currentPage=popup; popup.close(); context.currentPage=original; original.bringToFront(); context.validator.requireAllowed(original.url())
    }
}
