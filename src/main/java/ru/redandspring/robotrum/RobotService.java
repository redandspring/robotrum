package ru.redandspring.robotrum;

import ru.redandspring.robotrum.Log;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Created by Alexander Tretyakov.
 */
class RobotService {

    Robot robot = new Robot();

    private final static Color GRAY_COLOR = new Color(240,240,240);

    RobotService() throws AWTException {
    }

    void blockComputer() throws IOException {
        Log.info("blockComputer()");
        Runtime.getRuntime().exec("rundll32 user32.dll,LockWorkStation");
    }

    void click(Coord coord){
        robot.mouseMove(coord.x, coord.y);
        robot.delay(10);
        leftClick();
    }
    void clickShort(Coord coord) {
        robot.mouseMove(coord.x, coord.y);
        leftClick();
    }

    void insertTroomOnField(String troom) {
        setClipboard(troom);
        pasteClipboard();
    }

    boolean checkButtonNextColor() {

        robot.delay(100);
        Color buttonColor = robot.getPixelColor(Coord.NEXT_BUTTON.x, Coord.NEXT_BUTTON.y);
        return (Color.BLACK.equals(buttonColor));
    }

    boolean checkColorInterrupt(Coord coord) {
        Color touchColor = robot.getPixelColor(coord.x, coord.y);
        return (!Color.WHITE.equals(touchColor));
    }

    boolean checkColorGenerateWaltroom(Coord coord) {
        Color touchColor = robot.getPixelColor(coord.x, coord.y);
        return (!GRAY_COLOR.equals(touchColor));
    }

    boolean check2Fa() {
        Coord coord = Coord.FA2_CHECK_COLOR;
        Color touchColor = robot.getPixelColor(coord.x, coord.y);
        return (Color.BLACK.equals(touchColor));
    }

    void clearField(int delay) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_A);

        robot.keyPress(KeyEvent.VK_DELETE);
        robot.keyRelease(KeyEvent.VK_DELETE);
        robot.delay(delay);
    }

    void clickCtrlNew() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_N);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_N);
    }

    void closePrevWindow() {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.delay(200);
        closeWindow();
    }

    void closeWindow() {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    private void pasteClipboard() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
    }

    private void setClipboard(final String str) {
        final StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    private void leftClick(){
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }


}
