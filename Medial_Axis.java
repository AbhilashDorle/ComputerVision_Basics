import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Medial {
	ArrayList<Integer> list	= new ArrayList<Integer>(); 
	int count=1;//THE MAXIMUM VALUE OF IMAGE INTENSITY IN THE DISTANCE TRANSFORM

	/**********METHOD TO FIND THE DISTANCE TRANSFORM**********/
	void trans(int img[][]) throws IOException
	{
		int p=0,b=1,flag=1;
		for(int i=1;i<511;i++)
		{
			for(int j=1;j<511;j++)
			{
				if(img[i][j]==255)
				{
					if(img[i-1][j]==0 || img[i][j-1]==0 || img[i+1][j]==0 || img[i][j+1]==0)
					{
						img[i][j]=1;
					}
				}
			}
		}
		while(flag==1)
		{
			flag=0;
			list.add(b);
			b=b+1;
			count+=1;
			for(int i=1;i<511;i++)
			{
				for(int j=1;j<511;j++)
				{
					if(img[i][j]==255)
					{
						if(img[i-1][j]==list.get(p) || img[i][j-1]== list.get(p) || img[i+1][j]==list.get(p) || img[i][j+1]==list.get(p))
						{
							img[i][j]=b;
						}
						flag=1;
					}
				}
			}
			p=p+1;
		}

		/****MAKING THE LAST PIXEL OF THE BOUNDARY ZERO TO REMOVE MINOR IRREGULARITIES THAT MIGHT COME UP****/
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				if(img[i][j]==255)
					img[i][j]=0;
			}
		}
	}

	/**********METHOD TO FIND THE MEDIAL AXIS**********/
	void skeletal(int img[][])throws IOException
	{
		int counter=1;
		/*****ALGORITHM TO FIND THE DISTANCE TRANSFORM*****/
		while(counter<=count)
		{
			for(int i=1;i<511;i++)
			{
				for(int j=1;j<511;j++)
				{
					if(img[i][j]==counter)
					{
						if(img[i][j]<img[i+1][j] || img[i][j]<img[i-1][j] || img[i][j]<img[i][j+1] || img[i][j]<img[i][j-1])
							img[i][j]=0;
					}
				}
			}
			counter+=1;
		}
		int a[][] = new int[512][512];
		/****THE PIXELS OF MEDIAL AXIS ARE ASSIGNED WITH A VALUE OF 150,INSTEAD OF 1, JUST TO MAKE IT VISIBLE****/
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				if(img[i][j]!=0)
					a[i][j]=150;
				else
					a[i][j]=0;
			}
		}
		imag(a);
	}

	/**********METHOD TO GET THE IMAGE BACK FROM MEDIAL AXIS**********/
	void back(int img[][]) throws IOException
	{
		int counter = count;
		while(counter>1)
		{
			for(int i=1;i<511;i++)
			{
				for(int j=1;j<511;j++)
				{
					if(img[i][j]==counter)
					{
						if(img[i][j-1]==0)
							img[i][j-1]=counter-1;
						if(img[i][j+1]==0)
							img[i][j+1]=counter-1;
						if(img[i+1][j]==0)
							img[i+1][j]=counter-1;
						if(img[i-1][j]==0)
							img[i-1][j]=counter-1;
					}		
				}
			}
			counter-=1;
		}
		int a[][] = new int[512][512];
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				if(img[i][j]!=0)
					a[i][j]=255;
				else
					a[i][j]=0;
			}
		}
		imag(a);
	}

	/******METHOD TO INVERT THE IMAGE INTENSITIES AFTER THRESHOLDING*******/
	void invert(int img[][]) throws IOException
//THIS INVERSION IS PERFORMED TO FIT IN THE ALGORITHM THAT IS APPLIED ON THE IMAGE
	{
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				if(img[i][j]==255)
					img[i][j]=0;
				else
					img[i][j]=255;
			}
		}
	}

	/**********METHOD TO APPLY THE THRESHOLD TO THE IMAGE**********/
	void thresholding(int img[][]) throws IOException
	{
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				if(img[i][j]>128)
					img[i][j]=255;
				else
					img[i][j]=0;
			}
		}
		imag(img);
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
							read.setRGB(i, j, pixel);
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
	
	/**********MAIN METHOD**********/
	public static void main(String[] args) throws IOException 
	{
		int arr[][] = new int[512][512];
		File f = new File("C:\\comb.img");
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
		Medial ob = new Medial();
		ob.thresholding(arr);
		ob.invert(arr);
		ob.trans(arr);
		ob.skeletal(arr);
		ob.back(arr);
	}
}
 

