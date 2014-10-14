package ru.fizteh.fivt.students.ValentinLeonov.Shell;

public class Main {
    public static void main(String[] args) {
        Shell shell = new Shell();
        if (args.length > 0) {
            StringBuilder commands = new StringBuilder();
            for (String arg : args) {
                commands.append(arg).append(' ');
            }

            try {
                shell.executeCommands(commands.toString());
            } catch (SException exception) {
                System.err.println(exception);
                System.exit(1);
            }

        } else {
            shell.interactiveMode();
        }
    }
}
