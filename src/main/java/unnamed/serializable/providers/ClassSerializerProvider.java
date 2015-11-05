package unnamed.serializable.providers;

import unnamed.serializable.IObjectSerializer;
import unnamed.serializable.ISerializerProvider;
import unnamed.serializable.SerializerAdapters;
import unnamed.serializable.cls.ClassSerializersProvider;
import unnamed.serializable.cls.SerializableClass;
import unnamed.utils.io.IStreamSerializer;

public class ClassSerializerProvider implements ISerializerProvider {

    @Override
    public IStreamSerializer<?> getSerializer(Class<?> cls) {
        if (cls.isAnnotationPresent(SerializableClass.class)) {
            IObjectSerializer<Object> objectSerializer = ClassSerializersProvider.instance.getSerializer(cls);
            return SerializerAdapters.createFromObjectSerializer(cls, objectSerializer);
        }
        return null;
    }
}