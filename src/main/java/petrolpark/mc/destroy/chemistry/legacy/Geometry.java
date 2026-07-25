package petrolpark.mc.destroy.chemistry.legacy;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.util.MathsHelper;

/**
 * The VSEPR geometry around an Atom - how its connections are arranged in space.
 * <p>
 * This was nested in Destroy's {@code MoleculeRenderer} before the 1.21.1 port. It describes
 * molecular shape rather than how a Molecule is drawn, and {@link LegacyElement} needs it to
 * declare the geometry of each Element, so it lives with the chemistry engine now. The
 * renderer uses it from here.
 * </p>
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public enum Geometry {

    LINEAR(new Vec3(1d, 0d, 0d)),
    V_SHAPE(new Vec3(0.333333d, -0.942809d, 0d).normalize()),
    TRIGONAL_PLANAR(new Vec3(0.5d, 0.86602540378d, 0d).normalize(), new Vec3(0.5d, -0.86602540378d, 0d).normalize()),
    TRIGONAL_PYRAMIDAL(new Vec3(0.333333d, -0.942809d, 0d).normalize(), new Vec3(0.333333d, 0.471405d, 0.816497d).normalize()),
    TETRAHEDRAL(new Vec3(0.333333d, -0.942809d, 0d).normalize(), new Vec3(0.333333d, 0.471405d, 0.816497d).normalize(), new Vec3(0.333333d, 0.471405d, -0.816497d).normalize()),
    OCTAHEDRAL(new Vec3(1d, 0d, 0d), new Vec3(0d, 1d, 0d), new Vec3(0d, -1d, 0d), new Vec3(0d, 0d, 1d), new Vec3(0d, 0d, -1d));

    /**
     * The default input direction for a Geometry, to which all the output directions are relative.
     */
    public static final Vec3 standardDirection = new Vec3(1d, 0d, 0d);
    public static final Vec3 inverseStandardDirection = new Vec3(-1d, 0d, 0d);

    /**
     * The normalized direction vector of each additional connection out of this Geometry
     * relative to the {@link Geometry#standardDirection standard input direction vector},
     * with the first output vector also in the XY plane.
     */
    public final ImmutableList<Vec3> connections;

    Geometry(Vec3 ...connections) {
        this.connections = ImmutableList.copyOf(connections);
    };

    /**
     * Get the angle in degrees between the connections around this Geometry -
     * specifically, the angle between the input vector (1,0,0) and the XY-coplanar output vector.
     */
    public double getAngle() {
        double angle = MathsHelper.angleBetween(standardDirection, connections.get(0), new Vec3(0d, 0d, 1d));
        return angle < 90d ? 180d - angle : angle; // We always want the obtuse angle
    };

    /**
     * Confine this Geometry to a specified plane, with the zag oriented optimally to continue the chain in the given direction.
     * @param zig The zig ({@link Geometry#connections input direction vector})
     * @param plane The normal to the plane in which this chain is being rendered (the plane in which the zig and the zag should lie)
     * @param direction The overall direction in which the chain should continue, which should be in the plane
     */
    public ConfinedGeometry confine(Vec3 zig, Vec3 plane, Vec3 direction) {

        // Check the direction vector is in the plane
        if (plane.dot(direction) > 0.000001d) throw new IllegalStateException("Chains of Molecules being rendered in a plane must continue in a direction in that plane.");

        // Determine how the zig was transformed from (1,0,0)
        Vec3 rotationVec = zig.cross(standardDirection);
        double angle = MathsHelper.angleBetween(standardDirection, zig, rotationVec);

        // Calculate the adjusted zag vector by applying the same transformation to the XY-coplanar output vector for this geometry
        Vec3 zag = MathsHelper.rotate(connections.get(0), rotationVec, angle);

        // Determine whether the continuation vector flipped by 180 degrees is more faithful to the direction in which this branch should be going
        boolean flip = distanceFromPointToLine(zig.add(MathsHelper.rotate(zag, zig, 180d)), Vec3.ZERO, direction) < distanceFromPointToLine(zig.add(zag), Vec3.ZERO, direction);

        return new ConfinedGeometry(this, rotationVec, angle, flip);
    };

    public List<Vec3> getConnections(boolean includeInput) {
        if (!includeInput) return connections;
        List<Vec3> connectionsAndInput = new ArrayList<>(connections.size() + 1);
        connectionsAndInput.addAll(connections);
        connectionsAndInput.add(inverseStandardDirection);
        return connectionsAndInput;
    };

    /**
     * The shortest (perpendicular) distance from a point to a line.
     * @param point The point to which to find the distance
     * @param linePoint Any point on the line
     * @param lineDirection The direction vector of the line
     */
    public static double distanceFromPointToLine(Vec3 point, Vec3 linePoint, Vec3 lineDirection) {
        return (point.subtract(linePoint)).cross(lineDirection).length() / lineDirection.length();
    };

    /**
     * A {@link Geometry} which has been {@link Geometry#confine confined} to a plane.
     */
    public static class ConfinedGeometry {

        public final Geometry geometry;
        public final Vec3 rotationAxis;
        public final double angle;
        /**
         * Whether to rotate this Geometry 180 degrees around the input connection.
         */
        public final boolean flip;

        private ConfinedGeometry(Geometry geometry, Vec3 rotationAxis, double angle, boolean flip) {
            this.geometry = geometry;
            this.rotationAxis = rotationAxis;
            this.angle = angle;
            this.flip = flip;
        };

        public Vec3 getZig() {
            return MathsHelper.rotate(Geometry.standardDirection, rotationAxis, angle);
        };

        public Vec3 getInverseZig() {
            return MathsHelper.rotate(Geometry.inverseStandardDirection, rotationAxis, angle);
        };

        public Vec3 getZag() {
            return getZag(0);
        };

        public Vec3 getZag(int index) {
            Vec3 unflipped = MathsHelper.rotate(geometry.connections.get(index), rotationAxis, angle);
            if (!flip) {
                return unflipped;
            } else {
                return MathsHelper.rotate(unflipped, MathsHelper.rotate(Geometry.standardDirection, rotationAxis, angle), 180d);
            }
        };
    };

};
