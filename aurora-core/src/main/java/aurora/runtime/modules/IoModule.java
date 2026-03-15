package aurora.runtime.modules;

import aurora.runtime.AuroraLib;
import aurora.runtime.AuroraNative;
import aurora.runtime.NativeBinder;
import aurora.runtime.VM;

import java.util.Scanner;

/**
 * Native module providing Input/Output (I/O) capabilities to Aurora.
 * This includes console printing, reading from stdin, and file system operations.
 */
@AuroraLib("Aurora.Io")
public class IoModule implements NativeModule {
    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public void register(VM vm) {
        NativeBinder.bind(vm, this);
    }

    @AuroraNative("Aurora.Io._print(Lobject;)")
    public void _print(Object msg) {
        System.out.print(msg);
    }

    @AuroraNative("Aurora.Io._println(Lobject;)")
    public void _println(Object msg) {
        System.out.println(msg);
    }

    @AuroraNative("Aurora.Io._readLine()")
    public String _readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

    @AuroraNative("Aurora.Io._readText(Lstring;)")
    public String _readText(String path) {
        try {
            return java.nio.file.Files.readString(java.nio.file.Path.of(path));
        } catch (java.io.IOException e) {
            throw new aurora.runtime.AuroraRuntimeException("Failed to read file: " + e.getMessage());
        }
    }

    @AuroraNative("Aurora.Io._writeText(Lstring;Lstring;)")
    public void _writeText(String path, String content) {
        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of(path), content);
        } catch (java.io.IOException e) {
            throw new aurora.runtime.AuroraRuntimeException("Failed to write to file: " + e.getMessage());
        }
    }
}
