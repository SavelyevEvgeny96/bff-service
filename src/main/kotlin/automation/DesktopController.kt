package automation

import java.nio.file.Path

/** Windows desktop operations, separated from the scenario so it can be unit-tested. */
interface DesktopController {
    fun switchWindow()
    fun openNotepad(file: Path)
    fun clearDocument()
    fun typeText(text: String)
    fun backspace()
    fun save()
    fun closeWindow()
    fun waitFor(milliseconds: Long)
}
