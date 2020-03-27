import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Jcg.geometry.GeometricOperations_2;
import Jcg.geometry.PointCloud_2;
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
	
	class SortPointsByCoordinates implements Comparator<Integer> {
		GridPoint_2[] points;
		public SortPointsByCoordinates(GridPoint_2[] points) {
			this.points = points;
		}
		
		public int compare(Integer p1, Integer p2) {
			if (this.points[p1].x < this.points[p2].x) {
				return -1;
			}
			if (this.points[p1].x == this.points[p2].x) {
				return this.points[p2].y - this.points[p1].y;
			}
			return 1;
		}
	}

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
    	
    	int[] convexH = this.computeConvexHull();
    	return this.computeArea(convexH);
    }
    
    /**
	 * Compute the convex hull of the input point set
	 * 
	 * @param points
	 * 					a point cloud (points are not sorted)
	 * 
	 * @return the ordered set of points on the convex hull
	 */
     public int[] computeConvexHull() {
    	System.out.print("Performing Andrew algorithm for convex hull...");
    	// trier la region 
    	ArrayList<Integer> sortedPoints= new ArrayList<Integer>();
    	for (int i = 0; i < this.points.length; i++)
    		sortedPoints.add(i);
    	Collections.sort(sortedPoints,new SortPointsByCoordinates(this.points));
    	
    	ArrayList<Integer> upperHull=this.computeUpperHull(sortedPoints);
    	ArrayList<Integer> lowerHull=this.computeLowerHull(sortedPoints);
    	
    	
    	for(int i=lowerHull.size()-2;i>=1;i--) { // LCA to be improved
    		upperHull.add(lowerHull.get(i));
    	}
    	System.out.println("done");
    	int outerN = upperHull.size();
    	int[] convH = new int[outerN];
    	for(int i = 0; i<outerN; i++) {
    		convH[i] = upperHull.get(i);
    	}
    	
    	return convH;
    }
    
    /**
	 * Compute the upper Hull of the point set
	 * 
	 * @param sortedPoints a list of points already sorted (according to a given order)
	 * 
	 * @return the ordered list of points on the upper Hull
	 */
    public ArrayList<Integer> computeUpperHull(ArrayList<Integer> sortedPoints) {
    	ArrayList<Integer> upperHull = new ArrayList<Integer>();
    	for (int i=0; i<sortedPoints.size(); i++) {
    		Integer p=sortedPoints.get(i);
    		int last=upperHull.size();
    		while(upperHull.size()>=2 &&
			  	  !this.isCounterClockwise(upperHull.get(last-1),upperHull.get(last-2),p)
			     ) {
				upperHull.remove(last-1); // remove last point in the upper hull
				last=upperHull.size();
			}
			upperHull.add(upperHull.size(),p); // add at the end
    	}
		return upperHull;
    }
    
    /**
	 * Compute the lower Hull of the point set
	 * 
	 * @param sortedPoints a list of points already sorted (according to a given order)
	 * 
	 * @return the ordered list of points on the lower Hull
	 */
    public ArrayList<Integer> computeLowerHull(ArrayList<Integer> sortedPoints) {
    	ArrayList<Integer> upperHull = new ArrayList<Integer>();
    	for (int i=0; i<sortedPoints.size(); i++) {
    		int p=sortedPoints.get(i);
    		int last=upperHull.size();
    		while(upperHull.size()>=2 && 
    				(this.isCounterClockwise(upperHull.get(last-1),upperHull.get(last-2),p) ||
    				this.liesOn(upperHull.get(last-1),upperHull.get(last-2),p)) // consider the case of 3 collinear points
			     ) {
				upperHull.remove(last-1); // remove last point in the upper hull
				last=upperHull.size();
			}
			upperHull.add(upperHull.size(),p); // add at the end
    	}
		return upperHull;
    }

    /**
     * Return the area of the polygon (given as a permutation of the input points). <br>
     * 
     * @param polygon  an array of size 'n', storing a permutation of the input points (listed in ccw order on the boundary)
     */
    public long computeArea(int[] polygon) {
    	System.out.println("Computing the area of a polygon...");
    	if(polygon==null)
    		return -1;
    	
    	// Formula stolen from http://alienryderflex.com/polygon_area/
    	long area = 0;
    	for(int i = 0; i < polygon.length-1; i++) {
    		area += (this.points[polygon[i]].x + this.points[polygon[i+1]].x) 
    				* (this.points[polygon[i]].y - this.points[polygon[i+1]].y) / 2;
    	}
    	area += (this.points[polygon[polygon.length-1]].x + this.points[polygon[0]].x) 
				* (this.points[polygon[polygon.length-1]].y - this.points[polygon[0]].y) / 2;
    	
    	return area;
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
        long val = (this.points[q].y - this.points[p].y) * (this.points[r].x - this.points[q].x) - 
                (this.points[q].x - this.points[p].x) * (this.points[r].y - this.points[q].y); 
      
        if (val == 0) return 0; // colinear 
      
        return (val > 0)? 1: 2; // clock or counterclock wise 
    }
    
    private boolean isClockwise(int p, int q, int r) {
    	return this.orientation(p, q, r) == 1;
    }
    
    private boolean isCounterClockwise(int p, int q, int r) {
    	return this.orientation(p, q, r) == 2;
    }
    
    /**
     * Checks whether the point p is in the triangle formed by the points a, b and c
     * @param p
     * @param a
     * @param b
     * @param c
     * @return
     */
    public boolean isInTriangle(int p, int a, int b, int c) {
    	// We could avoid computing the orientation twice...
    	boolean clockwiseEverywhere = this.isClockwise(p, a, b) && this.isClockwise(p, b, c) && this.isClockwise(p, c, a);
    	boolean counterclockEverywhere = this.isCounterClockwise(p, a, b) && this.isCounterClockwise(p, b, c) && this.isCounterClockwise(p, c, a);
    	
    	return clockwiseEverywhere && counterclockEverywhere;
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
    
    private boolean liesOn(int p, int q, int r) 
    { 
        if (this.points[q].x <= Math.max(this.points[p].x, this.points[r].x) && this.points[q].x >= Math.min(this.points[p].x, this.points[r].x) && 
        		this.points[q].y <= Math.max(this.points[p].y, this.points[r].y) && this.points[q].y >= Math.min(this.points[p].y, this.points[r].y)) 
        return true; 
      
        return false; 
    } 

}
