package unnamed.geometry;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import unnamed.utils.AabbUtils;

import org.objectweb.asm.*;

import com.google.common.base.Preconditions;

public abstract class BlockSpaceTransform {

    private static class BytecodeClassLoader extends ClassLoader {
        private BytecodeClassLoader() {
            super (BytecodeClassLoader.class.getClassLoader());
        }

        public Class<?> define(byte[] data) {
            return defineClass(null, data, 0, data.length);
        }
    }

    private static Matrix3d createTranformMatrix(Orientation o) {
        // basis change matrix - local east (X), up (Y), south (Z) are new basis vectors
        final Matrix3d mat = new Matrix3d();
        mat.m00 = o.x.x;
        mat.m01 = o.x.y;
        mat.m02 = o.x.z;

        mat.m10 = o.y.x;
        mat.m11 = o.y.y;
        mat.m12 = o.y.z;

        mat.m20 = o.z.x;
        mat.m21 = o.z.y;
        mat.m22 = o.z.z;
        return mat;
    }

    private static void createVarAccess(MethodVisitor mv, double value, int var) {
        if (value == 1) {
            mv.visitVarInsn(Opcodes.DLOAD, var);
        } else if (value == -1) {
            mv.visitInsn(Opcodes.DCONST_1);
            mv.visitVarInsn(Opcodes.DLOAD, var);
            mv.visitInsn(Opcodes.DSUB);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // there should be exactly one non-zero value in row
    private static void createGetLine(MethodVisitor mv, double x, double y, double z) {
        if (x != 0) {
            Preconditions.checkArgument(y == 0 && z == 0);
            createVarAccess(mv, x, 2);
        } else if (y != 0) {
            Preconditions.checkArgument(x == 0 && z == 0);
            createVarAccess(mv, y, 4);
        } else if (z != 0) {
            Preconditions.checkArgument(x == 0 && y == 0);
            createVarAccess(mv, z, 6);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void createTransformMethod(MethodVisitor mv, boolean invert) {
        // 0 - this (unused)
        // 1 - orientation
        // 2,3 - x
        // 4,5 - y
        // 6,7 - z
        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 1);

        final String enumType = Type.getInternalName(Enum.class);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, enumType, "ordinal", Type.getMethodDescriptor(Type.INT_TYPE), false);

        final Orientation[] orientations = Orientation.values();

        final Label defaultLabel = new Label();
        final Label[] targets = new Label[orientations.length];
        for (int i = 0; i < orientations.length; ++i)
            targets[i] = new Label();

        mv.visitTableSwitchInsn(0, orientations.length - 1, defaultLabel, targets);

        {
            mv.visitLabel(defaultLabel);
            final String excType = Type.getInternalName(IllegalArgumentException.class);
            final Type stringType = Type.getType(String.class);

            mv.visitTypeInsn(Opcodes.NEW, excType);
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Object.class), "toString", Type.getMethodDescriptor(stringType), false);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, excType, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, stringType), false);
            mv.visitInsn(Opcodes.ATHROW);
        }

        {
            final String vecType = Type.getInternalName(Vec3.class);
            for (int i = 0; i < orientations.length; ++i) {
                mv.visitLabel(targets[i]);
                final Orientation orientation = orientations[i];
                final Matrix3d mat = createTranformMatrix(orientation);

                if (invert) mat.invertInplace();

                createGetLine(mv, mat.m00, mat.m10, mat.m20);
                createGetLine(mv, mat.m01, mat.m11, mat.m21);
                createGetLine(mv, mat.m02, mat.m12, mat.m22);

                mv.visitMethodInsn(Opcodes.INVOKESTATIC, vecType, "createVectorHelper", Type.getMethodDescriptor(Type.getObjectType(vecType), Type.DOUBLE_TYPE, Type.DOUBLE_TYPE, Type.DOUBLE_TYPE), false);
                mv.visitInsn(Opcodes.ARETURN);
            }
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static Class<? extends BlockSpaceTransform> createTransformClass() {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        final String parentCls = Type.getInternalName(BlockSpaceTransform.class);

        final String name = parentCls + "$GeneratedImplementation$";

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_SYNTHETIC, name, null, parentCls, new String[] {});

        cw.visitSource(".dynamic", null);

        {
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, parentCls, "<init>", "()V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        final String transformMethod = Type.getMethodDescriptor(Type.getType(Vec3.class), Type.getType(Orientation.class), Type.DOUBLE_TYPE, Type.DOUBLE_TYPE, Type.DOUBLE_TYPE);

        {
            final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "mapWorldToBlock", transformMethod, null, null);
            createTransformMethod(mv, true);
        }

        {
            final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "mapBlockToWorld", transformMethod, null, null);
            createTransformMethod(mv, false);
        }

        cw.visitEnd();

        final byte[] clsBytes = cw.toByteArray();
        final BytecodeClassLoader loader = new BytecodeClassLoader();
        @SuppressWarnings("unchecked")
        final Class<? extends BlockSpaceTransform> cls = (Class<? extends BlockSpaceTransform>) loader.define(clsBytes);
        return cls;
    }

    private static BlockSpaceTransform createTransformInstance() {
        try {
            final Class<? extends BlockSpaceTransform> transformClass = createTransformClass();
            return transformClass.newInstance();
        } catch (Throwable t) {
            throw new Error("Failed to create block space transformer", t);
        }
    }

    public static final BlockSpaceTransform instance = createTransformInstance();

    public abstract Vec3 mapWorldToBlock(Orientation orientation, double x, double y, double z);

    public abstract Vec3 mapBlockToWorld(Orientation orientation, double x, double y, double z);

    public AxisAlignedBB mapWorldToBlock(Orientation orientation, AxisAlignedBB aabb) {
        final Vec3 min = mapWorldToBlock(orientation, aabb.minX, aabb.minY, aabb.minZ);
        final Vec3 max = mapWorldToBlock(orientation, aabb.maxX, aabb.maxY, aabb.maxZ);
        return AabbUtils.createAabb(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);
    }

    public AxisAlignedBB mapBlockToWorld(Orientation orientation, AxisAlignedBB aabb) {
        final Vec3 min = mapBlockToWorld(orientation, aabb.minX, aabb.minY, aabb.minZ);
        final Vec3 max = mapBlockToWorld(orientation, aabb.maxX, aabb.maxY, aabb.maxZ);
        return AabbUtils.createAabb(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);
    }
}