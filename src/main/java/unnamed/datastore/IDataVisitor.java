package unnamed.datastore;

public interface IDataVisitor<K, V> {
    void begin(int size);

    void entry(K key, V value);

    void end();
}