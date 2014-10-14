package ru.fizteh.fivt.students.ValentinLeonov.FileMap;

import java.io.*;
import java.util.Map;

public class FileMap {
    public static void readTable(File dbName, State state) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(dbName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
        try {
            while (true) {
                int keyLength;
                int valueLength;
                byte[] keyBytes;
                byte[] valueBytes;

                try {
                    keyLength = dataInputStream.readInt();
                    keyBytes = new byte[keyLength];
                    dataInputStream.readFully(keyBytes);
                    valueLength = dataInputStream.readInt();
                    valueBytes = new byte[valueLength];
                    dataInputStream.readFully(valueBytes);
                } catch (EOFException ex) {
                    break;
                }


                if (keyLength <= 0 || valueLength <= 0 || keyLength > 1048576 || valueLength > 1048576) {
                    throw new IOException("Wrong string size");
                }

                if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                    throw new IOException("Corrupted database");
                }
                String key = new String(keyBytes, "UTF-8");
                String value = new String(valueBytes, "UTF-8");
                state.put(key, value);
            }
        } finally {
            dataInputStream.close();
        }
    }

    public static void writeTable(File dbName, State state) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(dbName);
        fileOutputStream.getChannel().truncate(0); // Clear file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        try {
            for (Map.Entry<String, String> entry : state.getEntrySet()) {
                dataOutputStream.writeInt(entry.getKey().getBytes("UTF-8").length);
                dataOutputStream.write(entry.getKey().getBytes("UTF-8"));
                dataOutputStream.writeInt(entry.getValue().getBytes("UTF-8").length);
                dataOutputStream.write(entry.getValue().getBytes("UTF-8"));
            }
        } finally {
            dataOutputStream.close();
        }

    }
}
