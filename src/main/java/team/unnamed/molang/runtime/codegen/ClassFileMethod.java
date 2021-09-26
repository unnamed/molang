package team.unnamed.molang.runtime.codegen;

import java.io.IOException;

/**
 * Describes a Java Class Method, a 'method_info'
 * structure with a single attribute (Code_attribute)
 *
 * <p>The structure has the following format:</p>
 * <pre>
 *     method_info {
 *         u2             access_flags;
 *         u2             name_index;
 *         u2             descriptor_index;
 *         u2             attributes_count;
 *         attribute_info attributes[attributes_count];
 *     }
 * </pre>
 *
 * <p>See the Java Virtual Machine specification
 * section 4.6 and 4.7.3 (The Code Attribute)</p>
 *
 * @author yusshu (Andre Roldan)
 */
class ClassFileMethod implements ClassFileComponent {// TODO: make it not extend JavaField

    /**
     * Constant containing the code attribute name
     *
     * <p>See the Java Virtual Machine specification
     * section 4.7.3 The Code Attribute</p>
     */
    private static final String CODE_ATTRIBUTE_NAME = "Code";

    /**
     * Mask of flags used to denote access permission
     * and properties of this method
     *
     * <p>See the Java Virtual Machine specification
     * table 4.6-A</p>
     */
    private short flags;

    /**
     * Index into the constant_pool table, the entry at
     * that index must be a <b>CONSTANT_Utf8_info</b>
     * structure representing one of the special method
     * names (&lt;init&gt;, &lt;clinit&gt;) or a valid
     * unqualified name denoting a method
     */
    private final int nameIndex;

    /**
     * Index into the constant_pool table, the entry
     * at that index must be a <b>CONSTANT_Utf8_info</b>
     * structure representing a valid method descriptor
     */
    private final int descriptorIndex;

    //#region Code Attribute properties
    private final int codeAttributeNameIndex;
    private int maxStack;

    private byte[] code = new byte[256];
    //#endregion

    public ClassFileMethod(
            ClassFile clazz,
            String name,
            String descriptor,
            short flags
    ) {
        ConstantPool pool = clazz.getConstantPool();
        this.codeAttributeNameIndex = pool.writeUtf8("Code");
        this.nameIndex = pool.writeUtf8(name);
        this.descriptorIndex = pool.writeUtf8(descriptor);
        this.flags = flags;
    }

    public short getFlags() {
        return flags;
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    public void add(int op) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void write(ClassFileOutputStream out) throws IOException {

        out.writeInt16(flags);
        out.writeInt16(nameIndex);
        out.writeInt16(descriptorIndex);

        // we just need the 'Code' attribute for the method
        out.writeInt16(1);

        // The Code_attribute has the following format:
        // CodeAttribute {
        //   u2 attribute_name_index;
        //   u4 attribute_length;
        //   u2 max_stack;
        //   u2 max_locals;
        //   u4 code_length;
        //   u1 code[code_length];
        //   u2 exception_table_length;
        //   {
        //     u2 start_pc;
        //     u2 end_pc;
        //     u2 handler_pc;
        //     u2 catch_type;
        //   } exception_table[exception_table_length];
        //   u2 attributes_count;
        //   attribute_info attributes[attributes_count];
        // }
        out.writeInt16(codeAttributeNameIndex);
        out.writeInt32(attributeLength);
        out.writeInt16(maxStack);
        out.writeInt16(maxLocals);
        out.writeInt32(codeLength);
    }

}
