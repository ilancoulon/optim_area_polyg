import java.util.*;

import Jcg.geometry.*;
import Jcg.geometry.kernel.*;
import Jcg.convexhull2d.*;

/**
 * @author Luca Castelli Aleardi, Ecole Polytechnique (INF562)
 * 
 * Implementation of Andrew's algorithm for the computation of 2d convex hulls
 */
public class ConvexHull implements ConvexHull_2 {
	
	/** Predicates used to perform geometric computations and tests (approximate, exact or filtered computations)*/
	GeometricPredicates_2 predicates;
	
	/**
	 * Set the choice of geometric predicates
	 */
	public ConvexHull() {
		//this.predicates=new FilteredPredicates_2();
		//this.predicates=new ApproximatePredicates_2(); // double precision
		this.predicates=new ExactPredicates_2();
	}
	
	class SortPointsByCoordinates implements Comparator<Point_2> {
		
		public int compare(Point_2 p1, Point_2 p2) {
			return p1.compareTo(p2);
		}
	}
	
	/**
	 * Compute the upper Hull of the point set
	 * 
	 * @param sortedPoints a list of points already sorted (according to a given order)
	 * 
	 * @return the ordered list of points on the upper Hull
	 */
    public ArrayList<Point_2> computeUpperHull(ArrayList<Point_2> sortedPoints) {
    	ArrayList<Point_2> upperHull = new ArrayList<Point_2>();
    	for (int i=0; i<sortedPoints.size(); i++) {
    		Point_2 p=sortedPoints.get(i);
    		int last=upperHull.size();
    		while(upperHull.size()>=2 &&
			  	  !predicates.isCounterClockwise(upperHull.get(last-1),upperHull.get(last-2),p)
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
    public ArrayList<Point_2> computeLowerHull(ArrayList<Point_2> sortedPoints) {
    	ArrayList<Point_2> upperHull = new ArrayList<Point_2>();
    	for (int i=0; i<sortedPoints.size(); i++) {
    		Point_2 p=sortedPoints.get(i);
    		int last=upperHull.size();
    		while(upperHull.size()>=2 && 
    				(predicates.isCounterClockwise(upperHull.get(last-1),upperHull.get(last-2),p) ||
    				predicates.liesOn(upperHull.get(last-1),upperHull.get(last-2),p)) // consider the case of 3 collinear points
			     ) {
				upperHull.remove(last-1); // remove last point in the upper hull
				last=upperHull.size();
			}
			upperHull.add(upperHull.size(),p); // add at the end
    	}
		return upperHull;
    }
    
	/**
	 * Compute the convex hull of the input point set
	 * 
	 * @param points
	 * 					a point cloud (points are not sorted)
	 * 
	 * @return the ordered set of points on the convex hull
	 */
     public PointCloud_2 computeConvexHull(PointCloud_2 points) {
    	System.out.print("Performing Andrew algorithm for convex hull...");
    	// trier la region 
    	ArrayList<Point_2> sortedPoints=(ArrayList<Point_2>)points.listOfPoints();	
    	Collections.sort(sortedPoints,new SortPointsByCoordinates());
    	
    	ArrayList<Point_2> upperHull=computeUpperHull(sortedPoints);
    	ArrayList<Point_2> lowerHull=computeLowerHull(sortedPoints);
    	
    	for(int i=lowerHull.size()-1;i>=0;i--) { // LCA to be improved
    		upperHull.add(lowerHull.get(i));
    	}
    	System.out.println("done");
    	return new PointCloud_2(upperHull);
    }

}