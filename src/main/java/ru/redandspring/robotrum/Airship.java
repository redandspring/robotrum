package ru.redandspring.robotrum;

import java.awt.*;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Alexander Tretyakov.
 */
public class Airship {

    private final WordService wordService = new WordService();
    private final RobotService robotService = new RobotService();

    private static int countChapterLimit = 2; // число файлов без перезагрузки
    private static int countChapterMultiLimit = 4; // кол-во перезагрузок
    private static int countChapterMultiAllLimit = countChapterLimit * countChapterMultiLimit; // общее кол-во файлов
    // кол-во проверяемы
    private static long countTroomAll;
    private static long counter;
    private static long countChapter;

    private static long countChapterIteration;
    private static long countChapterAll;

    private static boolean isInterrupt = false;
    private static boolean isProgramOpen = false;

    private Airship() throws AWTException {
    }

    public static void main(String[] args) {
        try {
            Airship airship = new Airship();
            Log.point("ALL");
            for (countChapter = 1; countChapter <= countChapterMultiAllLimit; countChapter++) {
                if (isInterrupt) break;
                Log.point("CH-"+countChapter);

                    airship.start();
                    airship.exit();

                Log.point("CH-"+countChapter);
            }
            Log.point("ALL");
            airship.blockComputer();
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    private void blockComputer() throws IOException {
        wordService.deleteFiles();
        robotService.blockComputer();
    }

    private void exit() {
        Log.info("Finish, count troom: " + counter + " of " + countTroomAll);
        Log.info("Finish, ALL count troom: " + countChapterIteration + " of " + countChapterAll);
    }

    private void start() {

        wordService.backupOldFile("Airship");

        try {

            // 1. прочитать фразы
            final Set<String> trooms = wordService.getTrooms("Airship");

            if (trooms == null) {
                Log.info("Warning! trooms is null");
                return;
            }

            if (!isProgramOpen){
                Log.info("openProgram() " + countChapter);
                robotService.click(Coord.ETROOM_RUN);
                robotService.robot.delay(9500);
                isProgramOpen = true;
            }

            robotService.robot.delay(4000);

            countTroomAll = trooms.size();
            countChapterAll += countTroomAll;
            counter = 0;
            int i = 0;

            for (String troom : trooms) {
                i++;
                Log.info("["+countChapter+"/"+countChapterMultiAllLimit+"] " + i + " of " + countTroomAll + ": " + troom);

                if (robotService.checkColorInterrupt(Coord.AIRSHIP_CHECK_COLOR)) {
                    Log.info("! CHECK_COLOR_INTERRUPT !");
                    isInterrupt = true;
                    return;
                }

                // 2. активировать окно
                robotService.click(Coord.ETROOM_FIELD);
                robotService.robot.delay(100);

                // 3. кликнуть Файл - новое
                robotService.clickCtrlNew();
                while (true) {
                    robotService.robot.delay(20);
                    if (!robotService.checkColorGenerateWaltroom(Coord.GENERATE_WALTROOM_WINDOW)) {
                        break;
                    }
                }

                // 4. кликнуть Next дважды
                robotService.click(Coord.NEXT_BUTTON);
                robotService.robot.delay(240);
                robotService.click(Coord.NEXT_BUTTON);
                robotService.robot.delay(240);

                // 5. выбрать have a troom и нажать Next
                robotService.click(Coord.HAVE_TROOM_RADIO_BUTTON);
                robotService.robot.delay(100);
                robotService.clickShort(Coord.NEXT_BUTTON);
                while (true) {
                    robotService.robot.delay(20);
                    if (!robotService.checkColorInterrupt(Coord.ENTER_TROOM_FIELD_COLOR)) {
                        break;
                    }
                }


                // 6. вставить и проверить фразу
                robotService.click(Coord.ENTER_TROOM_FIELD);
                robotService.robot.delay(20);
                robotService.insertTroomOnField(troom);

                if (robotService.checkButtonNextColor()) {

                    boolean isFa2 = robotService.check2Fa();

                    robotService.click(Coord.NEXT_BUTTON);
                    robotService.robot.delay(240);

                    if (isFa2) {
                        robotService.click(Coord.FA2_DISABLE_RADIO_BUTTON);
                        robotService.robot.delay(120);
                        robotService.click(Coord.NEXT_BUTTON);
                        robotService.robot.delay(200);
                        Log.info("is 2fa");
                    }

                    robotService.clickShort(Coord.NEXT_BUTTON);
                    Log.point("GEN_WALTROOM");
                    while (true) {
                        robotService.robot.delay(40);
                        if (robotService.checkColorGenerateWaltroom(Coord.GENERATE_WALTROOM_WINDOW)) {
                            break;
                        }
                    }
                    Log.point("GEN_WALTROOM");
                    robotService.robot.delay(800);
                    counter++;
                    countChapterIteration++;
                    // 8. закрыть прошлое окно
                    robotService.closePrevWindow();
                    robotService.robot.delay(800);

                } else {
                    Log.info("Warning! No check troom!");
                    robotService.closeWindow();
                    robotService.robot.delay(120);
                }
            }

            if ((countChapter % countChapterLimit) == 0){
                Log.info("closeProgram() " + countChapter);
                robotService.closeWindow();
                wordService.deleteFiles();
                isProgramOpen = false;
                robotService.robot.delay(8600);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
