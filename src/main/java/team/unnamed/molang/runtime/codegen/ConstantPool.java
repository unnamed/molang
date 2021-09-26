package team.unnamed.molang.runtime.codegen;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the JVM Class Constant Pool, contains
 * several kinds of constants for the run-time
 * See https://docs.oracle.com/javase/specs/jvms/se8/jvms8.pdf
 *
 * <strong>This class is not thread-safe</strong>
 * <p>
 * Based on mozilla/rhino ConstantPool
 */
class ConstantPool implements ClassFileComponent {

    static final int MAX_STRING_LENGTH = (1 << 16) - 1;

    /**
     * JVM Constant Pool tag types, see Java
     * Virtual Machine specification Table 4.4-A
     */
    static final byte
            CONSTANT_Class = 7,
            CONSTANT_Fieldref = 9,
            CONSTANT_Methodref = 10,
            CONSTANT_InterfaceMethodref = 11,
            CONSTANT_String = 8,
            CONSTANT_Integer = 3,
            CONSTANT_Float = 4,
            CONSTANT_Long = 5,
            CONSTANT_Double = 6,
            CONSTANT_NameAndType = 12,
            CONSTANT_Utf8 = 1,
            CONSTANT_MethodHandle = 15,
            CONSTANT_MethodType = 16,
            CONSTANT_InvokeDynamic = 18;

    /**
     * Hash table holding relations of Utf8 structure
     * values to their indexes in the pool.
     *
     * <p>It's used to avoid writing the same string
     * multiple times and use their cached index
     * instead</p>
     */
    final Map<String, Integer> utf8Indexes = new HashMap<>();

    /**
     * re-usable character array, used to write
     * UTF-8 strings to this constant pool
     */
    char[] charBuffer = new char[64];

    byte[] buffer;
    int off = 0;
    int count = 1;

    ConstantPool(int initialCapacity) {
        this.buffer = new byte[initialCapacity];
    }

    /**
     * Checks that the {@code buffer} can receive
     * the given {@code capacity} amount of bytes
     * for the next write, if it can't, it allocates
     * a new byte array of the required size
     */
    void ensureCapacity(int capacity) {
        if (off + capacity > buffer.length) {
            int newCapacity = buffer.length * 2;

            if (off + capacity > buffer.length) {
                newCapacity = off + buffer.length;
            }

            byte[] newPool = new byte[newCapacity];
            System.arraycopy(buffer, 0, newPool, 0, off);
            buffer = newPool;
        }
    }

    /**
     * Checks that the {@code charBuffer} can receive
     * the given {@code capacity} amount of chars
     * for the next write, if it can't, it allocates
     * a new char array of the required size
     */
    void ensureCharBufferCapacity(int capacity) {
        if (capacity > charBuffer.length) {
            charBuffer = new char[Math.max(charBuffer.length * 2, capacity)];
        }
    }

    //#region Structure write methods

    /**
     * Writes the given {@code integer} CONSTANT_Integer_info
     * structure into the {@code buffer} byte array
     *
     * @param value The written integer
     * @return The index where the value was written
     */
    int writeInt(int value) {
        ensureCapacity(5); // tag + 4 bytes
        count++;
        int loc = off;
        // write tag
        buffer[off++] = CONSTANT_Integer;
        // write integer
        off = Bytes.writeInt32(buffer, value, off);
        return loc;
    }

    /**
     * Writes the given {@code value} CONSTANT_Long_info
     * structure into the {@code buffer} byte array
     *
     * @param value The written value
     * @return The index where the value was written
     */
    int writeLong(long value) {
        ensureCapacity(9); // tag + 8 bytes
        count++;
        int loc = off;
        // write tag
        buffer[off++] = CONSTANT_Long;
        // write long
        off = Bytes.writeInt64(buffer, value, off);
        return loc;
    }

    /**
     * Writes the given {@code value} CONSTANT_Double_info
     * structure into the {@code buffer} byte array
     *
     * @param value The written value
     * @return The index where the value was written
     * @see Double#doubleToLongBits(double)
     */
    int writeDouble(double value) {
        ensureCapacity(9); // tag + 8 bytes
        count++;
        int loc = off;
        // write tag
        buffer[off++] = CONSTANT_Double;
        // write double
        off = Bytes.writeFloat64(buffer, value, off);
        return loc;
    }

