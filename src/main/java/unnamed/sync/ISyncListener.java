package unnamed.sync;

import java.util.Set;

public interface ISyncListener {
    void onSync(Set<ISyncableObject> changes);
}