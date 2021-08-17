package de.mark225.bluemapextracmds.util;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import de.mark225.bluemapextracmds.data.ShapeSelection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {

    /**
     * Checks if an integer point is inside an integer polygon using the non-zero winding rule
     * @param location the point to check
     * @param polygon the polygon to check against
     * @return true if the point is inside the polygon, otherwise false
     */
    public static boolean isInsidePoly(Vector2i location, List<Vector2i> polygon, Vector2i minCorner, Vector2i maxCorner){
        if(location.getX() < minCorner.getX() || location.getY() < minCorner.getY() || location.getX() > maxCorner.getX() || location.getY() > maxCorner.getY())
            return false;
        Vector2d pTL = location.toDouble();
        Vector2d pTR = pTL.add(1, 0);
        Vector2d pBR = pTL.add(1, 1);
        Vector2d pBL = pTL.add(0, 1);
        Vector2d pT = pTL.add(0.6, 0);
        Vector2d pB = pTL.add(0.4, 1);
        Vector2d pR = pTL.add(1, 0.6);
        Vector2d pL = pTL.add(0, 0.4);

        Map<Vector2d, Boolean> onLine = new HashMap<>();
        onLine.put(pTL, false);
        onLine.put(pTR, false);
        onLine.put(pBR, false);
        onLine.put(pBL, false);
        onLine.put(pT, false);
        onLine.put(pB, false);
        onLine.put(pR, false);
        onLine.put(pL, false);

        Map<Vector2d, Integer> winds = new HashMap<>();
        winds.put(pTL, 0);
        winds.put(pTR, 0);
        winds.put(pBR, 0);
        winds.put(pBL, 0);
        winds.put(pT, 0);
        winds.put(pB, 0);
        winds.put(pR, 0);
        winds.put(pL, 0);


        for(int index = 0; index < polygon.size(); index++){
            Vector2d q = polygon.get(index).toDouble().add(0.5, 0.5);
            Vector2d qEnd = polygon.get((index+1) % polygon.size()).toDouble().add(0.5, 0.5);
            //skip if equal
            if(q.equals(qEnd)) continue;
            //skip if parallel
            if(q.getY() == qEnd.getY()) continue;

            winds.replaceAll((p, wCount) ->{
                if(onLine.get(p))
                    return 1;
                int c = checkAgainstVector(p, q, qEnd);
                if(c == -2){
                    onLine.put(p, true);
                    return 1;
                }
                return wCount + c;
            });
        }

        return winds.entrySet().stream().anyMatch(entry -> entry.getValue() != 0 && !onLine.get(entry.getKey()));
    }


    private static int checkAgainstVector(Vector2d p, Vector2d q, Vector2d qEnd){
        //Some optimizations before calculating intersection point:
        //skip if completely left of p
        if(q.getX() <= p.getX() && qEnd.getX() <= p.getX()) return 0;
        //skip if completely above or below p
        if((q.getY() > p.getY() && qEnd.getY() > p.getY()) || (q.getY() < p.getY() && qEnd.getY() < p.getY())) return 0;


        double heightDiff = qEnd.getY() - q.getY();
        double heightDiffToP = p.getY() - q.getY();

        double heightRelation = heightDiffToP / heightDiff;

        Vector2d diffVector = qEnd.sub(q).toDouble().mul(heightRelation);
        Vector2d atHeightP = q.toDouble().add(diffVector);
        if(atHeightP.getX() < p.getX()) return 0;
        if(atHeightP.getX() == p.getX()) return -2;


        if(q.getY() > qEnd.getY()){
            return 1;
        }else{
            return -1;
        }
    }

    /**
     * Returns the cross product of two 2d integer vectors
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the cross product defined as v1.x * v2.y - v1.y * v2.x
     */
    public static int crossProduct(Vector2i v1, Vector2i v2){
        return v1.getX() * v2.getY() - v1.getY() * v2.getX();
    }


    public static ShapeSelection blockifyPoly(List<Vector2i> poly, Vector2i minCorner, Vector2i maxCorner, float minY, float maxY){
        Vector2i startVector = null;

        findStart: for(int y = minCorner.getY(); y <= maxCorner.getY(); y++){
            for(int x = minCorner.getX(); x <= maxCorner.getX(); x++){
                Vector2i v = new Vector2i(x, y);
                if(isInsidePoly(v, poly, minCorner, maxCorner)){
                    startVector = v;
                    break findStart;
                }
            }
        }

        List<Vector2d> newPoly = new ArrayList<>();
        newPoly.add(startVector.toDouble());
        Vector2i currentVector = startVector;
        int maxX = maxCorner.getX();
        int direction = 1;
        while(!currentVector.equals(startVector) || direction != 0){
            Vector2i vCorner, vStay;
            Vector2d vAddInner, vAddOuter;
            switch(direction){
                case 0: //up
                    vCorner = currentVector.sub(0, 1);
                    vAddInner = currentVector.add(1, 0).toDouble();
                    vAddOuter = currentVector.toDouble();
                    vStay = currentVector.sub(1, 0);
                    break;
                case 1: //left
                    vCorner = currentVector.sub(1, 0);
                    vAddInner = currentVector.toDouble();
                    vAddOuter = currentVector.add(0,1).toDouble();
                    vStay = currentVector.add(0, 1);
                    break;
                case 2: //down
                    vCorner = currentVector.add(0, 1);
                    vAddInner = vCorner.toDouble();
                    vAddOuter = currentVector.add(1,1).toDouble();
                    vStay = currentVector.add(1, 0);
                    break;
                case 3: //right
                    vCorner = currentVector.add(1, 0);
                    vAddInner = currentVector.add(1,1).toDouble();
                    vAddOuter = currentVector.add(1, 0).toDouble();
                    vStay = currentVector.sub(0, 1);
                    break;
                default: //literally impossible
                    return null;
            }

            if(isInsidePoly(vCorner, poly, minCorner, maxCorner)){
                newPoly.add(vAddInner);
                currentVector = vCorner;
                direction--;
            }else if(isInsidePoly(vStay, poly, minCorner, maxCorner)){
                currentVector = vStay;
            }else {
                newPoly.add(vAddOuter);
                direction++;
            }
            direction = Math.floorMod(direction, 4);

        }

        return new ShapeSelection(minY, maxY, new CopyOnWriteArrayList<>(newPoly));
    }

}
