package automation

import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.nio.file.Path

class WindowsDesktopController(private val robot: Robot = Robot()) : DesktopController {
    override fun switchWindow() = shortcut(KeyEvent.VK_ALT, KeyEvent.VK_TAB)

    override fun openNotepad(file: Path) {
        ProcessBuilder("notepad.exe", file.toAbsolutePath().normalize().toString()).start()
    }

    override fun clearDocument() {
        shortcut(KeyEvent.VK_CONTROL, KeyEvent.VK_A)
        robot.keyPress(KeyEvent.VK_BACK_SPACE)
        robot.keyRelease(KeyEvent.VK_BACK_SPACE)
    }

    /**
     * A one-character clipboard paste is used because Robot cannot enter arbitrary Unicode
     * (including Russian letters) through KeyEvent. Visually, characters still appear one by one.
     */
    override fun typeText(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
        shortcut(KeyEvent.VK_CONTROL, KeyEvent.VK_V)
    }

    override fun backspace() {
        robot.keyPress(KeyEvent.VK_BACK_SPACE)
        robot.keyRelease(KeyEvent.VK_BACK_SPACE)
    }

    override fun save() = shortcut(KeyEvent.VK_CONTROL, KeyEvent.VK_S)
    override fun closeWindow() = shortcut(KeyEvent.VK_ALT, KeyEvent.VK_F4)
    override fun waitFor(milliseconds: Long) = robot.delay(milliseconds.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())

    private fun shortcut(modifier: Int, key: Int) {
        robot.keyPress(modifier)
        robot.keyPress(key)
        robot.keyRelease(key)
        robot.keyRelease(modifier)
    }
}
