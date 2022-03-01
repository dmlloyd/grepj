package com.flurg.grepj;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public final class GrepJ {
    public static void main(String[] argsArray) {
        ArrayDeque<String> args = new ArrayDeque<>();
        Collections.addAll(args, argsArray);
        Pattern pattern = Pattern.compile(args.removeFirst());
        List<Path> paths;
        if (args.isEmpty()) {
            paths = List.of(Path.of("/dev/stdin"));
        } else {
            paths = args.stream().map(Path::of).toList();
        }
        boolean prepend = paths.size() > 1;
        String line;
        boolean found = false;
        for (Path path : paths) {
            try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                while ((line = br.readLine()) != null) {
                    if (line.endsWith("\r\n") || line.endsWith("\n\r")) {
                        line = line.substring(0, line.length() - 2);
                    } else if (line.endsWith("\r") || line.endsWith("\n")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    if (pattern.matcher(line).find()) {
                        found = true;
                        if (prepend) {
                            System.out.printf("%s: ", path);
                        }
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                System.err.printf("Failed to read %s: %s", path, e);
            }
        }
        System.exit(found ? 0 : 1);
    }
}
