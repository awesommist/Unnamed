package unnamed.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableFloat extends SyncableObjectBase implements ISyncableValueProvider<Float> {

    public static final float EPSILON = 0.0001f;

    private float value;

    public SyncableFloat() {
        value = 0;
    }

    public SyncableFloat(float val) {
        value = val;
    }

    @Override
    public void readFromStream(DataInputStream stream) throws IOException {
        value = stream.readFloat();
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(value);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag, String name) {
        value = tag.getFloat(name);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag, String name) {
        tag.setFloat(name, value);
    }

    @Override
    public Float getValue() {
        return value;
    }

    public float get() {
        return value;
    }

    public boolean equals(float otherValue) {
        return Math.abs(otherValue - value) < EPSILON;
    }

    public void set(float newValue) {
        if (!equals(newValue)) {
            value = newValue;
            markDirty();
        }
    }

    public void modify(float by) {
        set(value + by);
    }
}