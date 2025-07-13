package me.kall.fluidium.common.kdtree;

import java.util.UUID;

public class KDTree {
    private KDNode root;
    private final int dimensions = 3;
    private int size = 0;

    public void insert(PlayerRegion region) {
        root = insert(root, region, 0);
    }

    private KDNode insert(KDNode node, PlayerRegion region, int depth) {
        if (node == null) {
            size++;
            return new KDNode(region, depth % dimensions);
        }

        int cd = node.discriminant;
        double nodeValue = getCoordinate(node.region.center(), cd);
        double regionValue = getCoordinate(region.center(), cd);

        if (regionValue < nodeValue) {
            node.left = insert(node.left, region, depth + 1);
        } else {
            node.right = insert(node.right, region, depth + 1);
        }

        return node;
    }

    public boolean containsAnyInRange(double x, double y, double z, double range) {
        return containsAnyInRange(root, x, y, z, range, 0);
    }

    private boolean containsAnyInRange(KDNode node, double x, double y, double z, double range, int depth) {
        if (node == null) return false;

        PlayerRegion region = node.region;

        double dx = region.centerX() - x;
        if (Math.abs(dx) > region.radius()) return false;

        double dy = region.centerY() - y;
        if (Math.abs(dy) > region.radius()) return false;

        double dz = region.centerZ() - z;
        if (Math.abs(dz) > region.radius()) return false;

        double distanceSq = dx * dx + dy * dy + dz * dz;

        if (distanceSq <= region.radiusSq()) {
            return true;
        }

        int cd = depth % dimensions;
        double nodeValue = getCoordinate(region.center(), cd);
        double queryValue = getCoordinate(new double[]{x, y, z}, cd);
        double diff = queryValue - nodeValue;

        KDNode first = diff < 0 ? node.left : node.right;
        KDNode second = diff < 0 ? node.right : node.left;

        if (containsAnyInRange(first, x, y, z, range, depth + 1)) {
            return true;
        }

        if (second != null && diff * diff < region.radiusSq()) {
            return containsAnyInRange(second, x, y, z, range, depth + 1);
        }

        return false;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    private double getCoordinate(double[] point, int dimension) {
        return point[dimension];
    }

    public static class KDNode {
        public final PlayerRegion region;
        public final int discriminant;
        public KDNode left;
        public KDNode right;

        public KDNode(PlayerRegion region, int discriminant) {
            this.region = region;
            this.discriminant = discriminant;
        }

    }

    public static class PlayerRegion {
        private final UUID playerId;
        private final double centerX;
        private final double centerY;
        private final double centerZ;
        private final double radius;
        private final double radiusSq;

        public PlayerRegion(UUID playerId, double centerX, double centerY, double centerZ, double radius) {
            this.playerId = playerId;
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.radius = radius;
            this.radiusSq = radius * radius;
        }

        public double[] center() {
            return new double[]{centerX, centerY, centerZ};
        }

        public double centerX() { return centerX; }
        public double centerY() { return centerY; }
        public double centerZ() { return centerZ; }
        public double radius() { return radius; }
        public double radiusSq() { return radiusSq; }
        public UUID playerId() { return playerId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerRegion that = (PlayerRegion) o;
            return playerId.equals(that.playerId);
        }

        @Override
        public int hashCode() {
            return playerId.hashCode();
        }
    }
}
