package unnamed.gui.listener;

import unnamed.gui.component.BaseComponent;

public interface IKeyTypedListener extends IListenerBase {
    void componentKeyTyped(BaseComponent component, char character, int keyCode);
}