package unnamed.gui.listener;

import unnamed.gui.component.BaseComponent;

public interface IMouseDownListener extends IListenerBase {
    void componentMouseDown(BaseComponent component, int x, int y, int button);
}