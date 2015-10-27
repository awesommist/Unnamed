package unnamed.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableByteArray extends SyncableObjectBase implements ISyncableValueProvider<byte[]> {

    private byte[] value;

    public SyncableByteArray() {
        value = new byte[0];
    }

    public SyncableByteArray(byte[] val) {
        value = val;
    }

    @Override
    public void readFromStream(DataInputStream stream) throws IOException {
        int length = stream.readInt();
        value = new byte[length];
        for (int i = 0; i < length; i++) {
            value[i] = stream.readByte();
        }
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        if (value == null) {
            stream.writeInt(0);
        } else {
            stream.writeInt(value.length);
            for (byte element : value) {
                stream.writeByte(element);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String name) {
        nbt.getByteArray(name);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String name) {
        nbt.setByteArray(name, value);
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] newValue) {
        if (newValue != value) {
            value = newValue;
            markDirty();
        }
    }
}