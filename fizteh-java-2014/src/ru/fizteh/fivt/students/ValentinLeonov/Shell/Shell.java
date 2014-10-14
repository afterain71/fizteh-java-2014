package ru.fizteh.fivt.students.ValentinLeonov.Shell;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class Shell {
    private File directory;

    Shell() {
        directory = new File(System.getProperty("user.dir"));
    }

    public void executeCommands(String commands) throws SException {
        Scanner scanner = new Scanner(commands);
        try {
            while (scanner.hasNextLine()) {
                String[] singleCommand = scanner.nextLine().split(";");
                for (String command : singleCommand) {
                    executeSingleCommand(command);
                }
            }
        } finally {
            scanner.close();
        }
    }

    private void executeSingleCommand(String command) throws SException {
        if (command.trim().isEmpty()) {
            return;
        }

        String[] args = command.trim().split("[\t ]+");
        String commandName = args[0];

        if (commandName.equals("cd")) {
            cd(args);

        } else if (commandName.equals("mkdir")) {
            mkdir(args);

        } else if (commandName.equals("pwd")) {
            pwd(args);

        } else if (commandName.equals("rm")) {
            remove(args);

        } else if (commandName.equals("cp")) {
            cp(args);

        } else if (commandName.equals("mv")) {
            mv(args);

        } else if (commandName.equals("dir")) {
            dir(args);

        } else if (commandName.equals("ls")) {
            ls(args);

        } else if (commandName.equals("exit")) {
            System.exit(0);

        } else {
            throw new SException("shell", "no such command");
        }
    }

    private static void ls(String[] args) {
        String command = "ls";
        if (args.length != 1) {
            System.err.println(command + ": too much arguments");
            return;
        }
        try {
            File currentDirectory = new File(System.getProperty("user.dir"));
            for (String i : currentDirectory.list()) {
                System.out.println(i);
            }
        } catch (SecurityException e) {
            System.err.println(command + ": cannot get the list of files: access denied");
            return;
        }
    }

    public void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        String beginning;

        try {
            beginning = directory.getCanonicalPath() + "$ ";
        } catch (Exception exception) {
            beginning = "$ ";
        }
        System.out.print(beginning);
        System.out.flush();
        while (scan.hasNextLine()) {
            String commands = scan.nextLine().trim();
            if (commands.length() == 0) {
                System.out.print(beginning);
                System.out.flush();
                continue;
            }
            try {
                executeCommands(commands);
            } catch (SException exception) {
                System.out.println(exception);
            }
            try {
                if (!Files.isDirectory(directory.toPath())) {
                    System.err.println("no such directory");
                    directory = new File(System.getProperty("user.dir"));
                }
                beginning = directory.getCanonicalPath() + "$ ";
            } catch (Exception exception) {
                beginning = "$ ";
            }
            System.out.print(beginning);
            System.out.flush();
        }
    }

    private void mv(String[] args) {
        if (3 != args.length) {
            System.err.println("incorrect arguments");
            return;
        }

        File fl = new File(System.getProperty("user.dir") + "/" + args[1]);
        if (!fl.exists()) {
            System.err.println("There is no such file or directory: " + args[1]);
            return;
        }

        File nfl = new File(System.getProperty("user.dir") + "/" + args[2]);
        if (nfl.exists() && nfl.isDirectory()) {
            nfl = new File(nfl.getAbsolutePath() + "/" + fl.getName());
        }
        if (!fl.renameTo(nfl)) {
            System.err.println("Can't move this file: " + args[1]);
            return;
        }
        return;
    }

    private void cd(String[] args) {
        if (args.length != 2) {
            System.err.println("incorrect arguments");
            return;
        }

        if (args[1].equals(".")) {
            return;
        }

        if (args[1].equals("..")) {
            try {
                File temp = new File(System.getProperty("user.dir"));
                File fl = new File(temp.getParent());
                directory = fl;
                System.setProperty("user.dir", fl.getAbsolutePath());
            } catch (NullPointerException e) {
                System.err.println(e.getMessage());
                return;
            }
        } else {
            File fl = new File(System.getProperty("user.dir") + "/" + args[1]);
            directory = fl;
            if (!fl.isDirectory()) {
                System.err.println("there is no such file or directory: " + args[1]);
                return;
            }
            System.setProperty("user.dir", fl.getAbsolutePath());
        }
        return;
    }

    private void pwd(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 0);
            System.out.println(directory.getCanonicalPath());
        } catch (SException se) {
            throw se;
        } catch (Exception exception) {
            throw new SException(args[0], exception.getMessage());
        }
    }

    private void dir(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 0);
            File[] filesToShow = directory.listFiles();
            if (filesToShow != null) {
                for (File file : filesToShow) {
                    System.out.println(file.getCanonicalFile().getName());
                }
            }
        } catch (SException se) {
            throw se;
        } catch (Exception exception) {
            throw new SException("dir", exception.getMessage());
        }
    }

    private void mkdir(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 1);
            File tmpFile = new File(pathAppend(args[1]));
            if (tmpFile.exists()) {
                throw new SException(args[0], "\'" + args[1] + "\': File or directory exist in time");
            }
            if (!tmpFile.mkdir()) {
                throw new SException(args[0], "\'" + args[1] + "\': Directory wasn't created");
            }
        } catch (SException se) {
            throw se;
        } catch (Exception exception) {
            throw new SException(args[0], exception.getMessage());
        }
    }

    private void remove(String[] args) throws SException {
        if ((args.length > 3) | (args.length < 2)) {
            System.err.println("unknown arguments");
            return;
        }
        if (args.length == 3 && !args[1].equals("-r")) {
            System.err.println("incorrect arguments");
            return;
        }
        File fl = new File(System.getProperty("user.dir") + "/" + args[args.length - 1]);
        if (!fl.exists()) {
            System.err.println("There is no such file or directory: " +
                    System.getProperty("user.dir") + "/" + args[args.length - 1]);
            return;
        }

        if (fl.isDirectory() && (args.length == 2 | (args.length == 3 && !args[1].equals("-r")))) {
            System.err.println("This is a directory: " + args[args.length - 1]);
            return;
        }
        if (fl.isDirectory()) {
            if (fl.listFiles() != null) {
                try {
                    for (File file : fl.listFiles()) {
                        String[] tmp = {"rm", "-r", args[args.length - 1] + "/" + file.getName()};
                        remove(tmp);
                    }
                } catch (NullPointerException e) {
                    System.err.println(e.getMessage());
                    return;
                }
            }
        }

        if (!fl.delete()) {
            System.err.println("can't remove: " + args[args.length - 1]);
            return;
        }

    }

    private void cp(String[] args) {
        if (args.length > 4 | args.length < 2) {
            System.err.println("incorrect arguments");
            return;
        }
        if (args.length == 4 && !args[1].equals("-r")) {
            System.err.println("incorrect arguments");
            return;
        }
        File source = new File(System.getProperty("user.dir") + "/" + args[args.length - 2]);
        File path = new File(System.getProperty("user.dir") + "/" + args[args.length - 1]);
        if (source.equals(path)) {
            System.err.println("this files are equal");
            return;
        }

        if (source.isDirectory() && args.length == 3) {
            System.err.println("This is a directory: " + args[args.length - 2]);
            return;
        }

        if (source.isDirectory() && path.isFile()) {
            System.err.println("can't copy directory to the file");
            return;
        }

        if (path.exists() && path.isDirectory()) {
            path = new File(path.getAbsolutePath() + "/" + source.getName());
        }

        if (!source.exists()) {
            System.err.println("There is no such file or directory: " + args[args.length - 2]);
            return;
        }
        copy(source.getAbsolutePath(), path.getAbsolutePath());
    }

    private void copy(String now, String to) throws NullPointerException {
        File source = new File(now);
        File path = new File(to);

        if (source.isDirectory()) {
            if (!path.exists()) {
                if (!path.mkdir()) {
                    System.err.println("Can't create directory: " + to);
                    return;
                }
            }
            if (source.listFiles() != null) {
                try {
                    for (File file : source.listFiles()) {
                        copy(file.getAbsolutePath(), path.getAbsolutePath() + "/" + file.getName());
                    }
                } catch (NullPointerException e) {
                    System.err.println(e.getMessage());
                    return;
                }
            }
            return;
        }


        try {
            FileReader fr = new FileReader(source);
            FileWriter fw = new FileWriter(path);
            char[] buf = new char[1];
            while (fr.read(buf) > 0) {
                fw.write(buf);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private String pathAppend(String path) {
        File tmpFile = new File(path);
        if (!tmpFile.isAbsolute()) {
            return directory.getAbsolutePath() + File.separator + path;
        } else {
            return tmpFile.getAbsolutePath();
        }
    }

    private void checkLen(String cmdName, int hasLen, int needLen) throws SException {
        if (needLen > hasLen) {
            throw new SException(cmdName, "Lack of arguments");
        } else if (needLen < hasLen) {
            throw new SException(cmdName, "Too many arguments");
        }
    }
}

class SException extends Exception {
    private final String command;
    private final String message;

    SException(String c, String m) {
        command = c;
        message = m;
    }

    @Override
    public String toString() {
        return (command + ": " + message);
    }
}
