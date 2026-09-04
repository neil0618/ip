import java.util.Scanner;

/**
 * Entry point for the Anson task-tracking chatbot.
 */
public class Anson {
    /**
     * Runs the Anson chatbot, reading commands from standard input until
     * the user enters "bye".
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String banner = "    _                              \n"
                + "   / \\    _ __   ___   ___   _ __  \n"
                + "  / _ \\  | '_ \\ / __| / _ \\ | '_ \\ \n"
                + " / ___ \\ | | | |\\__ \\| (_) || | | |\n"
                + "/_/   \\_\\|_| |_||___/ \\___/ |_| |_|\n";
        String line = "___________________________________";
        Task[] tasks = new Task[100];
        int tasksCount = 0;

        System.out.println(banner);
        System.out.println("Hey there, I am Anson!");
        System.out.println("How can I help?");
        System.out.println(line);

        while (true) {
            String reply = scan.nextLine();
            System.out.println(line);

            if (reply.equals("bye")) {
                System.out.println("Bye, see you soon!");
                System.out.println(line);
                break;
            } else if (reply.startsWith("mark ")) {
                int taskNum = Integer.parseInt(reply.substring(5));

                if (taskNum <= 0 || taskNum > tasksCount) {
                    System.out.println("That task number does not exist!");
                    System.out.println(line);
                } else {
                    tasks[taskNum - 1].markAsDone();
                    System.out.println("I have marked this task as done:");
                    System.out.println(tasks[taskNum - 1]);
                    System.out.println(line);
                }
            } else if (reply.startsWith("unmark ")) {
                int taskNum = Integer.parseInt(reply.substring(7));

                if (taskNum <= 0 || taskNum > tasksCount) {
                    System.out.println("That task number does not exist!");
                    System.out.println(line);
                } else {
                    tasks[taskNum - 1].markAsNotDone();
                    System.out.println("I have marked this task as not done yet:");
                    System.out.println(tasks[taskNum - 1]);
                    System.out.println(line);
                }
            } else if (reply.equals("list")) {
                if (tasksCount == 0) {
                    System.out.println("Your task list is empty!");
                } else {
                    System.out.println("Here are the tasks in your list:");

                    for (int i = 0; i < tasksCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                }

                System.out.println(line);
            } else if (reply.startsWith("todo ")) {
                String description = reply.substring(5);

                tasks[tasksCount] = new Todo(description);

                tasksCount++;
                System.out.println("Got it! I've added this task:");
                System.out.println("  " + tasks[tasksCount - 1]);
                System.out.println("Number of tasks in the list: " + tasksCount);
                System.out.println(line);
            } else if (reply.startsWith("deadline ")) {
                String[] parts = reply.substring(9).split(" /by ", 2);

                tasks[tasksCount] = new Deadline(parts[0], parts[1]);

                tasksCount++;
                System.out.println("Got it! I've added this task:");
                System.out.println("  " + tasks[tasksCount - 1]);
                System.out.println("Number of tasks in the list: " + tasksCount);
                System.out.println(line);
            } else if (reply.startsWith("event ")) {
                String[] fromSplit = reply.substring(6).split(" /from ", 2);
                String[] toSplit = fromSplit[1].split(" /to ", 2);

                tasks[tasksCount] = new Event(fromSplit[0], toSplit[0], toSplit[1]);

                tasksCount++;
                System.out.println("Got it! I've added this task:");
                System.out.println("  " + tasks[tasksCount - 1]);
                System.out.println("Number of tasks in the list: " + tasksCount);
                System.out.println(line);
            } else {
                System.out.println("Please specify the task as todo, deadline, or event!");
                System.out.println(line);
            }
        }
    }
}
