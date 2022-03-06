package analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Path root = Path.of(args[0]);

        File dir = root.toFile();

        File pattern = new File(args[1]);

        String patterns = null;

        try {
            FileInputStream fis = new FileInputStream(pattern);
            patterns = new String(fis.readAllBytes());
            fis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String[] patternArray = patterns.split("\n");

        String[][] newP = new String[patternArray.length][2];

        for (int i = 0; i < patternArray.length; i++) {
            patternArray[i] = patternArray[i].replaceAll("\\d;", "");
            for (int j = 0; j < 2; j++) {
                newP[i][j] = patternArray[i]
                        .split(";")[j]
                        .replace("\"", "");
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(8);

        for (File file : dir.listFiles()) {

            String inputFile = file.toString();

            executor.submit(() -> {
                bytes(inputFile, newP);
            });
        }

        TimeUnit.SECONDS.sleep(2);

        executor.shutdown();

    }

    public static void bytes(String inputFile, String[][] patternArray) {
        String text = null;
        try (
                InputStream inputStream = new FileInputStream(inputFile);
        ) {
            byte[] headerBytes = inputStream.readAllBytes();

            text = new String(headerBytes);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        for (int i = 0; i < patternArray.length; i++) {

            if (text.contains(patternArray[i][0])) {
                System.out.println(inputFile.substring(11) + ": " + patternArray[i][1]);
            } else {
                System.out.println(inputFile.substring(11) + ": Unknown file type");
            }

        }

    }
}

