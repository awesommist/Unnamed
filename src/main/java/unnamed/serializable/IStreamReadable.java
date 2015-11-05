package unnamed.serializable;

import java.io.DataInput;
import java.io.IOException;

public interface IStreamReadable {
    void readFromStream(DataInput input) throws IOException;
}