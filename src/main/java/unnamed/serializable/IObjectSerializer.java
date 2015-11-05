package unnamed.serializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IObjectSerializer<T> {
    void readFromStream(T object, DataInput input) throws IOException;

    void writeToStream(T object, DataOutput output) throws IOException;
}