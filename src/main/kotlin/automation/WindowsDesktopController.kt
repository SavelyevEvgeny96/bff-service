package automation

import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.nio.file.Path

class WindowsDesktopController(
    private val robot: Robot = Robot(),
) : DesktopController {
    override fun switchWindow() = shortcut(KeyEvent.VK_ALT, KeyEvent.VK_TAB)

    override fun putOnClipboard(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }

    override fun openNotepad(file: Path) {
        ProcessBuilder("notepad.exe", file.toAbsolutePath().normalize().toString()).start()
    }

    override fun paste() {
        shortcut(KeyEvent.VK_CONTROL, KeyEvent.VK_A)
        shortcut(KeyEvent.VK_CONTROL, KeyEvent.VK_V)
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
