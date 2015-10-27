package unnamed.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableInt extends SyncableObjectBase implements ISyncableValueProvider<Integer> {

    private int value;

    public SyncableInt() {
        value = 0;
    }

    public SyncableInt(int val) {
        value = val;
    }

    @Override
    public void readFromStream(DataInputStream stream) throws IOException {
        value = stream.readInt();
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(value);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag, String name) {
        if (tag.hasKey(name)) value = tag.getInteger(name);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag, String name) {
        tag.setInteger(name, value);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public int get() {
        return value;
    }

    public void set(int newValue) {
        if (newValue != value) {
            value = newValue;
            markDirty();
        }
    }

    public void modify(int by) {
        set(value + by);
    }
}