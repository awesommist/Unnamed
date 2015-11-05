package unnamed.geometry;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.base.Preconditions;

public enum Orientation {
    XN_YN(HalfAxis.NEG_X, HalfAxis.NEG_Y),
    XN_YP(HalfAxis.NEG_X, HalfAxis.POS_Y),
    XN_ZN(HalfAxis.NEG_X, HalfAxis.NEG_Z),
    XN_ZP(HalfAxis.NEG_X, HalfAxis.POS_Z),

    XP_YN(HalfAxis.POS_X, HalfAxis.NEG_Y),
    XP_YP(HalfAxis.POS_X, HalfAxis.POS_Y),
    XP_ZN(HalfAxis.POS_X, HalfAxis.NEG_Z),
    XP_ZP(HalfAxis.POS_X, HalfAxis.POS_Z),

    YN_ZP(HalfAxis.NEG_Y, HalfAxis.POS_Z),
    YN_XN(HalfAxis.NEG_Y, HalfAxis.NEG_X),
    YN_XP(HalfAxis.NEG_Y, HalfAxis.POS_X),
    YN_ZN(HalfAxis.NEG_Y, HalfAxis.NEG_Z),

    YP_XN(HalfAxis.POS_Y, HalfAxis.NEG_X),
    YP_XP(HalfAxis.POS_Y, HalfAxis.POS_X),
    YP_ZN(HalfAxis.POS_Y, HalfAxis.NEG_Z),
    YP_ZP(HalfAxis.POS_Y, HalfAxis.POS_Z),

    ZN_XP(HalfAxis.NEG_Z, HalfAxis.POS_X),
    ZN_XN(HalfAxis.NEG_Z, HalfAxis.NEG_X),
    ZN_YP(HalfAxis.NEG_Z, HalfAxis.POS_Y),
    ZN_YN(HalfAxis.NEG_Z, HalfAxis.NEG_Y),

    ZP_XN(HalfAxis.POS_Z, HalfAxis.NEG_X),
    ZP_XP(HalfAxis.POS_Z, HalfAxis.POS_X),
    ZP_YN(HalfAxis.POS_Z, HalfAxis.NEG_Y),
    ZP_YP(HalfAxis.POS_Z, HalfAxis.POS_Y);

    public static final Orientation[] VALUES = values();

    private static final TIntObjectMap<Orientation> LOOKUP_XY = new TIntObjectHashMap<Orientation>(VALUES.length);
    private static final TIntObjectMap<Orientation> LOOKUP_XZ = new TIntObjectHashMap<Orientation>(VALUES.length);
    private static final TIntObjectMap<Orientation> LOOKUP_YZ = new TIntObjectHashMap<Orientation>(VALUES.length);

    private static final Orientation[][] ROTATIONS = new Orientation[VALUES.length][HalfAxis.VALUES.length];

    private static int lookupKey(HalfAxis a, HalfAxis b) {
        return (a.ordinal() << 3) | (b.ordinal());
    }

    private static void addToLookup(TIntObjectMap<Orientation> lookup, Orientation o, HalfAxis a, HalfAxis b) {
        final int key = lookupKey(a, b);
        final Orientation prev = lookup.put(key, o);
        Preconditions.checkState(prev == null, "Key %s duplicate: %s->%s", key, prev, o);
    }

    static {
        for (Orientation o : VALUES) {
            addToLookup(LOOKUP_XY, o, o.x, o.y);
            addToLookup(LOOKUP_YZ, o, o.y, o.z);
            addToLookup(LOOKUP_XZ, o, o.x, o.z);
        }

        for (Orientation o : VALUES) {
            final int i = o.ordinal();
            ROTATIONS[i][HalfAxis.POS_X.ordinal()] = lookupXYNotNull(o.x, o.z.negate()/* , o.y */);
            ROTATIONS[i][HalfAxis.NEG_X.ordinal()] = lookupXYNotNull(o.x, o.z/* , o.y.negate() */);

            ROTATIONS[i][HalfAxis.POS_Y.ordinal()] = lookupXYNotNull(o.z, o.y/* , o.x.negate() */);
            ROTATIONS[i][HalfAxis.NEG_Y.ordinal()] = lookupXYNotNull(o.z.negate(), o.y/* , o.x */);

            ROTATIONS[i][HalfAxis.POS_Z.ordinal()] = lookupXYNotNull(o.y.negate(), o.x/* , o.z */);
            ROTATIONS[i][HalfAxis.NEG_Z.ordinal()] = lookupXYNotNull(o.y, o.x.negate()/* , o.z */);
        }
    }