    /**
     * Writes the given {@code value} CONSTANT_Float_info
     * structure into the {@code buffer} byte array
     * following the IEEE 754 standard
     *
     * @param value The written value
     * @return The index where the value was written
     * @see Float#floatToIntBits
     */
    int writeFloat(float value) {
        ensureCapacity(5); // tag + 4 bytes
        count++;
        int loc = off;
        // write tag
        buffer[off++] = CONSTANT_Float;
        // write float
        off = Bytes.writeFloat32(buffer, value, off);
        return loc;
    }

    /**
     * Writes the given {@code string} CONSTANT_Utf8_info
     * structure into the {@code buffer} byte array
     * <p>
     * See JVM Spec 4.4.7
     *
     * @param string The written string
     * @return The string index at {@code buffer}
     */
    int writeUtf8(String string) {

        // CONSTANT_Utf8_info structure:
        // u1 tag;
        // u2 length;
        // u1 bytes[length];
        int index = utf8Indexes.getOrDefault(string, -1);

        if (index != -1) {
            // if there is a cached index for
            // the given string, use it
            return index;
        }

        int len = string.length();
        if (len > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("String too big");
        }

        // ensure capacity for the worst case where
        // we must use three bytes for all the string
        // characters
        ensureCapacity(3 + len * 3); // tag(1byte) + length (2bytes) + length

        // location (index) of this string
        int loc = off;

        // write tag
        buffer[off++] = CONSTANT_Utf8;

        // skip length, it will be written later
        int lenLoc = off;
        off += 2;

        // ensure charBuffer has a 'len' length as minimum
        ensureCharBufferCapacity(len);

        string.getChars(0, len, charBuffer, 0);
        int cursorBeforeChars = off;

        for (int i = 0; i < len; i++) {
            int c = charBuffer[i];
            // refer to the jvm specification
            if (c >= 0x01 && c < 0x7F) {
                // we can use a single byte for
                // this code-point!
                buffer[off++] = (byte) c;
            } else if (c > 0x7FF) {
                // so big, we must use three bytes
                // in this case
                buffer[off++] = (byte) (0xE0 | (c >> 12));
                buffer[off++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buffer[off++] = (byte) (0x80 | (c & 0x3F));
            } else {
                // we must use two bytes in this case
                buffer[off++] = (byte) (0xC0 | (c >> 6));
                buffer[off++] = (byte) (0x80 | (c & 0x3F));
            }
        }

        int utf8Len = off - cursorBeforeChars;
        if (utf8Len > MAX_STRING_LENGTH) {
            throw new IllegalStateException("String too big");
        }

        // now correctly write length
        buffer[lenLoc++] = (byte) (utf8Len >>> 8);
        buffer[lenLoc] = (byte) utf8Len;
        count++;

        // save the index for this string
        utf8Indexes.put(string, loc);
        return loc;
    }

    /**
     * Writes the given {@code string} CONSTANT_String_info
     * structure into the {@code buffer} byte array
     *
     * @param stringIndex The written Utf8 string index
     * @return The written structure index in the pool
     */
    int writeString(int stringIndex) {
        // CONSTANT_String_info {
        //   u1 tag;                // tag
        //   u2 string_index;       // Utf8 string in pool
        // }

        int loc = off;
        ensureCapacity(3); // tag(1byte) + index(2bytes)
        count++;
        // write tag
        buffer[off++] = CONSTANT_String;
        off = Bytes.writeInt16(buffer, stringIndex, off);
        return loc;
    }

    /**
     * Writes the given {@code name} and {@code type} as
     * a CONSTANT_NameAndType_info structure into the
     * {@code buffer} byte array
     *
     * @param nameIndex       The written name Utf8 index in pool
     * @param descriptorIndex The written type Utf8 index in pool
     * @return The written structure index in the pool
     */
    int writeNameAndType(int nameIndex, int descriptorIndex) {
        // CONSTANT_NameAndType_info {
        //   u1 tag;                      // tag
        //   u2 name_index;               // Utf8 string in pool
        //   u2 descriptor_index;         // Utf8 string in pool

        int loc = off;
        ensureCapacity(5); // tag(1byte) + name(2bytes) + desc(2bytes)
        count++;

        // write tag
        buffer[off++] = CONSTANT_NameAndType;

        off = Bytes.writeInt16(buffer, nameIndex, off);
        off = Bytes.writeInt16(buffer, descriptorIndex, off);

        return loc;
    }

    /**
     * Writes the given {@code name} as a CONSTANT_Class_info
     * structure into the {@code buffer} byte array
     *
     * @param nameIndex The class name Utf8 pool index
     * @return The structure index in the pool
     */
    int writeClass(int nameIndex) {
        // CONSTANT_Class_info {
        //   u1 tag;
        //   u2 name_index;
        // }

        int loc = off;
        ensureCapacity(3); // tag(1byte) + name(2bytes)
        count++;

        // write tag
        buffer[off++] = CONSTANT_Class;

        // write name index
        off = Bytes.writeInt16(buffer, nameIndex, off);

        return loc;
    }

    /**
     * Writes a CONSTANT_Fieldref_info, CONSTANT_Methodref_info
     * or a CONSTANT_InterfaceMethodref_info, depending on the given
     * {@code tag}.
     *
     * <p>This method supports multiple structures because of their
     * similarity</p>
     *
     * @param classIndex       Index of a valid CONSTANT_Class_info structure
     * @param nameAndTypeIndex Index of a valid CONSTANT_NameAndType_info structure
     * @return The written structure index in the pool
     */
    int writeRef(byte tag, int classIndex, int nameAndTypeIndex) {
        int loc = off;
        count++;
        // write tag
        buffer[off++] = tag;

        off = Bytes.writeInt16(buffer, classIndex, off);
        off = Bytes.writeInt16(buffer, nameAndTypeIndex, off);
        return loc;
    }

    /**
     * Writes a CONSTANT_MethodHandle_info into the underlying
     * {@code buffer} byte array
     *
     * @param referenceKind  The reference kind 0-9
     * @param referenceIndex The reference index, a valid
     *                       index in the pool
     * @return The index where this structure was written
     */
    int writeMethodHandle(byte referenceKind, int referenceIndex) {
        int loc = off;
        count++;

        // write tag
        buffer[off++] = CONSTANT_MethodHandle;

        // write reference kind
        buffer[off++] = referenceKind;

        // write reference index
        off = Bytes.writeInt16(buffer, referenceIndex, off);

        return loc;
    }

    /**
     * Writes a CONSTANT_MethodType into the underlying
     * {@code buffer} byte array
     *
     * @param descriptorIndex The Utf8 descriptor index in the pool
     * @return The index where this structure was written
     */
    int writeMethodType(int descriptorIndex) {
        int loc = off;
        count++;

        // write tag
        buffer[off++] = CONSTANT_MethodType;

        // write descriptor index
        off = Bytes.writeInt16(buffer, descriptorIndex, off);

        return loc;
    }

    /**
     * Writes a CONSTANT_InvokeDynamic_info structure into
     * the underlying {@code buffer} byte array
     *
     * @param methodIndex      The method index, must be a valid
     *                         index into the {@code bootstrap_methods}
     *                         array of the bootstrap method table of
     *                         this class file
     * @param nameAndTypeIndex Index of a CONSTANT_NameAndType_info
     *                         structure in this pool
     * @return The index where this structure was written
     */
    int writeInvokeDynamic(int methodIndex, int nameAndTypeIndex) {
        // arguments don't match, but it's the same structure so...
        return writeRef(CONSTANT_InvokeDynamic, methodIndex, nameAndTypeIndex);
    }
    //#endregion

    @Override
    public int size() {
        int size = 0;
        size += 2; // pool length
        size += off; // pool bytes
        return size;
    }

    @Override
    public int write(byte[] b, int off) {
        int len = this.off;
        off = Bytes.writeInt16(b, len, off);
        System.arraycopy(buffer, 0, b, off, len);
        off += len;
        return off;
    }

}
