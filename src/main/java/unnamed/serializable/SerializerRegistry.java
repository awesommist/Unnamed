package unnamed.serializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import unnamed.reflection.ConstructorAccess;
import unnamed.reflection.TypeUtils;
import unnamed.serializable.providers.*;
import unnamed.utils.io.IStreamReader;
import unnamed.utils.io.IStreamSerializer;
import unnamed.utils.io.IStreamWriter;
import unnamed.utils.io.TypeRW;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SerializerRegistry {

    public static final SerializerRegistry instance = new SerializerRegistry();

    private final Map<Class<?>, IStreamSerializer<?>> serializers = Maps.newHashMap(TypeRW.STREAM_SERIALIZERS);

    private final List<ISerializerProvider> providers = Lists.newArrayList();

    private final List<IGenericSerializerProvider> genericProviders = Lists.newArrayList();

    {
        providers.add(new EnumSerializerProvider());
        providers.add(new ArraySerializerProvider());
        providers.add(new ClassSerializerProvider());

        genericProviders.add(new ListSerializerProvider());
        genericProviders.add(new SetSerializerProvider());
        genericProviders.add(new MapSerializerProvider());
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> resolve(Class<?> intf, Class<?> concrete) {
        final Class<?> rawType = TypeUtils.getTypeParameter(intf, concrete).getRawType();
        return (Class<? extends T>)rawType;
    }

    public <T> void register(Class<? extends T> target, IStreamSerializer<T> serializer) {
        Preconditions.checkArgument(target != Object.class, "Can't register serializer for Object");
        final IStreamSerializer<?> prev = serializers.put(target, serializer);
        Preconditions.checkState(prev == null, "Duplicate serializer for %s", target);
    }

    public <T> void register(IStreamSerializer<T> serializer) {
        Class<? extends T> cls = resolve(IStreamSerializer.class, serializer.getClass());
        register(cls, serializer);
    }

    public <T extends IStreamWriteable & IStreamReadable> void registerSerializable(Class<? extends T> cls, IInstanceFactory<T> factory) {
        register(cls, SerializerAdapters.createFromFactory(factory));
    }

    public <T extends IStreamWriteable & IStreamReadable> void registerSerializable(IInstanceFactory<T> factory) {
        Class<? extends T> cls = resolve(IInstanceFactory.class, factory.getClass());
        registerSerializable(cls, factory);
    }

    public <T extends IStreamWriteable & IStreamReadable> void registerSerializable(Class<T> cls) {
        IInstanceFactory<T> factory = ConstructorAccess.create(cls);
        registerSerializable(cls, factory);
    }

    public <T extends IStreamWriteable> void registerWriteable(Class<? extends T> cls, IStreamReader<T> reader) {
        register(cls, SerializerAdapters.createFromReader(reader));
    }

    public <T extends IStreamWriteable> void registerWriteable(IStreamReader<T> reader) {
        Class<? extends T> cls = resolve(IStreamReader.class, reader.getClass());
        registerWriteable(cls, reader);
    }

    public void registerProvider(ISerializerProvider provider) {
        Preconditions.checkNotNull(provider);
        providers.add(provider);
    }

    private IStreamSerializer<?> findClassSerializer(Class<?> cls, IStreamSerializer<?> serializer) {
        for (ISerializerProvider provider : providers) {
            serializer = provider.getSerializer(cls);
            if (serializer != null) {
                serializers.put(cls, serializer);
                return serializer;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> IStreamSerializer<T> findSerializer(Class<? extends T> cls) {
        IStreamSerializer<?> serializer = serializers.get(cls);

        if (serializer == null) serializer = findClassSerializer(cls, serializer);

        return (IStreamSerializer<T>) serializer;
    }

    public IStreamSerializer<Object> findSerializer(Type type) {
        if (type instanceof Class) return findSerializer((Class<?>)type);
        return findGenericSerializer(type);
    }

    @SuppressWarnings("unchecked")
    protected IStreamSerializer<Object> findGenericSerializer(Type type) {
        for (IGenericSerializerProvider provider : genericProviders) {
            IStreamSerializer<?> serializer = provider.getSerializer(type);
            if (serializer != null) return (IStreamSerializer<Object>) serializer;
        }

        return null;
    }

    public <T> T createFromStream(DataInput input, Class<? extends T> cls) throws IOException {
        IStreamReader<T> reader = findSerializer(cls);
        Preconditions.checkNotNull(reader, "Can't find reader for %s", cls);
        return reader.readFromStream(input);
    }

    public Object createFromStream(DataInput input, Type type) throws IOException {
        IStreamReader<?> reader = findSerializer(type);
        Preconditions.checkNotNull(reader, "Can't find reader for %s", type);
        return reader.readFromStream(input);
    }

    public <T> void writeToStream(DataOutput output, Class<? extends T> cls, T target) throws IOException {
        Preconditions.checkNotNull(target);

        IStreamWriter<T> writer = findSerializer(cls);
        Preconditions.checkNotNull(writer, "Can't find writer for %s", cls);
        writer.writeToStream(target, output);
    }

    public void writeToStream(DataOutput output, Type type, Object target) throws IOException {
        Preconditions.checkNotNull(target);

        IStreamWriter<Object> writer = findSerializer(type);
        Preconditions.checkNotNull(writer, "Can't find writer for %s", type);
        writer.writeToStream(target, output);
    }
}