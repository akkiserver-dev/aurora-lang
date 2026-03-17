package aurora.runtime.modules;

import aurora.runtime.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Native module providing Input/Output (I/O) capabilities to Aurora.
 * This includes console printing, reading from stdin, and file system operations.
 */
@AuroraLib("Aurora.Io")
public class IoModule implements NativeModule {
    private static final Scanner scanner = new Scanner(System.in);
    private VM vm;

    @Override
    public void register(VM vm) {
        this.vm = vm;
        NativeBinder.bind(vm, this);
    }

    @AuroraNative("Aurora.Io.__native_io_print(Lobject;)")
    public void Aurora_Io_print(ArObject msg) {
        System.out.print(vm.arToString(msg));
    }

    @AuroraNative("Aurora.Io.__native_io_println(Lobject;)")
    public void Aurora_Io_println(ArObject msg) {
        System.out.println(vm.arToString(msg));
    }

    @AuroraNative("Aurora.Io.__native_io_readLine()")
    public String Aurora_Io_readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

    @AuroraNative("Aurora.Io._readText(Lstring;)")
    public String Aurora_Io_readText(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new AuroraRuntimeException("Failed to read file: " + e.getMessage());
        }
    }

    @AuroraNative("Aurora.Io._writeText(Lstring;Lstring;)")
    public void Aurora_Io_writeText(String path, String content) {
        try {
            Files.writeString(Path.of(path), content);
        } catch (IOException e) {
            throw new AuroraRuntimeException("Failed to write to file: " + e.getMessage());
        }
    }
}
