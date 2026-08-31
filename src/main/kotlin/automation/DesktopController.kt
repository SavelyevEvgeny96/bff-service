package automation

/** Operations that affect the Windows desktop, separated to keep the scenario testable. */
interface DesktopController {
    fun switchWindow()
    fun putOnClipboard(text: String)
    fun openNotepad(file: java.nio.file.Path)
    fun paste()
    fun save()
    fun closeWindow()
    fun waitFor(milliseconds: Long)
}
