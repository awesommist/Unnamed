package unnamed.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableBoolean extends SyncableObjectBase implements ISyncableValueProvider<Boolean> {

    private boolean value;

    public SyncableBoolean() {
        value = false;
    }

    public SyncableBoolean(boolean val) {
        value = val;
    }

    @Override
    public void readFromStream(DataInputStream stream) throws IOException {
        value = stream.readBoolean();
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeBoolean(value);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag, String name) {
        value = tag.getBoolean(name);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag, String name) {
        tag.setBoolean(name, value);
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean newValue) {
        if (newValue != value) {
            value = newValue;
            markDirty();
        }
    }

    public void toggle() {
        value = !value;
        markDirty();
    }
}