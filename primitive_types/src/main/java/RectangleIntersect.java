import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Write a program to determine if 2 rectangles intersect. If they intersect, return the intersection.
 */

public class RectangleIntersect {
    public static Rectangle intersects(Rectangle rect1, Rectangle rect2) {
        boolean xIntersects = rect1.x <= rect2.x && rect1.x + rect1.width >= rect2.x;
        boolean yIntersects = rect1.y <= rect2.y && rect1.y + rect1.height >= rect2.y;

        if (xIntersects && yIntersects) {
            return new Rectangle(Math.max(rect1.x, rect2.x), Math.max(rect1.y, rect2.y),
                    Math.min(rect1.x + rect1.width, rect2.x + rect2.width) - Math.max(rect1.x, rect2.x),
                    Math.min(rect1.y + rect1.height, rect2.y + rect2.height) - Math.max(rect1.y, rect2.y));
        }

        return null;

    }

    public static class Rectangle {
        public int x;
        public int y;
        public int width;
        public int height;

        public Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rectangle rectangle = (Rectangle) o;
            return x == rectangle.x &&
                    y == rectangle.y &&
                    width == rectangle.width &&
                    height == rectangle.height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, width, height);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("x", x)
                    .add("y", y)
                    .add("width", width)
                    .add("height", height)
                    .toString();
        }
    }
}
