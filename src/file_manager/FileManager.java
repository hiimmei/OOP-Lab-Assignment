package file_manager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class FileManager {
    public static List<String> readAllFromFile(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading files" + path);
        }
        return lines;
    }

    public static void writeLinesToFile (String path, List<String> lines, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, append))) {
            for (String line : lines) {
                writer.write("{");
                writer.write(line);
                writer.write("}");
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + path);
        }
    }

    public static void writeObject (String path, Object obj, boolean append) {
        writeLinesToFile(path, Collections.singletonList(obj.toString()), append);
    }

    public static <T> List<T> readObjects (String path, Function<String, T> mapper) {
        List<T> result = new ArrayList<>();
        List<String> lines = readAllFromFile(path);
        for (String line : lines) {
            try {
                T obj = mapper.apply(line);
                if (obj != null) {
                    result.add(obj);
                }
            } catch (Exception e) {
                System.err.println("Error mapping line: " + line);
            }
        }
        return result;
    }

    public static void clearFile (String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // Ghi file rỗng để clear
        } catch (IOException e) {
            System.err.println("Error clearing file: " + path);
        }
    }
}