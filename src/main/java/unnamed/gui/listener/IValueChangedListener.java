package unnamed.gui.listener;

public interface IValueChangedListener<T> extends IListenerBase {
    void valueChanged(T value);
}