    public static Orientation lookupXY(HalfAxis x, HalfAxis y) {
        final int key = lookupKey(x, y);
        return LOOKUP_XY.get(key);
    }

    public static Orientation lookupXZ(HalfAxis x, HalfAxis z) {
        final int key = lookupKey(x, z);
        return LOOKUP_XZ.get(key);
    }

    public static Orientation lookupYZ(HalfAxis y, HalfAxis z) {
        final int key = lookupKey(y, z);
        return LOOKUP_YZ.get(key);
    }

    private static Orientation lookupXYNotNull(HalfAxis x, HalfAxis y) {
        Orientation v = lookupXY(x, y);
        if (v == null) throw new NullPointerException(x + ":" + y);
        return v;
    }

    public static Orientation rotateAround(Orientation orientation, HalfAxis axis) {
        return ROTATIONS[orientation.ordinal()][axis.ordinal()];
    }

    public Orientation rotateAround(HalfAxis axis) {
        return rotateAround(this, axis);
    }

    public final HalfAxis x; // +X, east

    public final HalfAxis y; // +Y, top

    public final HalfAxis z; // +Z, south

    private final ForgeDirection[] localToGlobalDirections = new ForgeDirection[ForgeDirection.values().length];
    private final ForgeDirection[] globalToLocalDirections = new ForgeDirection[ForgeDirection.values().length];

    private void addDirectionMapping(ForgeDirection local, ForgeDirection global) {
        localToGlobalDirections[local.ordinal()] = global;
        globalToLocalDirections[global.ordinal()] = local;
    }

    private void addDirectionMappings(ForgeDirection local, ForgeDirection global) {
        addDirectionMapping(local, global);
        addDirectionMapping(local.getOpposite(), global.getOpposite());
    }

    Orientation(HalfAxis x, HalfAxis y) {
        this.x = x;
        this.y = y;
        this.z = x.cross(y);

        addDirectionMappings(ForgeDirection.EAST, x.dir);
        addDirectionMappings(ForgeDirection.UP, y.dir);
        addDirectionMappings(ForgeDirection.SOUTH, z.dir);
        addDirectionMapping(ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN);
    }

    public ForgeDirection localToGlobalDirection(ForgeDirection local) {
        return localToGlobalDirections[local.ordinal()];
    }

    public ForgeDirection globalToLocalDirection(ForgeDirection global) {
        return globalToLocalDirections[global.ordinal()];
    }

    public ForgeDirection north() {
        return localToGlobalDirection(ForgeDirection.NORTH);
    }

    public ForgeDirection south() {
        return localToGlobalDirection(ForgeDirection.SOUTH);
    }

    public ForgeDirection east() {
        return localToGlobalDirection(ForgeDirection.EAST);
    }

    public ForgeDirection west() {
        return localToGlobalDirection(ForgeDirection.WEST);
    }

    public ForgeDirection up() {
        return localToGlobalDirection(ForgeDirection.UP);
    }

    public ForgeDirection down() {
        return localToGlobalDirection(ForgeDirection.DOWN);
    }

    public double transformX(double x, double y, double z) {
        return this.x.x * x + this.y.x * y + this.z.x * z;
    }

    public int transformX(int x, int y, int z) {
        return this.x.x * x + this.y.x * y + this.z.x * z;
    }

    public double transformY(double x, double y, double z) {
        return this.x.y * x + this.y.y * y + this.z.y * z;
    }

    public int transformY(int x, int y, int z) {
        return this.x.y * x + this.y.y * y + this.z.y * z;
    }

    public double transformZ(double x, double y, double z) {
        return this.x.z * x + this.y.z * y + this.z.z * z;
    }

    public int transformZ(int x, int y, int z) {
        return this.x.z * x + this.y.z * y + this.z.z * z;
    }
}