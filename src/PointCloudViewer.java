import processing.core.*;

import java.awt.Color;

/**
 * A class for drawing graphs (using Processing 1.5.1)
 *
 * @author Luca Castelli Aleardi (Ecole Polytechnique, 2020)
 */
public class PointCloudViewer extends PApplet {
    // coordinates of the bounding box
    protected double xmin=0., xmax=Double.MIN_VALUE, ymin=0., ymax=Double.MIN_VALUE;

    // parameters for edge rendering
    double boundaryThickness=0.5;
    private int backgroundColor=255;
    private int edgeColor=50;
    private int edgeOpacity=200;
        
    /** node selected with mouse click (to show)  */
    public int selectedNode=-1; 
    public GridPoint_2 current; // coordinates of the selected point
        
    /** input point cloud to render */
    static public GridPoint_2[] inputPoints=null;
    /** input point cloud to render */
    static public int[] optimalPolygon=null; // minimal (or maximal) area polygon to render

    /** height of the grid (input parameter) */
    static public int drawingHeight;
    /** width of the grid (input parameter) */
    static public int drawingWidth;
        	
   	// parameters of the 2d frame/canvas
    /** horizontal size of the canvas (pizels) */
    public static int sizeX;
    /** vertical size of the canvas (pixels) */
    public static int sizeY; // 
    
    /** range of the window (left bottom and right top corners) */
    public static GridPoint_2 a,b; // 
	  
	  /**
	   * Initialize the frame
	   */
	  public void setup() {
		  if(sizeX<=0 || sizeY<=0) {
			  System.out.println("Error: the size of the canvas is not defined");
			  System.out.println("WARNING: please do NOT run the class PointCloudViewer");
			  System.out.println("to launch the viewer, run the class AreaOptimizer");
			  System.exit(0);
		  }
		  System.out.println("Setting Canvas size: "+sizeX+" x "+sizeY);
		  this.size(sizeX,sizeY); // set the size of the Java Processing frame
		  
		  // set drawing parameters (size and range of the drawing layout)
		  int deltaX=2*drawingWidth/10;
		  int deltaY=2*drawingHeight/10;
		  this.a=new GridPoint_2(-deltaX, -deltaY); // left bottom corner (the drawing region is centered at the origin)
		  this.b=new GridPoint_2(drawingWidth+deltaX, drawingHeight+deltaY); // top right corner of the drawing region
	  }

	  /**
	   * Main method for drawing the applet
	   */
	  public void draw() {
	    this.background(this.backgroundColor); // set the color of background
	    
	    this.drawBoundingBox(); // draw the bounding box (smallest square containing the input points)
	    this.drawPolygon(); // draw the computed polygon (if defined)
	    this.display2D(); // draw the input points

	    if(this.selectedNode>=0) {
	    	this.drawVertexLabel(this.selectedNode);
	    }
	    
	    this.drawOptions();
	  }
	  
	  /**
	   * Deal with keyboard events
	   */
	  public void keyPressed(){
		  switch(key) {
		  case('-'):this.zoom(1.2); break;
		  case('+'):this.zoom(0.8); break;
		  }
	  }
	  
	  public void translateBox(int deltaX, int deltaY) {
			  GridVector_2 v=new GridVector_2(deltaX, deltaY);
			  a.translateOf(v);
			  b.translateOf(v);
			  System.out.println("translating window");
	  }
	  
	  public void zoom(double factor) {
	  }
	  	  
	  public void mouseClicked() {
		  if(mouseButton==LEFT) { // select a vertex (given its 2D position)
			  this.selectedNode=this.selectNode(mouseX, mouseY);
			  if(selectedNode>=0)
				  System.out.println("vertex "+selectedNode);
		  }
	  }

	  public void mousePressed() {
		  this.current=new GridPoint_2(mouseX, mouseY);
	  }
	  
	  public void mouseReleased() {
	  }
	  
	  public void mouseDragged() {
	  }
		
