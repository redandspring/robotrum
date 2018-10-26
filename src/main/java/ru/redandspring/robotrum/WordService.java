package ru.redandspring.robotrum;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alexander Tretyakov.
 */
class WordService {

    private static final String DIR = "c:/_FOLDER/_coins";
    private static final String ALL_WORDS_FILE = DIR + "/troom-short.txt";

    private static final String GEN_TROOM_DIR = DIR + "/gen-troom";
    private static final String GEN_TROOM_USED_DIR = DIR + "/gen-troom-used";
    private static final String GEN_TROOM_USED_BACKUP_DIR = DIR + "/gen-troom-used/backup";
    private static final String GEN_TROOM_FILE = GEN_TROOM_DIR + "/trooms-%d.txt";
    private static final String DIR_WALTROOM = "c:/_FOLDER/b__copy/_etroom3/troom_data/waltroom";

    private static final String TROOM_SUCCESS_DIR = DIR + "/troom-success";
    private static final String TROOM_SUCCESS_USED_DIR = DIR + "/troom-success-used";
    private static final String TROOM_SUCCESS_USED_BACKUP_DIR = DIR + "/troom-success-used/backup";
    private static final String TROOM_SUCCESS_FILE = TROOM_SUCCESS_DIR + "/trooms-%d.txt";

    // колво строк с трумами в одном файле
    private static final long LIMIT_TROOM = 40_000;
    // колво сброса успешных в файл
    private static final int LIMIT_TROOM_SUCCESS = 50;

    private static final Random RAND = new Random();

    private static final Set<String> bufferTroom = new HashSet<>();

    /**
     * Сгенерировать и Записать все трумы
     * @param count - колво которые нужно сгенерировать
     */
    void generateAndWriteTrooms(final long count) throws IOException {

        Map<Integer, String> allWords = getAllWords();

        final long chunk =  (count > LIMIT_TROOM) ? count / LIMIT_TROOM : 1;
        for (int j = 1; j <= chunk; j++) {
            Set<String> trooms = generateTrooms(allWords, (count > LIMIT_TROOM) ? LIMIT_TROOM : count);
            writeTrooms(trooms, GEN_TROOM_FILE);
        }
    }

    /**
     * Возвращает из одного файла и копирует его в папку used
     */
    Set<String> getTrooms(String mode) throws IOException {
        File folder;
        File destFolder;
        if ("Harvester".equals(mode)) {
            folder = new File(GEN_TROOM_DIR);
            destFolder = new File(GEN_TROOM_USED_DIR);
        }
        else if ("Airship".equals(mode)) {
            folder = new File(TROOM_SUCCESS_DIR);
            destFolder = new File(TROOM_SUCCESS_USED_DIR);
        }
        else {
            return null;
        }
        File[] folderEntries = folder.listFiles();
        if (folderEntries == null){
            return null;
        }
        for (File entry : folderEntries)
        {
            if (!entry.isDirectory())
            {
                final Set<String> content = getFile(entry);
                boolean isRename = entry.renameTo(new File(destFolder, entry.getName()));
                if (!isRename){
                    Log.info("Warning! fail renameTo failed");
                    return null;
                }
                return content;
            }
        }

        return null;
    }

    void backupOldFile(String mode){
        File folder;
        File destFolder;
        if ("Harvester".equals(mode)) {
            folder = new File(GEN_TROOM_USED_DIR);
            destFolder = new File(GEN_TROOM_USED_BACKUP_DIR);
        }
        else if ("Airship".equals(mode)) {
            folder = new File(TROOM_SUCCESS_USED_DIR);
            destFolder = new File(TROOM_SUCCESS_USED_BACKUP_DIR);
        }
        else {
            return;
        }
        File[] folderEntries = folder.listFiles();
        if (folderEntries == null){
            return;
        }
        for (File entry : folderEntries)
        {
            if (!entry.isDirectory())
            {
                boolean isRename = entry.renameTo(new File(destFolder, entry.getName()));
                if (!isRename){
                    Log.info("Warning! backupOldFile() fail ");
                    return;
                }
            }
        }
    }

    /**
     * Записывает успешную в память
     * @param troom фраза
     */
    void writeTroomSuccess(String troom) throws FileNotFoundException {

        bufferTroom.add(troom);

        if (bufferTroom.size() >= LIMIT_TROOM_SUCCESS){
            closeTroomSuccess();
        }
    }

    /**
     * Сброс успешных в файл
     */
    void closeTroomSuccess() throws FileNotFoundException {
        if (bufferTroom.size() > 0) {
            writeTrooms(bufferTroom, TROOM_SUCCESS_FILE);
            bufferTroom.clear();
        }
    }

    /**
     * Удалить маленькие файлы
     */
    void deleteFiles() {

        File folder = new File(DIR_WALTROOM);
        boolean isDelete = false;
        int count = 0;

        File[] folderEntries = folder.listFiles();
        if (folderEntries == null){
            return;
        }
        for (File entry : folderEntries)
        {
            if (!entry.isDirectory())
            {
                if ("default_wallet".equals(entry.getName())) continue;
                if (entry.length() < 4070){
                    isDelete = entry.delete();
                    if (isDelete) count++;
                }
            }
        }

        if (isDelete) {
            Log.info("deleteFiles() success count=" + count);
        }
    }

    /**
     * Возвращает содержимое файла построчно
     */
    private Set<String> getFile(File file) throws IOException {

        final Set<String> result = new HashSet<>();

        FileInputStream fileInput = new FileInputStream(file.getAbsoluteFile());
        BufferedReader br = new BufferedReader(new InputStreamReader(fileInput));
        String line;
        while ((line = br.readLine()) != null){
            result.add(line);
        }
        br.close();
        fileInput.close();
        return result;
    }

    /**
     * Возвращает все слова
     */
    private Map<Integer, String> getAllWords() throws IOException {

        final Map<Integer, String> words = new HashMap<>();
        FileInputStream fileWords = new FileInputStream(ALL_WORDS_FILE);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileWords));
        String word;
        Integer i = 1;
        while ((word = br.readLine()) != null){
            words.put(i++, word);
        }
        br.close();
        fileWords.close();
        return words;
    }

    /**
     * Сгенерировать все трумы
     */
    private Set<String> generateTrooms(final Map<Integer, String> words, final long count){

        Set<String> result = new LinkedHashSet<>();
        for (int j = 0; j < count; j++) {
            result.add(generateTroom(words));
        }
        return result;
    }

    /**
     * Записываем сгенерированные в файл
     */
    private void writeTrooms(Set<String> trooms, String file) throws FileNotFoundException {

        final long time = new Date().getTime();

        try (PrintWriter out = new PrintWriter(String.format(file, time))) {
            for (String item : trooms) {
                out.println(item);
            }
        }
    }

    /**
     * Сгенерировать одну
     */
    private String generateTroom(final Map<Integer, String> words) {

        final int size = words.size();
        Set<String> generated = new LinkedHashSet<>();
        while (generated.size() < 12) {
            Integer index = RAND.nextInt(size) + 1;
            generated.add(words.get(index));
        }

        return generated.stream().collect(Collectors.joining(" "));
    }
}
