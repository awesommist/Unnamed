package unnamed.utils.coord;

import unnamed.api.ICopyable;

public class DimCoord implements ICopyable<DimCoord> {

    public final int dim;
    public final int x;
    public final int y;
    public final int z;

    public DimCoord(int dim, int x, int y, int z) {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public DimCoord copy() {
        return new DimCoord(dim, x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DimCoord &&
                ((DimCoord) obj).dim == dim &&
                ((DimCoord) obj).x == x &&
                ((DimCoord) obj).y == y &&
                ((DimCoord) obj).z == z;
    }

    @Override
    public String toString() {
        return dim + "@(" + x + ", " + y + ", " + z + ")";
    }
}