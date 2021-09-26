package team.unnamed.molang.runtime.codegen;

/**
 * Describes a Field of a Java class
 */
class ClassFileField implements ClassFileComponent {

    /**
     * Mask of access flags to denote access
     * permission to and properties of this field
     *
     * @see Bytecode
     */
    private short accessFlags;

    /**
     * Index to a CONSTANT_Utf8_info structure
     * in the constant pool
     *
     * <p>Use two bytes to write this index</p>
     */
    private int nameIndex;

    /**
     * Index to a CONSTANT_Utf8_info structure
     * in the constant pool which represents a
     * valid field descriptor
     *
     * <p>Use two bytes to write this index</p>
     */
    private int descriptorIndex;

    /**
     * Array of attributes for this Java Field
     *
     * <p>Use two bytes for writing the length</p>
     */
    private JavaAttribute[] attributes;

    public ClassFileField(
            short accessFlags,
            int nameIndex,
            int descriptorIndex,
            JavaAttribute[] attributes
    ) {
        this.accessFlags = accessFlags;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributes = attributes;
    }

    @Override
    public int size() {
        int size = 0;
        size += 2; // accessFlags
        size += 2; // nameIndex
        size += 2; // descriptorIndex
        size += 2; // attributes length
        for (JavaAttribute attribute : attributes) {
            size += attribute.size();
        }
        return size;
    }

    @Override
    public int write(byte[] buf, int off) {
        off = Bytes.writeInt16(buf, accessFlags, off);
        off = Bytes.writeInt16(buf, nameIndex, off);
        off = Bytes.writeInt16(buf, descriptorIndex, off);
        off = Bytes.writeInt16(buf, attributes.length & 0xFFFF, off);
        for (JavaAttribute attribute : attributes) {
            off = attribute.write(buf, off);
        }
        return off;
    }

}
