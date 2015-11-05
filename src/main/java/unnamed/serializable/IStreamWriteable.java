package unnamed.serializable;

import java.io.DataOutput;
import java.io.IOException;

public interface IStreamWriteable {
    void writeToStream(DataOutput output) throws IOException;
}