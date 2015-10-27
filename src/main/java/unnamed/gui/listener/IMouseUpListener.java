package unnamed.gui.listener;

import unnamed.gui.component.BaseComponent;

public interface IMouseUpListener extends IListenerBase {
    void componentMouseUp(BaseComponent component, int x, int y, int button);
}