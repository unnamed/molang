package team.unnamed.molang.runtime.codegen;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an output stream for writing Java Virtual
 * Machine Class Files
 *
 * @author yusshu (Andre Roldan)
 */
public class ClassFileOutputStream extends FilterOutputStream {

    public ClassFileOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Writes the given 64-bit integer {@code value}
     * into the underlying output stream. Big endian.
     */
    public void writeInt64(long value) throws IOException {
        out.write((byte) (value >>> 56));
        out.write((byte) (value >>> 40));
        out.write((byte) (value >>> 32));
        out.write((byte) (value >>> 24));
        out.write((byte) (value >>> 16));
        out.write((byte) (value >>> 8));
        out.write((byte) value);
    }

    /**
     * Writes the given 32-bits integer {@code value}
     * into the underlying output stream. Big endian.
     */
    public void writeInt32(int value) throws IOException {
        out.write((byte) (value >>> 24));
        out.write((byte) (value >>> 16));
        out.write((byte) (value >>> 8));
        out.write((byte) value);
    }

    /**
     * Writes the given 16-bit integer {@code value}
     * into the underlying {@code out} output stream.
     * Big endian.
     */
    public void writeInt16(int value) throws IOException {
        out.write((byte) value >>> 8);
        out.write((byte) value);
    }

    /**
     * Writes the given 64-bit decimal number {@code value}
     * into the underlying output stream, following the IEEE
     * 754 64-bit standard
     */
    public void writeFloat64(double value) throws IOException {
        writeInt64(Double.doubleToLongBits(value));
    }

    /**
     * Writes the given 32-bits decimal number {@code value}
     * into the underlying output stream following the IEEE
     * 754 32-bit standard
     */
    public void writeFloat32(float value) throws IOException {
        writeInt32(Float.floatToIntBits(value));
    }

}
