package unnamed.gui.listener;

import unnamed.gui.component.BaseComponent;

public interface IMouseDragListener extends IListenerBase {
    void componentMouseDrag(BaseComponent component, int x, int y, int button, long time);
}