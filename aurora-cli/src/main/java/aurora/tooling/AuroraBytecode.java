package aurora.tooling;

import aurora.analyzer.ModuleResolver;
import aurora.compiler.CompiledClass;
import aurora.compiler.CompiledFunction;
import aurora.compiler.Compiler;
import aurora.parser.AuroraParser;
import aurora.parser.tree.Program;
import aurora.runtime.Chunk;
import aurora.runtime.OpCode;
import picocli.CommandLine;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * CLI command for inspecting the bytecode structure of an Aurora source file.
 * It compiles a source file and prints a detailed breakdown of the constant pool
 * and instruction sequence, including nested functions and classes.
 */
@CommandLine.Command(name = "bytecode", description = "Display bytecode structure of an Aurora source file")
public class AuroraBytecode implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The Aurora source file (.ar)")
    private Path sourceFile;

    @CommandLine.Option(names = { "-v", "--verbose" }, description = "Show line/column info for each instruction")
    private boolean verbose;

    @Override
    public Integer call() {
        try {
            if (!Files.exists(sourceFile)) {
                System.err.println("File not found: " + sourceFile);
                return 1;
            }

            ModuleResolver modules = new ModuleResolver();
            modules.setProjectRoot(Paths.get("."));

            String code = Files.readString(sourceFile, StandardCharsets.UTF_8);
            Program program = AuroraParser.parse(code, sourceFile.getFileName().toString(), modules);

            Compiler compiler = new Compiler(modules);
            Chunk chunk = compiler.compile(program);

            System.out.println("=== Aurora Bytecode: " + sourceFile.getFileName() + " ===");
            System.out.println();
            printChunk(chunk, "<main>", 0);

            return 0;
        } catch (aurora.parser.SyntaxErrorException e) {
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
            return 1;
        }
    }

    private void printChunk(Chunk chunk, String name, int indentLevel) {
        String indent = "  ".repeat(indentLevel);

        System.out.println(indent + "── Chunk: " + name + " ──");
        System.out.println();

        // ---- Constant Pool ----
        System.out.println(indent + "  Constant Pool (" + chunk.constants.size() + " entries):");
        for (int i = 0; i < chunk.constants.size(); i++) {
            Object c = chunk.constants.get(i);
            System.out.printf("%s    #%-4d %s%n", indent, i, formatConstant(c));
        }
        System.out.println();

        // ---- Instructions ----
        System.out.println(indent + "  Instructions (" + chunk.count + " slots):");
        OpCode[] opcodes = OpCode.values();
        int i = 0;
        while (i < chunk.count) {
            int raw = chunk.code[i];
            if (raw < 0 || raw >= opcodes.length) {
                // Operand slot (should not be reached if we skip correctly)
                System.out.printf("%s    %04d: <data> %d%n", indent, i, raw);
                i++;
                continue;
            }

            OpCode op = opcodes[raw];
            StringBuilder line = new StringBuilder();
            line.append(String.format("%s    %04d: %-22s", indent, i, op.name()));

            int instrAddr = i;
            i++;

            switch (op) {
                case LOAD_CONST -> {
                    int idx = chunk.code[i++];
                    Object k = idx < chunk.constants.size() ? chunk.constants.get(idx) : "???";
                    line.append(String.format("#%-4d  // %s", idx, formatConstantBrief(k)));
                }
                case GET_LOCAL, SET_LOCAL -> {
                    int idx = chunk.code[i++];
                    line.append(String.format("slot_%d", idx));
                }
                case GET_GLOBAL, SET_GLOBAL -> {
                    int idx = chunk.code[i++];
                    Object k = idx < chunk.constants.size() ? chunk.constants.get(idx) : "???";
                    line.append(String.format("#%-4d  // \"%s\"", idx, k));
                }
                case GET_PROPERTY, SET_PROPERTY, SUPER_GET_PROPERTY, SUPER_SET_PROPERTY -> {
                    int idx = chunk.code[i++];
                    Object k = idx < chunk.constants.size() ? chunk.constants.get(idx) : "???";
                    line.append(String.format("#%-4d  // .%s", idx, k));
                }
                case INVOKE, SUPER_INVOKE -> {
                    int idx = chunk.code[i++];
                    int argc = chunk.code[i++];
                    Object k = idx < chunk.constants.size() ? chunk.constants.get(idx) : "???";
                    line.append(String.format("#%-4d argc=%-2d  // .%s()", idx, argc, k));
                }
                case JUMP, JUMP_IF_FALSE -> {
                    int target = chunk.code[i++];
                    line.append(String.format("-> %04d", target));
                }
                case TRY -> {
                    int catchAddr = chunk.code[i++];
                    line.append(String.format("catch-> %04d", catchAddr));
                }
                case CALL -> {
                    int argc = chunk.code[i++];
                    line.append(String.format("argc=%d", argc));
                }
                case NEW -> {
                    int argc = chunk.code[i++];
                    line.append(String.format("argc=%d", argc));
                }
                case NEW_ARRAY -> {
                    int size = chunk.code[i++];
                    line.append(String.format("size=%d", size));
                }
                case IMPORT -> {
                    int idx = chunk.code[i++];
                    Object k = idx < chunk.constants.size() ? chunk.constants.get(idx) : "???";
                    line.append(String.format("#%-4d  // %s", idx, formatConstantBrief(k)));
                }
                default -> {
                    // No operand
                }
            }

            if (verbose && instrAddr < chunk.count) {
                int ln = chunk.lines[instrAddr];
                int col = chunk.columns[instrAddr];
                if (ln > 0) {
                    line.append(String.format("  [L%d:%d]", ln, col));
                }
            }

            System.out.println(line);
        }
        System.out.println();

        // ---- Nested functions / classes ----
        for (int ci = 0; ci < chunk.constants.size(); ci++) {
            Object c = chunk.constants.get(ci);
            if (c instanceof CompiledFunction fn) {
                printChunk(fn.chunk(), "fun " + fn.name() + " (arity=" + fn.arity() + ")", indentLevel + 1);
            } else if (c instanceof CompiledClass cls) {
                System.out.println(indent + "  ── Class: " + cls.name + " ──");
                if (cls.superClassName != null) {
                    System.out.println(indent + "    extends: " + cls.superClassName);
                }
                if (cls.initializer != null) {
                    printChunk(cls.initializer.chunk(),
                            "constructor (arity=" + cls.initializer.arity() + ")",
                            indentLevel + 2);
                }
                for (var entry : cls.methods.entrySet()) {
                    printChunk(entry.getValue().chunk(),
                            "method " + entry.getKey() + " (arity=" + entry.getValue().arity() + ")",
                            indentLevel + 2);
                }
                System.out.println();
            }
        }
    }

    private String formatConstant(Object c) {
        if (c == null)
            return "None";
        if (c instanceof Integer)
            return "Int(" + c + ")";
        if (c instanceof Long)
            return "Long(" + c + "L)";
        if (c instanceof Float)
            return "Float(" + c + "f)";
        if (c instanceof Double)
            return "Double(" + c + ")";
        if (c instanceof Boolean)
            return "Bool(" + c + ")";
        if (c instanceof String)
            return "String(\"" + escapeString((String) c) + "\")";
        if (c instanceof CompiledFunction fn)
            return "Function(" + fn.name() + ", arity=" + fn.arity() + ")";
        if (c instanceof CompiledClass cls)
            return "Class(" + cls.name + ")";
        return c.getClass().getSimpleName() + "(" + c + ")";
    }

    private String formatConstantBrief(Object c) {
        if (c == null)
            return "none";
        if (c instanceof Integer)
            return c.toString();
        if (c instanceof Long)
            return c + "L";
        if (c instanceof Float)
            return c + "f";
        if (c instanceof Double)
            return c.toString();
        if (c instanceof Boolean)
            return c.toString();
        if (c instanceof String)
            return "\"" + escapeString((String) c) + "\"";
        if (c instanceof CompiledFunction fn)
            return "fun " + fn.name();
        if (c instanceof CompiledClass cls)
            return "class " + cls.name;
        return c.toString();
    }

    private static String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
