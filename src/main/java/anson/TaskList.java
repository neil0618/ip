package anson;

import java.util.ArrayList;

/**
 * Contains the task list and the operations to add, remove and look up tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks Initial tasks, e.g., those loaded from storage.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index Zero-based position of the task.
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index Zero-based position of the task.
     * @return The task at that position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return True if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }
}