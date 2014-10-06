package ru.fizteh.fivt.students.ValentinLeonov.Shell;

import java.io.File;
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
            copyOrMove(args, false);

        } else if (commandName.equals("mv")) {
            copyOrMove(args, true);

        } else if (commandName.equals("dir")) {
            dir(args);

        } else if (commandName.equals("exit")) {
            System.exit(0);

        } else {
            throw new SException("shell", "no such command");
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

    private void cd(String[] args) throws SException {
        try {
            checkLen(args[0], args.length - 1, 1);
            File tmpFile = new File(pathAppend(args[1]));
            if (tmpFile.isDirectory()) {
                directory = tmpFile;
            } else if (!tmpFile.exists()) {
                throw new SException(args[0], "\'" + args[1] + "\': no such file or directory");
            } else {
                throw new SException(args[0], "\'" + args[1] + "\': not a directory");
            }
        } catch (SException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new SException(args[0], exception.getMessage());
        }
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
        try {
            checkLen(args[0], args.length - 1, 1);
            Path pathToRemove = directory.toPath().resolve(args[1]).normalize();
            if (!Files.exists(pathToRemove)) {
                throw new SException(args[0], "Cannot be removed: File does not exist");
            }
            if (directory.toPath().normalize().startsWith(pathToRemove)) {
                throw new SException(args[0], "\'" + args[1] +
                        "\': Cannot be removed: First of all, leave this directory");
            }

            File fileToRemove = new File(pathAppend(args[1]));
            File[] filesToRemove = fileToRemove.listFiles();
            if (filesToRemove != null) {
                for (File file : filesToRemove) {
                    try {
                        String[] toRemove = new String[2];
                        toRemove[0] = args[0];
                        toRemove[1] = file.getPath();
                        remove(toRemove);
                    } catch (Exception exception) {
                        throw new SException(args[0], "\'" + file.getCanonicalPath()
                                + "\' : File cannot be removed: " + exception.getMessage() + " ");
                    }
                }
            }
            try {
                if (!Files.deleteIfExists(pathToRemove)) {
                    throw new SException(args[0], "\'" + fileToRemove.getCanonicalPath()
                            + "\' : File cannot be removed ");
                }
            } catch (DirectoryNotEmptyException exception) {
                throw new SException(args[0], "\'" + fileToRemove.getCanonicalPath() + "\' : Directory not empty");
            }
        } catch (SException se) {
            throw se;
        } catch (AccessDeniedException exception) {
            throw new SException(args[0], "Access denied");
        } catch (Exception exception) {
            throw new SException(args[0], exception.getMessage());
        }
    }

    private void copyOrMove(String[] args, boolean moveOrCopy) throws SException {
        String commandName;
        commandName = moveOrCopy ? "move" : "copy";
        try {
            checkLen(args[0], args.length - 1, 2);
            Path curDir = Paths.get(directory.getCanonicalPath());
            Path srcPath = curDir.resolve(args[1]).normalize();
            Path dstPath = curDir.resolve(args[2]).normalize();

            if (!Files.exists(srcPath)) {
                throw new SException(commandName, args[1] + ": file not exist");
            }

            if (srcPath.equals(dstPath)) {
                throw new SException(commandName, "It's the same file");
            }

            if (Files.isDirectory(dstPath)) {
                dstPath = dstPath.resolve(srcPath.getFileName()).normalize();
            } else if (Files.isDirectory(srcPath) && Files.exists(dstPath)) {
                throw new SException(commandName, "File that isn\'t directory.");
            }

            if (dstPath.startsWith(srcPath)) {
                throw new SException(commandName, "Cannot move/copy file: cycle copy:"
                        + srcPath.toString() + " -> " + dstPath.toString());
            }

            if (moveOrCopy) {
                Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            }

            File[] sourceEntries = srcPath.toFile().listFiles();
            if (sourceEntries != null) {
                for (File entry : sourceEntries) {
                    String name = entry.getName();
                    String[] nw = new String[3];
                    nw[0] = args[0];
                    nw[1] = srcPath.resolve(name).normalize().toString();
                    nw[2] = dstPath.resolve(name).normalize().toString();
                    copyOrMove(nw, moveOrCopy);
                }
            }
        } catch (SException se) {
            throw se;
        } catch (Exception exception) {
            throw new SException(commandName, exception.getMessage());
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
