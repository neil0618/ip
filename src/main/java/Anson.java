import java.util.Scanner;

public class Anson {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String banner = "    _                              \n"
                + "   / \\    _ __   ___   ___   _ __  \n"
                + "  / _ \\  | '_ \\ / __| / _ \\ | '_ \\ \n"
                + " / ___ \\ | | | |\\__ \\| (_) || | | |\n"
                + "/_/   \\_\\|_| |_||___/ \\___/ |_| |_|\n";
        String line = "___________________________________";
        String[] tasks = new String[100];
        int tasksCount = 0;

        System.out.println(banner);
        System.out.println("Hey there, I am Anson!");
        System.out.println("How can I help?");
        System.out.println(line);

        while(true) {
            String reply = scan.nextLine();
            System.out.println(line);

            if(reply.equals("bye")) {
                System.out.println("Bye, see you soon!");
                System.out.println(line);
                break;
            }
            else if(reply.equals("list")) {
                if(tasksCount == 0) {
                    System.out.println("Your list is empty!");
                }
                else {
                    for (int i = 0; i < tasksCount; i++) {
                        System.out.println((i + 1) + ". " + tasks[i]);
                    }
                }

                System.out.println(line);
            }
            else {
                tasks[tasksCount] = reply;
                tasksCount++;
                System.out.println("added: " + reply);
                System.out.println(line);
            }
        }
    }
}
