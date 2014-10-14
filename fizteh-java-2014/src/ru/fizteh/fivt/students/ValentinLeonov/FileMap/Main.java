package ru.fizteh.fivt.students.ValentinLeonov.FileMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Map<String, Command> commandMap = new HashMap<String, Command>();

        File dbName = new File(System.getProperty("db.file"));
        if (!dbName.exists()) {
            try {
                dbName.createNewFile();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }

        State state = new State(dbName);
        state.changeCurrentTable(dbName);

        try {
            FileMap.readTable(dbName, state);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }


        commandMap.put("put", new CommandPut());
        commandMap.put("get", new CommandGet());
        commandMap.put("remove", new CommandRemove());
        commandMap.put("list", new CommandList());
        commandMap.put("exit", new CommandExit());

        if (args.length == 0) {
            interactiveMode(commandMap, state, dbName);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : args) {
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }
            String commands = stringBuilder.toString();
            batchMode(commands, commandMap, state, dbName);
        }

        try {
            FileMap.writeTable(dbName, state);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    private static void interactiveMode(Map<String, Command> commandMap, State state, File dbName) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (scanner.hasNextLine()) {
            String string = scanner.nextLine();
            try {
                parseCommands(string, commandMap, state);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            } catch (ExitException ex) {
                try {
                    FileMap.writeTable(dbName, state);
                } catch (IOException exc) {
                    System.err.println(exc.getMessage());
                    System.exit(1);
                }
                System.exit(0);
            }
            System.out.print("$ ");
        }
    }

    private static void batchMode(String commands, Map<String, Command> commandMap, State state, File dbName) {
        try {
            parseCommands(commands, commandMap, state);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } catch (ExitException ex) {
            try {
                FileMap.writeTable(dbName, state);
            } catch (IOException exc) {
                System.err.println(exc.getMessage());
                System.exit(1);
            }
            System.exit(0);
        }
    }

    private static void parseCommands(String commands, Map<String, Command> commandMap, State state) throws IOException, ExitException {
        String[] listOfCommand = commands.trim().split("\\s*;\\s*");
        for (String string : listOfCommand) {
            String[] commandArguments = string.split("\\s+");
            Command command = commandMap.get(commandArguments[0]);
            if (command == null) {
                throw new IOException("Wrong command name");
            }
            if (commandArguments.length != command.getArgumentsCount() + 1) {
                throw new IOException("Wrong argument count");
            } else {
                command.execute(state, commandArguments);
            }
        }
    }
}
