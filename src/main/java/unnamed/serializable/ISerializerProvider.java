package unnamed.serializable;

import unnamed.utils.io.IStreamSerializer;

public interface ISerializerProvider {
    IStreamSerializer<?> getSerializer(Class<?> cls);
}