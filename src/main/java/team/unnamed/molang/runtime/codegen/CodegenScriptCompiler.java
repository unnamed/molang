package team.unnamed.molang.runtime.codegen;

import team.unnamed.molang.ast.Node;
import team.unnamed.molang.runtime.CompiledScript;
import team.unnamed.molang.runtime.ScriptCompiler;

import java.io.EOFException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link ScriptCompiler} that compiles
 * the syntax tree to JVM bytecode
 */
public class CodegenScriptCompiler implements ScriptCompiler {

    /**
     * File header constant, the "magic number" of the
     * JVM class files
     */
    private static final int CLASS_MAGIC_NUMBER = 0xCAFEBABE;

    private static final int MAJOR_VERSION;
    private static final int MINOR_VERSION;
    private static final boolean GENERATE_STACK_MAP;

    /**
     * Name of the super-class for all the generated script
     * classes
     */
    private static final String SUPER_CLASS_NAME = CompiledScript.class.getName().replace('.', '/');

    /**
     * Package for generated class files
     */
    private static final String GENERATED_PACKAGE = "team/unnamed/molang/generated";

    /**
     * Identifier counter for generating unique class
     * names for the generated class files
     */
    private static final AtomicInteger IDENTIFIER = new AtomicInteger(0);

    static {
        int major = 48;
        int minor = 0;

        try (InputStream input = CodegenScriptCompiler.class
                .getResourceAsStream("team/unnamed/molang/runtime/CodegenScriptCompiler.class")) {
            if (input != null) {
                // 4 bytes: magic number 0xCAFEBABE
                // 2 bytes: minor version
                // 2 bytes: major version
                byte[] header = new byte[8];

                if (input.read(header) == -1) {
                    throw new EOFException();
                }

                minor = (header[4] << 8) | (header[5] & 0xFF);
                major = (header[6] << 8) | (header[7] & 0xFF);
            }
        } catch (Exception ignored) {
        }

        MINOR_VERSION = minor;
        MAJOR_VERSION = major;
        GENERATE_STACK_MAP = major >= 50;
    }

    /**
     * Method for generating unique class names for
     * the generated class files
     * @param name The user-friendly class name
     * @return A unique class name containing the
     * specified {@code name}
     */
    private static String createName(String name) {
        return GENERATED_PACKAGE + name + '_'
                + Integer.toHexString(IDENTIFIER.getAndIncrement());
    }

    public byte[] compile(Node root, String name) {
        String className = createName(name);

        ClassFile clazz = new ClassFile(className, SUPER_CLASS_NAME);

        // create constructor
        {
            ClassFileMethod method = clazz.newMethod("<init>", "()V", Bytecode.ACC_PUBLIC);
            method.code[off++] = Bytecode.ALOAD_0; // load "this"

        }

        byte[] data = new byte[clazz.size()];
        clazz.write(data, 0);
        return data;
    }

}
