# Compiler & CLI Toolchain

The Aurora command-line interface (`aurora`) provides tools for running, compiling, and debugging Aurora source code.

## CLI Commands

### 1. `run`
Runs an Aurora source file. If the file is a `.ar` source, it is compiled in-memory first. If it is a `.arobj` binary, it is loaded directly.

```bash
aurora run main.ar [options]
aurora run main.arobj [options]
```

**Options:**
- `--outputAst`: Parses the file and prints the Abstract Syntax Tree (AST) to console. Does not execute the program.
- `-v`, `--verbose`: Enables detailed logging and stack traces on error.

### 2. `compile`
Compiles an Aurora source file into a portable bytecode binary (`.arobj`).

```bash
aurora compile source.ar -o output.arobj
```

**Options:**
- `-o`, `--output <path>`: Specifies the destination for the compiled binary. Defaults to `<filename>.arobj`.
- `-v`, `--verbose`: Detailed output during the compilation process.

## Object File Format (`.arobj`)

Aurora compiled files are binary documents with a specific structure:

1.  **Magic Number**: `0x4155524F` (ASCII 'AURO')
2.  **Version**: `0x00000002` (Current VM version)
3.  **Constant Pool**: A table of literals (Strings, Numbers, Functions, Classes) serialized for efficient loading.
4.  **Bytecode**: The sequence of virtual machine instructions (`OpCode`).

## Virtual Machine (VM)
The Aurora VM is a stack-based executor. It uses **4-byte instructions** and maintains a stack of `ArObject` instances. Because it is written in Java, it benefits from the underlying JVM's memory management and JIT potential.
 stone.