	    /**
	     * Update of the bounding box
	     */    
	    protected void update(double x, double y) {
	    	//if (x<xmin)
	    	//	xmin = x-boundaryThickness;
	    	if (x>xmax)
	    		xmax = x+boundaryThickness;
	    	//if (y<ymin)
	    	//	ymin = y-boundaryThickness;
	    	if (y>ymax)
	    		ymax = y+boundaryThickness;
	    }

	    /**
	     * Update the range of the drawing region (defined by corners points 'a' and 'b')
	     */    
	    public void updateBoundingBox() {
	    }
	    
	    /**
	     * Return the current coordinates of the bounding box
	     */    
	    public double[] boundingBox() {
	    	return new double[] {xmin, xmax, ymin, ymax};
	    }

		/**
		 * Return the integer coordinates of a pixel (px, py) corresponding to a given point 'v'. <br>
		 * <br>
		 * Warning: we must take care of the following parameters: <br>
		 * -) the size of the canvas <br>
		 * -) the size of bottom and left panels <br>
		 * -) the negative direction of y-coordinates (in java drawing) <br>
		 * 
		 * @return res[] an array storing the 'x' (stored in res[0]) and 'y' coordinates of the pixel on the screen
		 */
		public int[] getCoordinates(GridPoint_2 v) {
			double x=v.getX(); // coordinates of point v
			double y=v.getY();
			double xRange=b.getX()-a.getX(); // width and height of the drawing area
			double yRange=b.getY()-a.getY();
			int i= (int) (this.sizeX*( (x-a.getX()) / xRange )); // scale with respect to the canvas dimension
			int j= (int) (this.sizeY*( (y-a.getY()) / yRange ));
			j=this.sizeY-j; // y = H - py;
			
			int[] res=new int[]{i, j};
			return res;
		}
		
		  /**
		   * Draw a gray edge (u, v)
		   */
		  public void drawSegment(GridPoint_2 u, GridPoint_2 v) {		  
			int[] min=getCoordinates(u);
			int[] max=getCoordinates(v);
		    
			this.stroke(edgeColor, edgeOpacity);
		    this.line(	(float)min[0], (float)min[1], 
		    			(float)max[0], (float)max[1]);
		  }

		  /**
		   * Draw a colored edge (u, v)
		   */
		  public void drawColoredSegment(GridPoint_2 u, GridPoint_2 v, int r, int g, int b) {		  
			int[] min=getCoordinates(u);
			int[] max=getCoordinates(v);
		    
			this.stroke(r, g, b, edgeOpacity);
		    this.line(	(float)min[0], (float)min[1], 
		    			(float)max[0], (float)max[1]);
		  }

		  /**
		   * Draw the grid
		   */
		  public void drawGrid() {
		  }

		  /**
		   * Draw the bounding box
		   */
		  public void drawBoundingBox() {
			  int r=180, g=180, b=180; // gray

			  this.drawColoredSegment(new GridPoint_2(0, 0), new GridPoint_2(drawingWidth, 0), r, g, b);
			  this.drawColoredSegment(new GridPoint_2(0, drawingHeight), new GridPoint_2(drawingWidth, drawingHeight), r, g, b);
			  this.drawColoredSegment(new GridPoint_2(0, 0), new GridPoint_2(0, drawingHeight), r, g, b);
			  this.drawColoredSegment(new GridPoint_2(drawingWidth, 0), new GridPoint_2(drawingWidth, drawingHeight), r, g, b);
		  }

		  /**
		   * Draw a polygon
		   */
		  public void drawPolygon() {
			  if(this.optimalPolygon==null)
				  return;
			  int r=180, g=180, b=180; // gray
			  GridPoint_2 p=null, q=null;
			  int n=this.optimalPolygon.length;
			  
			  fill(200,200,200);
			  beginShape();
			  for(int i=0;i<n;i++) {
				  p=this.inputPoints[this.optimalPolygon[i]];
				  int[] min=getCoordinates(p); // pixel coordinates of the point in the frame
				  vertex(min[0],min[1]);
			  }
			  endShape();
			  
			  for(int i=0;i<n-1;i++) {
				  p=this.inputPoints[this.optimalPolygon[i]];
				  q=this.inputPoints[this.optimalPolygon[i+1]];
				  this.drawColoredSegment(p, q, 0, 0, 0);
			  }
			  p=q;
			  q=this.inputPoints[this.optimalPolygon[0]];
			  this.drawColoredSegment(p, q, 0, 0, 0);
		  }

