package com.exam.fileanalyzer.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class FileUtils {

    public static void extractFilesFromZip(ZipFile zip, Path tempDir) throws IOException {
        zip.stream().forEach(entry -> {
            Path outputFile = tempDir.resolve(entry.getName());
            try (InputStream inputStream = zip.getInputStream(entry);
                 FileOutputStream outputStream = new FileOutputStream(outputFile.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
