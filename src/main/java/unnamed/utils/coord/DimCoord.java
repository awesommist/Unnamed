package unnamed.utils.coord;

import unnamed.api.ICopyable;

public class DimCoord implements ICopyable<DimCoord> {

    public final int d;
    public final int x;
    public final int y;
    public final int z;

    public DimCoord(int d, int x, int y, int z) {
        this.d = d;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public DimCoord copy() {
        return new DimCoord(d, x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DimCoord &&
                ((DimCoord) obj).d == d &&
                ((DimCoord) obj).x == x &&
                ((DimCoord) obj).y == y &&
                ((DimCoord) obj).z == z;
    }

    @Override
    public String toString() {
        return d + "@(" + x + ", " + y + ", " + z + ")";
    }
}