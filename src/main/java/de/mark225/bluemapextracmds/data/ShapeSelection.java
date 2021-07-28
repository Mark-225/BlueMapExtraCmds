package de.mark225.bluemapextracmds.data;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import de.bluecolored.bluemap.api.marker.Shape;

import java.util.*;
import java.util.stream.Collectors;

public class ShapeSelection {
    private float minY;
    private float maxY;
    private List<Vector2d> points;

    public ShapeSelection(float minY, float maxY, List<Vector2d> points) {
        this.minY = minY;
        this.maxY = maxY;
        this.points = points;
    }

    public ShapeSelection(){
        minY = 0;
        maxY = 0;
        points = new ArrayList<>();
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public List<Vector2d> getPoints() {
        return points;
    }

    public void setPoints(List<Vector2d> points) {
        this.points = points;
    }

    public Shape toBluemapShape(){
        return new Shape(points.toArray(new Vector2d[0]));
    }

    public boolean isValidPolygon(){
        return points.size() >= 3;
    }

    public boolean isValidLine(){
        return points.size() >= 2;
    }

    public ShapeBounds getBounds(){
        return new ShapeBounds(points);
    }

    public String toString(){
        return points.stream().map(v -> v.getX() + ":" + v.getY()).collect(Collectors.joining(" ; "));
    }

}
