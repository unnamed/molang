package team.unnamed.molang.runtime.codegen;

/**
 * Attributes are used in the ClassFile, field_info, method_info
 * and Code_attribute structures of the JVM class file format
 *
 * <p>All attributes have the following general format:</p>
 *
 * <pre>
 *     attribute_info {
 *         u2 attribute_name_index;
 *         u4 attribute_length;
 *         u1 info[attribute_length];
 *     }
 * </pre>
 *
 * <p>For all attributes, the attribute_name_index must be a valid
 * unsigned 16-bit index into the constant pool of the class, where
 * a CONSTANT_Utf8_info structure must exist</p>
 */
class JavaAttribute implements ClassFileComponent {

    private short attributeNameIndex;

    /**
     * <p>Use 4 bytes to write the length</p>
     */
    private byte[] info;

    @Override
    public int size() {
        return info.length + 4 /* length */ + 2 /* name index */;
    }

    @Override
    public int write(byte[] buf, int off) {
        off = Bytes.writeInt16(buf, attributeNameIndex, off);
        off = Bytes.writeInt32(buf, info.length, off);
        System.arraycopy(info, 0, buf, 0, info.length);
        off += info.length;
        return off;
    }

}
