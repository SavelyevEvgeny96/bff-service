package automation.engine

import automation.browser.BrowserManager
import automation.config.AutomationConfig
import automation.model.*
import automation.process.WindowsProcessManager
import automation.report.ReportGenerator
import automation.scenario.*
import automation.validation.AllowedUrlValidator
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Collections
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

class AutomationEngine(private val config: AutomationConfig, private val requested: Set<String> = emptySet()) {
    private val log=LoggerFactory.getLogger(javaClass); private val started=Instant.now(); private val reporter=ReportGenerator(); private val reportWritten=AtomicBoolean()
    private val runDir=Path.of(config.artifacts.directory).resolve("run-"+DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss").withZone(java.time.ZoneOffset.UTC).format(started))
    private val stop=StopController(Path.of("build/automation.stop")); private val processes=WindowsProcessManager(config.processes.allowed)
    private val validator=AllowedUrlValidator(config.allowedHosts); private var browser: BrowserManager?=null
    private lateinit var context: AutomationContext
    fun run(): Int {
        stop.initialize(); validateConfiguration(); var scenarios=selectedScenarios()
        if(config.randomizeScenarioOrder) Collections.shuffle(scenarios,Random(config.randomSeed))
        if(config.dryRun){ println("DRY RUN — nothing will be started\nSeed: ${config.randomSeed}"); scenarios.forEach{println("- ${it.name}: ${it.actions.size} actions")}; return 0 }
        Files.createDirectories(runDir); browser=BrowserManager(config.browser,validator,runDir); browser!!.start()
        context=AutomationContext(config,started,config.randomSeed,runDir,validator,processes,browser,stop)
        val hook=Thread { stop.requestStop(); cleanup(); writeReport() }; Runtime.getRuntime().addShutdownHook(hook)
        try { for(scenario in scenarios){ if(stop.stopRequested.get()) break; execute(scenario) } } finally { cleanup(); writeReport(); runCatching{Runtime.getRuntime().removeShutdownHook(hook)} }
        return if(context.results.any{it.status==ScenarioStatus.FAILED}) 1 else 0
    }
    private fun execute(s: SelectedScenario){
        val beforeActions=context.actionCounter.get(); val beforeNav=context.navigationCounter.get(); val at=Instant.now(); log.info("scenario={} result=STARTED seed={}",s.name,config.randomSeed)
        val status:ScenarioStatus; var error:String?=null
        try{ ConfiguredScenario(s.name,s.actions).run(context,ActionExecutor()); status=ScenarioStatus.SUCCESS }
        catch(e:Exception){ error=e.stackTraceToString(); status=if(stop.stopRequested.get()||e.message=="Emergency stop requested") ScenarioStatus.CANCELLED else ScenarioStatus.FAILED; browser?.saveFailure(s.name,"action-${context.actionCounter.get()}",e); log.error("scenario={} result={} error={}",s.name,status,e.message) }
        val end=Instant.now(); context.results += ScenarioResult(s.name,status,at,end,Duration.between(at,end),context.actionCounter.get()-beforeActions,context.navigationCounter.get()-beforeNav,error)
    }
    private fun selectedScenarios():MutableList<SelectedScenario>{
        if(requested.isNotEmpty()){ val unknown=requested-config.scenarios.keys; require(unknown.isEmpty()){ "Unknown scenarios: $unknown" } }
        return config.scenarios.filter{(name,s)->s.enabled&&(requested.isEmpty()||name in requested)}.map{SelectedScenario(it.key,it.value.actions)}.toMutableList()
    }
    private fun validateConfiguration(){
        require(config.allowedHosts.isNotEmpty()){ "allowed-hosts must not be empty" }; config.allowedHosts.forEach{require(it.domain.isNotBlank()){"Allowed domain is blank"}}
        config.processes.allowed.forEach{(id,p)->require(id.isNotBlank()&&p.executable.isNotBlank()){ "Invalid process allowlist entry" }}
        config.scenarios.forEach{(name,s)->require(name.isNotBlank()){ "Scenario name is blank" }; s.actions.forEach{a->a.url?.let(validator::requireAllowed); a.process?.let{require(it in config.processes.allowed){"Scenario '$name' references non-allowlisted process '$it'"}} }}
        require(!config.limits.maxRunDuration.isNegative&&!config.limits.maxRunDuration.isZero); require(config.limits.maxActionCount>0&&config.limits.maxNavigationCount>0)
    }
    private fun cleanup(){ browser?.close(); if(config.processes.cleanupOnExit) processes.cleanup() }
    private fun writeReport(){ if(::context.isInitialized&&reportWritten.compareAndSet(false,true)) reporter.write(config.randomSeed,context.results,started,runDir) }
    private data class SelectedScenario(val name:String,val actions:List<automation.action.ActionConfig>)
}
