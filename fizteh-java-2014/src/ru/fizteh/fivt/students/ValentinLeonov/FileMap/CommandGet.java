package ru.fizteh.fivt.students.ValentinLeonov.FileMap;

import java.io.IOException;

public class CommandGet implements Command {
    public String getName() {
        return "get";
    }

    public int getArgumentsCount() {
        return 1;
    }

    public void execute(State state, String[] args) throws IOException, ExitException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String value = state.get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
