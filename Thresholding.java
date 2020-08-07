import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Three 
{
	int histogram[] = new int[256];

	int pi=0,pj=0,v=0;//triplets used for peakiness detection
	int goodness=0,valley=0;//the valley corresponding to the measure of goodness

	/**********FUNCTION TO PERFORM THRESHOLDING USING PEAKINESS DETECTION***********/
	int[][] peak(int img[][], int a, int b) throws IOException
	{
		int imge[][] = new int[a][b];
		int index=0,t=0;
		int start=0,end=255;
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				imge[i][j]=img[i][j];
			}
		}
		/****GENERATING THE HISTOGRAM****/
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				index=imge[i][j];
				histogram[index]+=1;
			}
		}
		peaks(start,end);
		for(int i=0;i<256;i++)
		{
			if(histogram[i]==valley)
			{
				t=i;//threshold value
			}
		}
		threshold(imge,t,a,b);
		return imge;
	}

	/**********LISTING ALL THE TRIPLETS OF PEAKS AND VALLEYS EXHAUSTIVELY***********/
	void peaks(int start, int end)
	{
		int counter=0;
		int d = 4;
		while(start<end-d)
		{
			pi=histogram[start];
			counter=start+d;
			v=histogram[start+1];
			while(counter<=end)
			{
				pj=histogram[counter];
				for(int i=start+1;i<counter;i++)
				{
					if(v>histogram[i])
					{
						v=histogram[i];
					}
				}
				goddness(pi,pj,v);
				counter+=1;
			}
			start+=1;	
		}
	}

	/**********FUNCTION TO FIND THE MEASURE OF GOODNESS***********/
	void goddness(int p1,int p2,int v)
	{
		int diff = Math.abs(p1-p2);
		int dom = (p1+p2)/2;
		int deep = (v+1);
		int close = (Math.abs((diff/2)-v)+1);
		int g = (diff*dom)*(deep*close);
		if(g<goodness)
		{
			goodness=g;
			valley=v;
		}
	}

	/**********FUNCTION TO PERFORM DUAL THRESHOLDING***********/
	int[][] dual(int img[][], int a, int b)
	{
		int imge[][] = new int[a][b];
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				imge[i][j]=img[i][j];
			}
		}
		/****T1 AND T2 ARE TOWARDS THE EITHER END OF THE HISTOGRAM****/
		int t1=144;
		int t2=208;
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				if(imge[i][j]<t1)
				{
					imge[i][j]=0;
				}
				else if(imge[i][j]>t2)
				{
					imge[i][j]=255;
				}
			}
		}
		for(int i=1;i<a-1;i++)
		{
			for(int j=1;j<b-1;j++)
			{
				if(imge[i][j]>t1 && imge[i][j]<t2)
				{
					if(imge[i-1][j]<t1 || imge[i][j-1]<t1 || imge[i+1][j]<t1 || imge[i][j+1]<t1)
					{
						imge[i][j]=0;
					}
				}
			}
		}
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				if(imge[i][j]!=0)
				{
					imge[i][j]=255;
				}
			}
		}
		return imge;
	}

	/**********FUNCTION TO PERFORM ITERATIVE THRSEHOLDING***********/
	int[][] iterative(int img[][],int a, int b) throws IOException
	{
		int imge[][] = new int[a][b];
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				imge[i][j]=img[i][j];
			}
		}
		int T=0,sum=0,tnext=0;
		int r1,r2;
		int c1,c2;
		int flag=0;
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				sum = sum + imge[i][j];
			}
		}
		T=sum/(a*b);//calculation of the initial threshold
		while(flag==0)//iteratively calculating the best threshold value
		{
			r1=0;r2=0;
			c1=0;c2=0;
			tnext=0;
			for(int i=0;i<a;i++)
			{
				for(int j=0;j<b;j++)
				{
					if(imge[i][j]<=T)
					{
						r1+=imge[i][j];
						c1+=1;
					}
					else
					{
						r2+=imge[i][j];
						c2+=1;
					}
				}
			}
			r1=r1/c1;
			r2=r2/c2;
			tnext=(r1+r2)/2;
			if(tnext==T)
				flag=1;
			else
				T=tnext;
		}
		threshold(imge,tnext,a,b);
		return imge;
	}

	/**********FUNCTION TO PERFORM ADAPTIVE THRESHOLDING***********/
	int[][] adaptive(int img[][],int a,int b) throws IOException
	{
		int imge[][] = new int[a][b];
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				imge[i][j]=img[i][j];
			}
		}
		int a1[][] = new int[256][256];
		int a2[][] = new int[256][256];
		int a3[][] = new int[256][256];
		int a4[][] = new int[256][256];
		/****DIVISION OF IMAGE INTO FOUR SUBIMAGES****/
		for(int i=0;i<256;i++)
		{
			for(int j=0;j<256;j++)
			{
				a1[i][j]=imge[i][j];
			}
		}
		int[][] b1 = iterative(a1,256,256);
		for(int i=0;i<256;i++)
		{
			for(int j=256;j<512;j++)
			{
				a2[i][j-256]=imge[i][j];
			}
		}
		int b2[][] = iterative(a2,256,256);
		for(int i=256;i<512;i++)
		{
			for(int j=0;j<256;j++)
			{
				a3[i-256][j]=imge[i][j];
			}
		}
		int b3[][] = iterative(a3,256,256);
		for(int i=256;i<512;i++)
		{
			for(int j=256;j<512;j++)
			{
				a4[i-256][j-256]=imge[i][j];
			}
		}
		int b4[][] = iterative(a4,256,256);
		/****GENERATING THE ORIGINAL IMAGE FROM THE SUBIMAGE****/
		for(int i=0;i<256;i++)
		{
			for(int j=0;j<256;j++)
			{
				imge[i][j]=b1[i][j];
			}
		}
		for(int i=0;i<256;i++)
		{
			for(int j=256;j<512;j++)
			{
				imge[i][j]=b2[i][j-256];
			}
		}
		for(int i=256;i<512;i++)
		{
			for(int j=0;j<256;j++)
			{
				imge[i][j]=b3[i-256][j];
			}
		}
		for(int i=256;i<512;i++)
		{
			for(int j=256;j<512;j++)
			{
				imge[i][j]=b4[i-256][j-256];
			}
		}
		return imge;
	}

	/**********FUNCTION TO THRESHOLD THE IMAGE**********/
	void threshold(int image[][], int thresh, int a, int b) throws IOException
	{
		for(int i=0;i<a;i++)
		{
			for(int j=0;j<b;j++)
			{
				if(image[i][j]>thresh)
					image[i][j]=255;
				else
					image[i][j]=0;
			}
		}
	}

	/**********FUNCTION DISPLAYS THE IMAGES ON A PANEL**********/
    void imag(int ab[][])throws IOException
	{	
		final BufferedImage read = new BufferedImage(512,512,BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
					int pixel=ab[i][j] * 0x10101;
							read.setRGB(j, i, pixel);
			}
		}
		JFrame frame = new JFrame("ini");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel()
				{
					@Override
		            protected void paintComponent(Graphics g) {
		                Graphics2D g2d = (Graphics2D)g;
		                g2d.clearRect(0, 0, getWidth(), getHeight());
		                g2d.drawImage(read, 0, 0, this);
		            }

				};
		        panel.setPreferredSize(new Dimension(512, 512));
		        frame.getContentPane().add(panel);
		        frame.pack();
		        frame.setVisible(true);
	}
	
    /**********MAIN FUNCTION***********/
	public static void main(String[] args) throws IOException 
	{
		int arr[][] = new int[512][512];
		File f = new File("C:\\Computer Vision\\test1.img");
		byte[] b = Files.readAllBytes(f.toPath());
		int a[] = new int[b.length-512];
		int p=0;
		for(int i=512;i<a.length;i++)
		{
			if(b[i]<0)
			{
				a[p]=b[i] & 0xff;
			p++;
			}
			else
			{
				a[p]=b[i];
				p++; 
			}
		}
		p=0;
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				arr[i][j]=a[p];
				p++;
			}
		}
		Three one = new Three();
		int[][] onei = one.iterative(arr,512,512);
		one.imag(onei);
		int[][] onea = one.adaptive(arr, 512, 512);
		one.imag(onea);
		int[][] onep = one.peak(arr, 512, 512);
		one.imag(onep);
		int[][] oned=one.dual(arr,512,512);
		one.imag(oned);
		int arr1[][] = new int[512][512];
		File f1 = new File("C:\\Computer Vision\\test2.img");
		byte[] b1 = Files.readAllBytes(f1.toPath());
		int a1[] = new int[b1.length-512];
		int p1=0;
		for(int i=512;i<a1.length;i++)
		{
			if(b1[i]<0)
			{
				a1[p1]=b1[i] & 0xff;
			p1++;
			}
			else
			{
				a1[p1]=b1[i];
				p1++; 
			}
		}
		p1=0;
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				arr1[i][j]=a1[p1];
				p1++;
			}
		}
		Three two = new Three();
		int[][] twoi = two.iterative(arr1,512,512);
		two.imag(twoi);
		int[][] twoa = two.adaptive(arr1, 512, 512);
		two.imag(twoa);
		int[][] twop = two.peak(arr1, 512, 512);
		two.imag(twop);
		int[][] twod=two.dual(arr1,512,512);
		two.imag(twod);
		int arr2[][] = new int[512][512];
		File f2 = new File("C:\\Computer Vision\\test3.img");
		byte[] b2 = Files.readAllBytes(f2.toPath());
		int a2[] = new int[b2.length-512];
		int p2=0;
		for(int i=512;i<a2.length;i++)
		{
			if(b2[i]<0)
			{
				a2[p2]=b2[i] & 0xff;
			p2++;
			}
			else
			{
				a2[p2]=b2[i];
				p2++; 
			}
		}
		p2=0;
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				arr2[i][j]=a2[p2];
				p2++;
			}
		}
		Three threee = new Three();
		int[][] threeei = threee.iterative(arr2,512,512);
		threee.imag(threeei);
		int[][] threeea = threee.adaptive(arr2, 512, 512);
		threee.imag(threeea);
		int[][] threeep = threee.peak(arr2, 512, 512);
		threee.imag(threeep);
		int[][] threeed=threee.dual(arr2,512,512);
		threee.imag(threeed);
	}
}

