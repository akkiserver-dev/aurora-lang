package aurora.compiler;

import aurora.runtime.Chunk;

/**
 * Intermediate function data structure used during compilation.
 * This record encapsulates the metadata and bytecode for a function or method.
 *
 * @param name  The name of the function.
 * @param chunk The bytecode chunk containing the function's instructions.
 * @param arity The number of arguments the function accepts.
 */
public record CompiledFunction(String name, Chunk chunk, int arity) {
}
