import tc.TC;

/**
 * Provides methods for dealing with 2D input points
 */		   
public class PointCloud_IO {

    /**
     * Load a 2D point cloud from file
     * Remarks: 
     * 	-) point indices range from 0..n-1
     *  -) point coordinates are assumed to be integers
     * 
     */		   
    public static GridPoint_2[] read(String filename) { 
    	System.out.println("Reading (integer) 2D points from file: "+filename);
		int n=-1; // input size
		int area=-1;
		GridPoint_2[] result; // input points
		
		TC.lectureDansFichier(filename);
		String line=TC.lireLigne(); // read first line
		String[] parse=TC.motsDeChaine(line); // parse the first line
		boolean ends=false;
		int i=0;
		while(i<parse.length && ends==false) {
			String s=parse[i];
			if(s.contains("points")) {
				ends=true;
				n=Integer.parseInt(parse[i-1].replace("(", ""));
			}
			else
				i++;
		}
		if(n==-1)
			throw new Error("Error: wrong input file");
		else
			result=new GridPoint_2[n];
		
		line=TC.lireLigne(); // read second line
		
		// start loading input points
		System.out.print("Reading "+n+" input points...");
		for(i=0;i<n;i++) {
			line=TC.lireLigne();
			parse=TC.motsDeChaine(line);
			int count=Integer.parseInt(parse[0]);
			int x=Integer.parseInt(parse[1]);
			int y=Integer.parseInt(parse[2]);
			if(i!=count)
				throw new Error("Error: wrong point index "+count);
			
			result[i]=new GridPoint_2(x, y);
		}
		System.out.println("done");
		
		TC.lectureEntreeStandard(); // end of file
		
    	return result;
    }

    /**
     * Return the bounding box containing the points
     * 
     */		   
    public static int[] getBoundingBox(GridPoint_2[] points) {
    	int xmax=0, ymax=0;
    	
    	for(GridPoint_2 p: points) {
    		if(p.getX()<0 || p.getY()<0)
    			throw new Error("Error: wrong bounding box");
    		
    		xmax=Math.max(xmax, p.getX());
    		ymax=Math.max(ymax, p.getY());
    	}
    	
    	System.out.print("Bounding box: [0, "+xmax+"]x[0, "+ymax+"]");
    	System.out.println(" - area of the bounding box: "+((long)xmax*(long)ymax)); // computations should be done on 64 bits
    	return new int[] {xmax, ymax};
    }

    /**
     * Write a polygon to file. <br>
     * The polygon is stored as a permutation of its point indices (listed in ccw order on its boundary)
     * Remarks: 
     * 	-) point indices range from 0..n-1
     * 
     */		   
    public static void write(int[] polygon, String filename) { 
    	if(polygon!=null) {
    		System.out.print("Writing a polygon to file: "+filename+"...");
    		int n=polygon.length; // input size

    		TC.ecritureDansNouveauFichier(filename);
    		for(int i=0; i<n; i++)
    			TC.println(polygon[i]); // the index of the i-th point on the boundary of the polygon (in ccw order)

    		TC.ecritureSortieStandard(); // end of file
    		System.out.println("done");
    	}
    	else
    		System.out.println("Writing a polygon to file: polygon not defined");
    }

}
