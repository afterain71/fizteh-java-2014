package ru.fizteh.fivt.students.ValentinLeonov.FileMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class State {
    private HashMap<String, String> hashMap;
    private File currentTable;
    private File dbDirectory;

    public State(File directory) {
        hashMap = new HashMap<String, String>();
        dbDirectory = directory;
    }

    public String put(String key, String value) {
        return hashMap.put(key, value);
    }

    public String get(String key) {
        return hashMap.get(key);
    }

    public String remove(String key) {
        return hashMap.remove(key);
    }

    public File getCurrentTable() {
        return currentTable;
    }

    public void changeCurrentTable(File table) {
        currentTable = table;
    }

    public File getDbDirectory() {
        return dbDirectory;
    }

    void list() {
        if (hashMap.isEmpty()) {
            System.out.println();
        }
        for (Map.Entry<String, String> pair : hashMap.entrySet()) {
            if (pair.getValue() != null) {
                System.out.println(pair.getKey());
            }
        }
    }

    public Set<Map.Entry<String, String>> getEntrySet() {
        return hashMap.entrySet();
    }

}
