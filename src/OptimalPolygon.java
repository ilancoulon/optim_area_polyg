import java.math.BigInteger;
import java.util.HashMap;

import Jcg.geometry.GeometricOperations_2;
import Jcg.geometry.Point_;
import Jcg.geometry.Point_2;
import Jcg.geometry.Segment_2;

/**
 * Main class providing tools for computing a polygon with minimal (maximal) area, whose vertices
 * are given as input point cloud (there are no interior points). <br>
 * 
 * @author Luca Castelli Aleardi (2020)
 *
 */
public class OptimalPolygon {
	
	/** Input point cloud: the set of vertices of the final polygon: an array of size 'n' */
	GridPoint_2[] points;

    /**
     * Initialize the input of the program
     */
    public OptimalPolygon(GridPoint_2[] points) {
    	this.points=points;
    }
    
    /**
     * Return the area of the convex hull of the input points. <br>
     * 
     * Remark: this method will be used to compute the SCORE.
     */
    public long computeAreaConvexHull() {
    	// COMPLETE THIS METHOD
    	System.out.println("Area convex hull: to be completed");
    	
    	return -1;
    }

    /**
     * Return the area of the polygon (given as a permutation of the input points). <br>
     * 
     * @param polygon  an array of size 'n', storing a permutation of the input points (listed in ccw order on the boundary)
     */
    public long computeArea(int[] polygon) {
    	System.out.println("Computing the area of a polygon: TO BE COMPLETED");
    	if(polygon==null)
    		return -1;
    	
    	// COMPLETE THIS METHOD
    	
    	return -1;
    }

    /**
     * Check whether the input polygon is valid <br>
     * -) the polygon should be given as a permutation of the integers [0.. n-1] <br>
     * -) the polygon should be simple: non consecutive edges should have empty intersection <br>
     */
    public boolean checkValidity(int[] polygon) {
    	int n = this.points.length;
    	if(polygon==null || polygon.length != n)
    		return false;
    	
    	// Check permutation
    	HashMap<Integer, Boolean> found = new HashMap<Integer, Boolean>(); 
    	for (int i = 0; i < n; i++) {
    		found.put(i, false);
    	}
    	for (int i = 0; i < polygon.length; i++) {
    		if (!found.containsKey(polygon[i])){
    			return false;
    		}
    		if (found.get(polygon[i])) {
    			return false;
    		}
    		found.put(polygon[i], true);
    	}
    	
    	
    	// Check self-intersection (really naive, should implement Bentley-Ottmann algorithm)
    	for (int i = 0; i < n - 1; i++) {
    		for (int j = i+2; j < n - 1; j++) {
    			if (this.doIntersect(polygon[i], polygon[i+1], polygon[j], polygon[j+1])) {
    				return false;
    			}
    		}
    	}
		for (int j = 1; j < n - 2; j++) {
			if (this.doIntersect(polygon[n-1], polygon[0], polygon[j], polygon[j+1])) {
				return false;
			}
		}
    	
    	
    	return true;
    }

    /**
     * Main function that computes a simple polygon of minimal area (whose vertices are exactly the input points).<br>
     * 
     * @return  an array of size 'n' storing the computed polygon as a permutation of point indices
     */
    public int[] computeMinimalAreaPolygon() {
    	System.out.print("Computing a simple polygon of minimal area: ");
    	long startTime=System.nanoTime(), endTime; // for evaluating time performances
    	
    	// COMPLETE THIS METHOD
    	System.out.println("TO BE COMPLETED");
    	
    	endTime=System.nanoTime();
        double duration=(double)(endTime-startTime)/1000000000.;
    	System.out.println("Elapsed time: "+duration+" seconds");
    	
    	return null; // remove this line
    }

    /**
     * Main function that computes a simple polygon of maximal area (whose vertices are exactly the input points).<br>
     * 
     * @return  an array of size 'n' storing the computed polygon as a permutation of point indices
     */
    public int[] computeMaximalAreaPolygon() {
    	System.out.print("Computing a simple polygon of maximal area: ");
    	long startTime=System.nanoTime(), endTime; // for evaluating time performances
    	
    	// COMPLETE THIS METHOD
    	System.out.println("TO BE COMPLETED");
    	
    	endTime=System.nanoTime();
        double duration=(double)(endTime-startTime)/1000000000.;
    	System.out.println("Elapsed time: "+duration+" seconds");
    	
    	return null; // remove this line
    }
    
    // Stolen from https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    private int orientation(int p, int q, int r) 
    { 
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/ 
        // for details of below formula. 
        int val = (this.points[q].y - this.points[p].y) * (this.points[r].x - this.points[q].x) - 
                (this.points[q].x - this.points[p].x) * (this.points[r].y - this.points[q].y); 
      
        if (val == 0) return 0; // colinear 
      
        return (val > 0)? 1: 2; // clock or counterclock wise 
    }
    
    // Stolen from https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    private boolean doIntersect(int p1, int q1, int p2, int q2) 
    { 
        // Find the four orientations needed for general and 
        // special cases 
        int o1 = orientation(p1, q1, p2); 
        int o2 = orientation(p1, q1, q2); 
        int o3 = orientation(p2, q2, p1); 
        int o4 = orientation(p2, q2, q1); 
      
        // General case 
        if (o1 != o2 && o3 != o4) 
            return true; 
      
        return false; // Doesn't fall in any of the above cases 
    } 

}