		  /**
		   * Draw a vertex label on the canvas (close to the node location)
		   */
		  public void drawVertexLabel(int u) {
			int[] min=getCoordinates(inputPoints[u]); // pixel coordinates of the point in the frame
		    			
			String label=this.getVertexLabel(u); // retrieve the vertex label to show
			
			//this.stroke(edgeColor, edgeOpacity);
			this.fill(220);
			this.rect((float)min[0], (float)min[1], 90, 20); // fill a gray rectangle
			this.fill(0);
			this.text(label, (float)min[0]+4, (float)min[1]+14); // draw the vertex label*/
		  }

		  /**
		   * Show options on the screen
		   */
		  public void drawOptions() {
			String label=""; // text to show
			label=label+"use 'left mouse click' to show vertex indices and coordinates";
			
			int posX=2;
			int posY=2;
			int textHeight=24;
			
			//this.stroke(edgeColor, edgeOpacity);
			this.fill(240);
			this.rect((float)posX, (float)posY, 389, textHeight); // fill a gray rectangle
			this.fill(0);
			this.text(label, (float)posX+2, (float)posY+10); // draw the text
		  }

		  /**
		   * Select the vertex whose 2d projection is the closest to pixel (i, j)
		   */
		  public int selectNode(int i, int j) {			  
			  int result=-1;
			  
			  double minDist=40.;
			  for(int k=0;k<inputPoints.length;k++) { // iterate over the vertices of g
				  GridPoint_2 u=inputPoints[k];
				  GridPoint_2 p=new GridPoint_2(u.getX(), u.getY());
				  int[] q=this.getCoordinates(p);
				  
				  double dist=Math.sqrt((q[0]-i)*(q[0]-i)+(q[1]-j)*(q[1]-j));
				  if(dist<minDist) {
					  minDist=dist;
					  result=k;
				  }
			  }
			  
			  this.selectedNode=result;
			  return result;
		  }
		  
		  /**
		   * Draw the skeleton of a graph in 2D using a Processing frame
		   */
		  public void display2D() {
			  if(this.inputPoints==null || this.inputPoints.length==0)
				  return;
			  
			  this.fill(255,100);
				
				for(GridPoint_2 u: this.inputPoints) { // finally draw the vertices of g
					this.drawVertex(u); // color map is not computed
				}

		  }
		  
		  /**
		   * Compute the label of a vertex, from its index, spectral distortion and vertex age
		   */
		  public static String getVertexLabel(int i) {
	        return ""+i+" ("+inputPoints[i].getX()+", "+inputPoints[i].getY()+")";
		  }
		  
		  /**
		   * Draw a vertex u on the canvas
		   */
		  public void drawVertex(GridPoint_2 u) {
			  double maxValue;
			  
			int[] min=getCoordinates(u); // pixel coordinates of the point in the frame
		    
			//System.out.println("v"+u.index+" dist: "+distortion+" max: "+maxDistortion);
			
			this.stroke(50, 255); // border color
			this.fill(50, 50, 50, 255); // node color
			
			int vertexSize=4; // basic vertex size
			//double growingFactor=1.+(distortion*10.);
			//vertexSize=(int)(3+vertexSize*growingFactor);
			this.ellipse((float)min[0], (float)min[1], vertexSize, vertexSize);
		  }

			/**
			 * Return an "approximation" (as String) of a given real number (with a given numeric precision)
			 */
			private static String approxNumber(double a, int precision) {
				String format="%."+precision+"f";
				String s=String.format(format,a);
				return s;
			}

}
