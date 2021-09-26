package team.unnamed.molang.runtime.codegen;

import java.io.IOException;

/**
 * Base interface for representing Java Class File
 * components like the constant pool, fields, methods,
 * attributes, the class file itself, etc.
 *
 * <p>Represents any object that can be sized and
 * written to a byte array</p>
 */
interface ClassFileComponent {

    /**
     * Returns the size of this component instance
     * in bytes
     */
    int size();

    /**
     * Writes this component into the given {@code out}
     * output stream.
     *
     * <p>The written bytes must be the same as specified
     * with {@link ClassFileComponent#size()}</p>
     */
    void write(ClassFileOutputStream out) throws IOException;

}
