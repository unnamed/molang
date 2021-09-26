package team.unnamed.molang.runtime.codegen;

/**
 * Describes a Java Class File
 */
class ClassFile implements ClassFileComponent {

    private final ConstantPool constantPool = new ConstantPool(256);

    /**
     * Mask of access flags to denote access
     * permission to and properties of this class
     *
     * @see Bytecode
     */
    private short flags = Bytecode.ACC_PUBLIC | Bytecode.ACC_SUPER;

    /**
     * Array of indexes to valid CONSTANT_Class_info
     * structures into the constant pool of this class
     *
     * <p>Use two bytes for writing the length</p>
     */
    private short[] interfaces;

    /**
     * Array of {@link ClassFileField} for completely
     * describing a Java field in this class or
     * interface
     *
     * <p>Use two bytes for writing the length</p>
     */
    private ClassFileField[] fields;

    /**
     * <p>Use two bytes for writing the length</p>
     */
    private ClassFileMethod[] methods;

    /**
     * <p>Use two bytes for writing the length</p>
     */
    private JavaAttribute[] attributes;

    /**
     * Index into the {@code constantPool} to
     * a CONSTANT_Class_info structure describing
     * this class
     */
    private final int classIndex;

    /**
     * Index into the {@code constantPool} to a
     * CONSTANT_Class_info structure describing
     * the parent class of this class
     */
    private final int superClassIndex;

    public ClassFile(String className, String superClassName) {
        this.classIndex = constantPool.writeClass(constantPool.writeUtf8(className));
        this.superClassIndex = constantPool.writeClass(constantPool.writeUtf8(superClassName));
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public ClassFileMethod newMethod(String name, String type, short flags) {
        /*ClassFileMethod method = new ClassFileMethod(
                flags,
                constantPool.writeUtf8(name),
                constantPool.writeUtf8(type),
                new JavaAttribute[0]
        );*/
        return null;
    }

    @Override
    public int size() {
        int size = 0;
        size += 4; // magic number 0xCAFEBABE
        size += 2; // minor version
        size += 2; // major version
        size += constantPool.size();
        size += 2; // flags
        size += 2; // class index (in constant pool)
        size += 2; // super class index (in constant pool)
        size += 2; // interfaces length
        size += interfaces.length * 2; // interface indexes
        size += 2; // fields length
        for (ClassFileField field : fields) {
            size += field.size();
        }
        size += 2; // methods length
        for (ClassFileMethod method : methods) {
            size += method.size();
        }
        size += 2; // attributes length
        for (JavaAttribute attribute : attributes) {
            size += attribute.size();
        }
        return size;
    }

    @Override
    public int write(byte[] data, int off) {
        // write header (magic number, minor and major version)
        off = Bytes.writeInt32(data, CodegenScriptCompiler.CLASS_MAGIC_NUMBER, off);
        off = Bytes.writeInt16(data, CodegenScriptCompiler.MINOR_VERSION, off);
        off = Bytes.writeInt16(data, CodegenScriptCompiler.MAJOR_VERSION, off);

        // write constant pool
        off = constantPool.write(data, off);

        // write class flags
        off = Bytes.writeInt16(data, flags, off);
        off = Bytes.writeInt16(data, classIndex, off);
        off = Bytes.writeInt16(data, superClassIndex, off);

        // write interfaces
        off = Bytes.writeInt16(data, interfaces.length, off);

        for (short interfaceIndex : interfaces) {
            off = Bytes.writeInt16(data, interfaceIndex, off);
        }

        // write fields
        off = Bytes.writeInt16(data, fields.length, off);
        for (ClassFileField field : fields) {
            off = field.write(data, off);
        }

        // write methods
        off = Bytes.writeInt16(data, methods.length, off);
        for (ClassFileMethod method : methods) {
            off = method.write(data, off);
        }

        // write attributes
        off = Bytes.writeInt16(data, attributes.length, off);
        for (JavaAttribute attribute : attributes) {
            off = attribute.write(data, off);
        }

        return off;
    }

}
