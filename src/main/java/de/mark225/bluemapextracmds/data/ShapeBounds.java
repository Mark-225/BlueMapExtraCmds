package de.mark225.bluemapextracmds.data;

import com.flowpowered.math.vector.Vector2d;

import java.util.List;

public class ShapeBounds {

    Vector2d minCorner;
    Vector2d maxCorner;

    public ShapeBounds(List<Vector2d> points){
        if(points.size() <= 0) return;
        Vector2d firstPoint = points.get(0);
        double minX = firstPoint.getX();
        double maxX = firstPoint.getX();
        double minZ = firstPoint.getY();
        double maxZ = firstPoint.getY();

        for(Vector2d point : points){
            if(point.getX() < minX)
                minX = point.getX();
            if(point.getX() > maxX)
                maxX = point.getX();
            if(point.getY() < minZ)
                minZ = point.getY();
            if(point.getY() > maxZ)
                maxZ = point.getY();
        }
        minCorner = new Vector2d(minX, minZ);
        maxCorner = new Vector2d(maxX, maxZ);
    }

    public Vector2d getMinCorner() {
        return minCorner;
    }

    public Vector2d getMaxCorner() {
        return maxCorner;
    }

    public Vector2d getCenterPoint(){
        return minCorner.add(maxCorner.sub(minCorner).div(2));
    }
}
