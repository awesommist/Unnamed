package unnamed.utils.io;

public interface IStringSerializer<T> {
    T readFromString(String s);
}