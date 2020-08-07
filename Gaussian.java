import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FourA {
	public static void main(String args[])
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src, src_gray = new Mat(), dst = new Mat();
        int kernel_size = 3;
        int i=1,o=0,j=1,p=1;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_8U;
    	double sigma=5;
    	while(j<4)
    	{
    		src = Imgcodecs.imread("C:\\Computer Vision\\Test"+p+".jpg");
        	while(i<10)
        		{
        			Imgproc.GaussianBlur(src, dst, new Size(5, 5), 5, sigma);
        			Imgproc.Laplacian(dst, src, 8, kernel_size, scale, delta, Core.BORDER_DEFAULT );
        			if(sigma%1==0)
        			{
        				String f="C:\\Computer Vision\\sigma"+o+".jpg";
        				o++;
        				Imgcodecs.imwrite(f, src);
        			}
        			sigma = sigma - 0.5;
        			i++;
        		}
        	sigma=5;
        	i=1;
        	p=p+1;
        	j++;
    	}
	}
}

 