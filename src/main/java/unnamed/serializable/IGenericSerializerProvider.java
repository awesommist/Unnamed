package unnamed.serializable;

import java.lang.reflect.Type;

import unnamed.utils.io.IStreamSerializer;

public interface IGenericSerializerProvider {
    IStreamSerializer<?> getSerializer(Type type);
}