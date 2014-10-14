package ru.fizteh.fivt.students.ValentinLeonov.FileMap;

import java.io.IOException;

public class CommandList implements Command {
    public String getName() {
        return "list";
    }

    public int getArgumentsCount() {
        return 0;
    }

    public void execute(State state, String[] args) throws IOException, ExitException {
        state.list();
    }
}
