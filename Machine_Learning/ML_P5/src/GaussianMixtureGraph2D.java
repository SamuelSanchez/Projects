import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.Random;

import javax.swing.*;
 
//TODO: this requires more testing - under development
public class GaussianMixtureGraph2D extends JPanel {
	private static final long serialVersionUID = 987456321L;
	
	private static final int PAD = 20;
    private int clusters;
    private double[][] dataPoints;
    private double[][] responsability;
    private double[][] mean;
    private Color[] colors;
    private static final double color_threshold = 50;
    private static final int sleepTime = 200; //500; //In nanoseconds
    private double max_x;
    private double max_y;
    private double min_x;
    private double min_y;
    
    public GaussianMixtureGraph2D(){
    	this(3, new double[0][0]);
    }
    
    public GaussianMixtureGraph2D(int clusters, double[][] dataPoints){
    	this.clusters = clusters;
    	this.dataPoints = dataPoints;
    	this.colors = new Color[clusters];
    	this.responsability = new double[0][0];
    	createClusterColors();
    	findMaxMinValues();
    }
    
    public void createClusterColors(){
    	int[][] colorTemp = new int[clusters][3]; //Stores the rgb (red,green,blue) colors
    	
    	//Create different colors for each cluster
    	for(int k = 0; k < clusters;){
    		int r = new Random().nextInt(255);
    		int g = new Random().nextInt(255);
    		int b = new Random().nextInt(255);
    		
    		boolean isSimilarColor = false;
    		//make sure that this combination is not existing here yet with 25 points difference
    		for(int i = 0; i < k; i++){
    			if((Math.abs(colorTemp[k][0]-r) + Math.abs(colorTemp[k][1]-g) + Math.abs(colorTemp[k][2]-b)) < color_threshold){
    				isSimilarColor = true;
    				break;
    			}
    		}
    		//Skip this combination of rgb
    		if(isSimilarColor){
    			continue;
    		}
    		//Otherwise keep this color
    		colorTemp[k][0] = r;
    		colorTemp[k][1] = g;
    		colorTemp[k][2] = b;
    		//Store this color
    		colors[k] = new Color(r,g,b, 250);
    		
    		k++;
    	}
    }
 
    public void update(double[][] responsability, double[][] mean){
    	this.responsability = responsability;
    	this.mean = mean;
    	repaint();
    	//Sleep to see changes
        try{
        	if(responsability.length > 0){
        		Thread.sleep(sleepTime);
        	}
        }catch(Exception e){
        	/*Do nothing*/
        }
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
//        double w = Math.abs(min_x) + Math.abs(max_x);
//        double h = Math.abs(min_y) + Math.abs(max_y);
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        String s = "y axis";
        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
        for(int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float)font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw)/2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        // Abcissa label.
        s = "x axis";
        sy = h - PAD + (PAD - sh)/2 + lm.getAscent();
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (w - sw)/2;
        g2.drawString(s, sx, sy);
        // Draw lines.
        double scaleX = (double)(w - 2*PAD)/(max_x - min_x);
        double scaleY = (double)(h - 2*PAD)/(max_y - min_y);
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD+(scaleX*Math.abs(min_x)), PAD, PAD+(scaleX*Math.abs(min_x)), h-PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD-(scaleY*Math.abs(min_y)), w-PAD, h-PAD-(scaleY*Math.abs(min_y))));
//        g2.setPaint(Color.green.darker());
//        for(int i = 0; i < data.length-1; i++) {
//            double x1 = PAD + i*xInc;
//            double y1 = h - PAD - scale*data[i];
//            double x2 = PAD + (i+1)*xInc;
//            double y2 = h - PAD - scale*data[i+1];
//            g2.draw(new Line2D.Double(x1, y1, x2, y2));
//        }
        // Mark data points.
        g2.setPaint(Color.red);
        //Mark points
        for(int n = 0; n < dataPoints.length; n++) {
        	//Get the points
        	double x = PAD + (scaleX*Math.abs(min_x)) +(scaleX*dataPoints[n][0]);
    		double y = h - PAD - (scaleX*Math.abs(min_x)) - scaleY*dataPoints[n][1];
    		
        	//Color the point with respect to the highest cluster
    		double highK = 0;
    		int position = 0;
    		if(responsability.length > 0){
	        	for(int k = 0; k < clusters; k++){
	        		if(responsability[n][k] > highK){
	        			highK = responsability[n][k];
	        			position = k;
	        		}
	        	}
	        	//Change to the correct marker color
	        	g2.setPaint(colors[position]);
    		}
    		
        	g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
        
        //Mark means
        for(int k = 0; k < clusters; k ++){
        	double meanX = PAD + (scaleX*Math.abs(min_x)) +(scaleX*mean[k][0]); //x
        	double meanY =  h - PAD - (scaleX*Math.abs(min_x)) - scaleY*mean[k][1]; //y
        	g2.setPaint(colors[k]);
        	g2.fill(new  java.awt.geom.Rectangle2D.Double(meanX-2, meanY-2, 8, 8));
        }
    }
 
    private void findMaxMinValues(){
    	double max_x = 0;
    	double max_y = 0;
    	double min_x = 0;
    	double min_y = 0;
    	for(int n = 0; n < dataPoints.length; n++){
    		if(dataPoints[n][0] > max_x){ //x
    			max_x = dataPoints[n][0];
    		}
    		if(dataPoints[n][1] > max_y){
    			max_y = dataPoints[n][1]; //y
    		}
    		if(dataPoints[n][0] < min_x){ //x
    			min_x = dataPoints[n][0];
    		}
    		if(dataPoints[n][1] < min_y){
    			min_y = dataPoints[n][1]; //y
    		}
    	}
    	//Store values
    	this.max_x = max_x;
    	this.max_y = max_y;
    	this.min_x = min_x;
    	this.min_y = min_y;
    }
}