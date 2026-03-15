package aurora.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a compiled block of code in the Aurora VM.
 * A chunk contains a sequence of bytecode instructions, a constant pool,
 * and mapping information for source lines and columns to facilitate debugging.
 */
public class Chunk {
    /** The sequence of bytecode instructions. */
    public int[] code;

    /** The current number of instructions in the chunk. */
    public int count;

    /** An array mapping each instruction to its source line number. */
    public int[] lines;

    /** An array mapping each instruction to its source column number. */
    public int[] columns;

    /**
     * The constant pool for this chunk. 
     * During compilation, it stores raw data; during execution, it stores {@link ArObject} instances.
     */
    public final List<Object> constants = new ArrayList<>();

    /** The path to the source file from which this chunk was compiled. */
    public String sourceFile;

    /**
     * Constructs a new code chunk.
     * @param sourceFile The source file name for debugging.
     */
    public Chunk(String sourceFile) {
        this.sourceFile = sourceFile;
        this.code = new int[8];
        this.lines = new int[8];
        this.columns = new int[8];
        this.count = 0;
    }

    public void write(int byteCode, int line, int column) {
        if (count >= code.length) {
            int oldCapacity = code.length;
            int newCapacity = oldCapacity * 2;
            int[] newCode = new int[newCapacity];
            int[] newLines = new int[newCapacity];
            int[] newColumns = new int[newCapacity];
            System.arraycopy(code, 0, newCode, 0, oldCapacity);
            System.arraycopy(lines, 0, newLines, 0, oldCapacity);
            System.arraycopy(columns, 0, newColumns, 0, oldCapacity);
            code = newCode;
            lines = newLines;
            columns = newColumns;
        }
        code[count] = byteCode;
        lines[count] = line;
        columns[count] = column;
        count++;
    }

    public int addConstant(Object value) {
        constants.add(value);
        return constants.size() - 1;
    }
}
