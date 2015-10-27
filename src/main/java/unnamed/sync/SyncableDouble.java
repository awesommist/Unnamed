package unnamed.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableDouble extends SyncableObjectBase implements ISyncableValueProvider<Double> {

    private double value;

    public SyncableDouble() {
        value = 0;
    }

    public SyncableDouble(double val) {
        value = val;
    }

    @Override
    public void readFromStream(DataInputStream stream) throws IOException {
        value = stream.readDouble();
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeDouble(value);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag, String name) {
        value = tag.getDouble(name);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag, String name) {
        tag.setDouble(name, value);
    }

    @Override
    public Double getValue() {
        return value;
    }

    public double get() {
        return value;
    }

    public void set(double newValue) {
        if (newValue != value) {
            value = newValue;
            markDirty();
        }
    }

    public void modify(float by) {
        set(value + by);
    }
}