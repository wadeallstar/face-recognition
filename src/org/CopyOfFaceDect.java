package org;

import java.io.File;
import java.util.Timer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/*
 * Detects faces in an image, draws boxes around them, and writes the results
 * to "faceDetection.png".
 */
public class CopyOfFaceDect{
    public Point[] framePosition(Size sz,Rect rect)
    {
    	
    	//recognition  frame middle point
    	double mid_x = rect.x + rect.width/2.0;
    	double mid_y = rect.y + rect.height/2.0;
    	
    	//calculate area ratio
    	double resize_factor = Math.log(sz.height/rect.height)/Math.log(10);
//    	System.out.println("resize_factor:"+resize_factor);
    	//middle point right side width
//    	double right_width = resize_factor*rect.width/2 + rect.width;
    	double right_width = resize_factor*rect.width/2;
    	
    	//middle point left side width
//    	double left_width = resize_factor*rect.width/2 + rect.width;
    	double left_width = resize_factor*rect.width/2;
    	
    	//middle point up side width
    	double up_height = 0;
    	//middle point down side width
    	double down_height = 0;
    	//width both side ratio
    	double ratio = 1.6;
    	//total width
    	double width = 0;
    	//total height
    	double height = 0;
    	//temp max width based on picture
    	double temp_width = 0;
    	//width scaling
    	double scale = 0;
    	//mid's right width calculate
    	
    	if(mid_x+right_width>sz.width)
    	{
    		right_width = sz.width - mid_x;
    		left_width = ratio*right_width;
    		if(mid_x-left_width<0)
    		{
    			left_width = mid_x;
    		}
    	}
    	else if(mid_x-left_width<0)
    	{
    		left_width = mid_x;
    		if(mid_x+ratio*left_width<=sz.width)
    		{
    			right_width = ratio*left_width;
    		}
    	}
    	
    	
    	width = right_width + left_width;
    	double factor = sz.height/width;
    	if(factor<1.25)
    	{
    		temp_width = sz.height/1.25;
    		scale = temp_width/width; 
    		right_width = scale*right_width;
        	left_width = scale*left_width;
    	}
    	width = right_width+left_width;
    	
    	//set middle point's height 
    	up_height = 1*width/2.0;
		down_height = 3*width/4.0;
		
    	if(mid_y+down_height>sz.height)
    	{
    		down_height = sz.height-mid_y;
    		up_height = 5*width/4.0 - down_height;
    		if(mid_y-up_height<0)
    		{
    			up_height = mid_y;
    		}
    	}
    	else if(mid_y-up_height<0)
    	{
    		up_height = mid_y;
    		down_height = 5*width/4.0 - up_height;
    		if(mid_y+down_height>sz.height)
    		{
    			down_height = sz.height-mid_y;
    		}
    	}
		height = up_height + down_height;
		
		double x1 = mid_x-left_width;
		double y1 = mid_y-up_height;
		double x2 = mid_x+right_width;
		double y2 = mid_y+down_height;
		//return two point,points[0] is start point,point[1] is end point
		Point[] points = new Point[2];
		points[0] = new Point(x1,y1);
		points[1] = new Point(x2,y2);
//		System.out.println(points[0]+":"+points[1]);
		return points;
    }
    public File detect(File source){
//        System.out.println("\nRunning DetectFaceDemo");

        // Create a face detector from the cascade file in the resources
        // directory.
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        CascadeClassifier faceDetector = new CascadeClassifier("face_recognition.xml");
        Mat src = Highgui.imread(source.getAbsolutePath());
        double c = 1;
        if(src.width() >400 || src.height()>300){
        	double w = (double)src.width()/400;
        	double h = (double)src.height()/300;
        	c = w>h?w:h;
        }
        Mat image = new Mat();
        Size sz = new Size((int)src.width()/c,(int)src.height()/c);
        Size min = new Size(20,20);
        Imgproc.resize( src, image, sz );
        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections,1.05,3,0,new Size(20,20),sz);

        
        
        System.out.println(String.format("Detected %s faces",
                faceDetections.toArray().length));

        /*
         * set the parameters of logical relationship 
         	faces = cascade.detectMultiScale(gray,1.23,4)
        	if len(faces) == 0 :
            faces = cascade.detectMultiScale(gray,1.19,3)
        	if len(faces)==0:
            faces = cascade.detectMultiScale(gray,1.01,20)
        	if len(faces)==0:
            faces = cascade.detectMultiScale(gray,1.01,10)

         */
        
        // Draw a bounding box around each face.
        int rect_contain = faceDetections.toArray().length;
        if(rect_contain>0)
        {
        	Rect rect = faceDetections.toArray()[rect_contain-1];
        	
        	//original second point
        	double rect_x2 = rect.x + rect.width;
        	double rect_y2 = rect.y + rect.height;
        	
        	Point[] points = framePosition(sz, rect);
        	/*
        	System.out.println("original point:");
    		System.out.println(rect.x+" "+rect.y);
    		System.out.println(rect_x2+" "+rect_y2);
    		System.out.println("final point:");
    		System.out.println(points[0].x+" "+points[0].y);
    		System.out.println(points[1].x+" "+points[1].y);
    		System.out.println("ratio:"+(points[1].x-points[0].x)/(points[1].y-points[0].y));
    		*/
    		if(points[0].x>rect.x||points[1].x<rect_x2)
    		{
    			System.out.println("picture is too small");
    		}
    		else
    		{
    			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect_x2, rect_y2), new Scalar(0, 0, 255));
    			Core.rectangle(image, points[0], points[1], new Scalar(0, 255, 0));
    		}
        }
        
        	
           

        // Save the visualized detection.
//        String filename = "faceDetection.png";
        String filename = source.getName().substring(0, source.getName().indexOf('.')) + ".png";
        
        System.out.println(filename);        
//        System.out.println(String.format("Writing %s", filename));
        Highgui.imwrite(filename, image);
        File rtn = new File(filename);
        return rtn;
    }
    public static void main (String [] args){
    	
    	long startTime=System.currentTimeMillis();  
    	File filedir = new File("image1");
    	File[] files = filedir.listFiles();
    	String filenameString;
    	for(int i = 0;i<files.length; i++)
    	{
        	CopyOfFaceDect fd = new CopyOfFaceDect();
        	fd.detect(files[i]);
    	}
    	long endTime = System.currentTimeMillis();
    	System.out.println("time:"+(endTime-startTime)/1000);
    }
    
}
