package ru.redandspring.robotrum;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Alexander Tretyakov.
 */
public class Harvester {

    private final WordService wordService = new WordService();
    private final RobotService robotService = new RobotService();

    // кол-во генерируемых
    private static final long COUNT_TROOM = 1_000_000;

    private Harvester() throws AWTException {
    }

    public static void main(String[] args) throws Exception {
        Log.point("ALL");
        Harvester harvester = new Harvester();
        harvester.start();
        harvester.exit();
        harvester.blockComputer();
        Log.point("ALL");
    }

    private void exit() {
        // запишем успешные фразы, если они остались в буфере
        try {
            wordService.closeTroomSuccess();
            Log.info("exit()");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void blockComputer() throws IOException {
        robotService.blockComputer();
    }

    private void start() throws LineUnavailableException {

        wordService.backupOldFile("Harvester");

        long num = 0;
        long all = 0;
        long success = 0;

        try {

            // 0. сгенерировать
            wordService.generateAndWriteTrooms(COUNT_TROOM);

            Log.info("openProgram() ");
            robotService.click(Coord.ETROOM_RUN);
            robotService.robot.delay(8000);

            // A1. активировать окно
            robotService.click(Coord.ETROOM_FIELD);
            robotService.robot.delay(200);

            // A2. кликнуть Файл - новое
            robotService.clickCtrlNew();
            while (true) {
                robotService.robot.delay(20);
                if (!robotService.checkColorGenerateWaltroom(Coord.GENERATE_WALTROOM_WINDOW)) {
                    break;
                }
            }

            // A3. кликнуть дважды
            robotService.click(Coord.NEXT_BUTTON);
            robotService.robot.delay(200);
            robotService.click(Coord.NEXT_BUTTON);
            robotService.robot.delay(200);

            // A4. выбрать и нажать
            robotService.click(Coord.HAVE_TROOM_RADIO_BUTTON);
            robotService.robot.delay(200);
            robotService.click(Coord.NEXT_BUTTON);
            while (true) {
                robotService.robot.delay(20);
                if (!robotService.checkColorInterrupt(Coord.ENTER_TROOM_FIELD_COLOR)) {
                    break;
                }
            }

            label:
            while (true) {
                if (num > 0) {
                    SoundUtils.beep();
                    Log.info("Start new troom file");
                    for (int ii = 0; ii < 30; ii++) {
                        robotService.robot.delay(400);
                        System.out.print(".");
                        if (robotService.checkColorInterrupt(Coord.HARVESTER_CHECK_COLOR)) {
                            System.out.println("");
                            Log.info("START NEW FILE ! CHECK_COLOR_INTERRUPT !");
                            break label;
                        }
                    }
                    System.out.println("");
                }
                // 1. прочитать фразы
                final Set<String> trooms = wordService.getTrooms("Harvester");
                robotService.robot.delay(100);

                if (trooms == null) {
                    Log.info("Warning! trooms is null");
                    break;
                }

                all += trooms.size();

                for (String troom : trooms) {

                    num++;
                    // проверка на прерывание
                    if (robotService.checkColorInterrupt(Coord.HARVESTER_CHECK_COLOR)) {
                        Log.info("! CHECK_COLOR_INTERRUPT !");
                        break label;
                    }
                    // 2. поставить курсор на поле move+click
                    robotService.clickShort(Coord.ENTER_TROOM_FIELD);
                    // 3. вставить
                    robotService.insertTroomOnField(troom);
                    // 4. проверить кнопку
                    if (robotService.checkButtonNextColor()) {
                        // 4a. записать успешную
                        Log.info("" + num + " of " + all + ". " + troom);
                        wordService.writeTroomSuccess(troom);
                        success++;
                        robotService.clearField(1400);
                    }
                    else {
                        // 5. очистить поле
                        robotService.clearField(20);
                    }
                }

            }

            Log.info("Finish, count troom: " + num + " success: " + success);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
