package automation

import java.awt.GraphicsEnvironment

fun main(args: Array<String>) {
    check(System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
        "This application can only control Notepad on Windows"
    }
    check(!GraphicsEnvironment.isHeadless()) { "A graphical desktop session is required" }

    val config = AutomationConfig.fromArgs(args)
    NotepadAutomation(WindowsDesktopController()).run(config)
    println("Saved text from ${config.source} to ${config.output}")
}
