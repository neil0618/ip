package anson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving tasks to, and loading tasks from, the hard disk.
 */
public class Storage {
    // Paths.get with separate segments keeps the path OS-independent.
    private static final Path DATA_FILE = Paths.get("data", "anson.txt");
    private static final String SPLIT_REGEX = " \\| ";

    /**
     * Loads tasks from the data file into the given array. A missing file
     * is treated as an empty task list. Lines that are not in the expected
     * format are skipped and a warning is printed.
     *
     * @param tasks Array to fill with the loaded tasks.
     * @return Number of tasks loaded.
     */
    public static int load(Task[] tasks) {
        if (!Files.exists(DATA_FILE)) {
            return 0;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(DATA_FILE);
        } catch (IOException e) {
            System.out.println("I couldn't read your saved tasks, so I'm starting with an empty list.");
            return 0;
        }

        int count = 0;
        int skipped = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            if (count >= tasks.length) {
                skipped++;
                continue;
            }
            try {
                tasks[count] = parseTask(line);
                count++;
            } catch (AnsonException e) {
                skipped++;
            }
        }

        if (skipped > 0) {
            System.out.println("Warning: skipped " + skipped + " unreadable line(s) in " + DATA_FILE
                    + ". They will be dropped the next time your tasks are saved.");
        }
        return count;
    }

    /**
     * Writes all tasks to the data file, creating the data folder and file
     * if they do not exist yet.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @throws AnsonException If the file cannot be written.
     */
    public static void save(Task[] tasks, int tasksCount) throws AnsonException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < tasksCount; i++) {
            lines.add(tasks[i].toFileString());
        }

        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(DATA_FILE, lines);
        } catch (IOException e) {
            throw new AnsonException("I couldn't save your tasks: " + e.getMessage());
        }
    }

    /**
     * Parses one line of the data file into a task.
     *
     * @param line A line such as "D | 0 | return book | June 6th".
     * @return The corresponding task.
     * @throws AnsonException If the line is not in the expected format.
     */
    private static Task parseTask(String line) throws AnsonException {
        String[] parts = line.split(SPLIT_REGEX, -1);
        if (parts.length < 3) {
            throw new AnsonException("Malformed line: " + line);
        }

        String type = parts[0];
        String doneFlag = parts[1];
        String description = parts[2];

        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new AnsonException("Malformed line: " + line);
        }
        if (description.trim().isEmpty()) {
            throw new AnsonException("Malformed line: " + line);
        }

        Task task;
        switch (type) {
            case "T":
                if (parts.length != 3) {
                    throw new AnsonException("Malformed line: " + line);
                }
                task = new Todo(description);
                break;
            case "D":
                if (parts.length != 4 || parts[3].trim().isEmpty()) {
                    throw new AnsonException("Malformed line: " + line);
                }
                task = new Deadline(description, parts[3]);
                break;
            case "E":
                if (parts.length != 5 || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
                    throw new AnsonException("Malformed line: " + line);
                }
                task = new Event(description, parts[3], parts[4]);
                break;
            default:
                throw new AnsonException("Malformed line: " + line);
        }

        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}