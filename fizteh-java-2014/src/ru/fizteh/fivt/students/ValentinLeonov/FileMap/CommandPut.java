package ru.fizteh.fivt.students.ValentinLeonov.FileMap;

import java.io.IOException;

public class CommandPut implements Command {
    public String getName() {
        return "put";
    }

    public int getArgumentsCount() {
        return 2;
    }

    public void execute(State state, String[] args) throws IOException, ExitException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String oldValue = state.put(args[1], args[2]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
