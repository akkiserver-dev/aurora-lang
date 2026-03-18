package aurora.tooling;

import aurora.Main;
import aurora.analyzer.ModuleResolver;
import aurora.compiler.Compiler;
import aurora.parser.AuroraParser;
import aurora.parser.tree.Program;
import aurora.runtime.Chunk;
import picocli.CommandLine;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * CLI command for building Aurora projects.
 * This tool packages Aurora source files and configurations into an {@code .arpkg}
 * container (a ZIP file containing {@code main.arobj} and optional module descriptors).
 */
@CommandLine.Command(name = "build", description = "Build an Aurora project into an .arpkg file")
public class AuroraBuild implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The root directory of the project", defaultValue = ".")
    private Path projectRoot;

    @CommandLine.Option(names = { "-o", "--output" }, description = "Output file")
    private Path outputFile;

    @CommandLine.Option(names = { "-v", "--verbose" }, description = "Verbose output")
    private boolean verbose;

    @Override
    public Integer call() throws Exception {
        Path root = projectRoot.toAbsolutePath().normalize();
        if (!Files.exists(root)) {
            System.err.println("Project root does not exist: " + root);
            return 1;
        }

        // Check for *.armod
        Path modFile = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, "*.armod")) {
            for (Path entry : stream) {
                if (modFile != null) {
                    System.err.println("Error: Multiple .armod files found in " + root);
                    return 1;
                }
                modFile = entry;
            }
        }

        if (modFile == null) {
            System.err.println("Warning: .armod file not found in " + root + ". Assuming simple script structure.");
        } else {
            if (verbose)
                System.out.println("Found configuration: " + modFile.getFileName());
        }

        // Find main file
        Path mainFile = root.resolve("src/main.ar");
        if (!Files.exists(mainFile)) {
            // Fallback: search for any .ar file if src/main.ar doesn't exist?
            // Or just check if root itself is a file
            if (Files.isRegularFile(projectRoot) && projectRoot.toString().endsWith(".ar")) {
                mainFile = projectRoot;
                root = projectRoot.getParent();
            } else {
                System.err.println("Entry point 'src/main.ar' not found in " + root);
                return 1;
            }
        }

        if (verbose)
            System.out.println("Compiling " + mainFile + "...");

        try {
            String code = Files.readString(mainFile, StandardCharsets.UTF_8);
            ModuleResolver modules = new ModuleResolver();
            modules.setProjectRoot(Paths.get("."));

            Program program = AuroraParser.parse(code, mainFile.getFileName().toString(), modules);

            Compiler compiler = new Compiler(modules);
            // TODO: Add project libraries to compiler
            // compiler.addLibraryPath(root.resolve("lib"));

            Chunk chunk = compiler.compile(program);

            Path outPath;
            if (outputFile != null) {
                outPath = outputFile;
            } else {
                String name = root.getFileName().toString();
                if (Files.isRegularFile(projectRoot)) {
                    name = projectRoot.getFileName().toString().replace(".ar", "");
                }
                outPath = root.resolve(name + ".arpkg");
            }

            if (verbose)
                System.out.println("Packaging to " + outPath + "...");

            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outPath.toFile()))) {
                // Determine dependencies to include
                // For now, we only output the compiled chunk as 'main.arobj'
                // In future, if we support dynamic loading from within the package, we'd add
                // library chunks here.

                ZipEntry entry = new ZipEntry("main.arobj");
                zos.putNextEntry(entry);
                DataOutputStream dos = new DataOutputStream(zos);

                // Write magic and version
                dos.writeInt(0x4155524F); // 'AURO'
                dos.writeInt(0x00000002);

                Main.writeChunk(dos, chunk);

                dos.flush();
                zos.closeEntry();

                // Copy .armod if exists
                if (modFile != null) {
                    ZipEntry modEntry = new ZipEntry(modFile.getFileName().toString());
                    zos.putNextEntry(modEntry);
                    Files.copy(modFile, zos);
                    zos.closeEntry();
                }
            }

            System.out.println("Build successful: " + outPath);
            return 0;

        } catch (aurora.parser.SyntaxErrorException e) {
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Build failed: " + e.getMessage());
            if (verbose)
                e.printStackTrace();
            return 1;
        }
    }
}
