import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Read 
{
	int a[] = new int[8];
	HashSet<Integer> set = new HashSet<Integer>();	//contains the labels of the image
	int area[] = new int[10];//stores the area of the objects
	int labels[];
	/*Function does the thresholding of the image*/
	int[][] thresholding(int[][] arr1) throws IOException
	{
		BufferedImage buf = new BufferedImage(512, 512, 3);
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				if(arr1[i][j]>128)//checking condition of the thresholding function
					arr1[i][j]=255;
				else
					arr1[i][j]=0;
				Color c = new Color(arr1[i][j],arr1[i][j],arr1[i][j]);
				buf.setRGB(i, j, c.getRGB());
			}
		}
        ImageIO.write(buf,"png", new File("output1.jpg"));
        return arr1;
	}


	/*Function displays the image*/
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


	/*Function performs the CCL labeling*/
	void label(int ar[][]) throws IOException
	{
		int labell=1,flag=0;
		int min=0;
		ArrayList<ArrayList<Integer>> outer = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> inner;
		
		for(int i=0;i<512;i++)
		{
			ar[i][0]=255;
			ar[i][511]=255;
		}
		for(int i=0;i<512;i++)
		{
			ar[0][i]=255;
			ar[511][i]=255;
		}
		for(int i=1;i<511;i++)
		{
			for(int j=1;j<511;j++)
			{
				
				if(ar[i][j]==0)
				{
					if(ar[i][j-1]==255 && ar[i-1][j]==255)
					{
						ar[i][j]=labell;
						labell++;		
					}
					else if(ar[i][j-1]!=0 && ar[i][j-1]!=255 && ar[i-1][j]==255)
					{
						ar[i][j]=ar[i][j-1];
					}
					else if(ar[i][j-1]==255 && ar[i-1][j]!=0 && ar[i-1][j]!=255)
					{
						ar[i][j]=ar[i-1][j];
					}
					else if(ar[i][j-1]==ar[i-1][j])
					{
						ar[i][j]=ar[i-1][j];
					}
					else if(ar[i][j-1]!=0 && ar[i][j-1]!=255 && ar[i-1][j]!=0 && ar[i-1][j]!=255)
					{
						ar[i][j]=ar[i][j-1];
						//Storing the labels in the equivalence sets
						for(int e=0;e<outer.size();e++)
						{
							if(outer.get(e).contains(ar[i-1][j]))
							{
								outer.get(e).add(ar[i][j-1]);
								flag=1;
							}
							
							else if(outer.get(e).contains(ar[i][j-1]))
							{
								outer.get(e).add(ar[i-1][j]);
								flag=1;
							}
						}
						if(flag!=1)
						{
							inner = new ArrayList<Integer>();
							inner.add(labell);
							inner.add(ar[i-1][j]);
							outer.add(inner);
						}
						
						flag=0;
					}
				}
			}
		}
		for(int i=0;i<outer.size();i++)
		{
			min=outer.get(i).get(0);
			for(int j=0;j<outer.get(i).size();j++)
			{
				if(outer.get(i).get(j)<min)
				{
					min=outer.get(i).get(j);
					a[i]=min;
				}
			}
		}
		for(int i=1;i<511;i++)
		{
			for(int j=1;j<511;j++)
			{
				for(int k=0;k<outer.size();k++)
				{
					if(outer.get(k).contains(ar[i][j]))
					{
						ar[i][j]=a[k];
					}
				}
			}
			
		}
		imag(ar);
	}


	/*Function calculates the area of the objects and size filtering*/
	void areaa(int img[][]) throws IOException
	{
		for(int i=0;i<512;i++)
		{
			for(int j=0;j<512;j++)
			{
				if(img[i][j]!=255)
				{
					set.add(img[i][j]);
				}
			}
		}
	
		labels = new int[set.size()];
		Iterator it = set.iterator();
		int index = 0;
		while(it.hasNext()) labels[index++] = (int)it.next(); 
		for(int i=0;i<area.length;i++)
		{
			area[i]=0;
		}
		for(int p=0;p<labels.length;p++)
		{			
			for(int i=0;i<512;i++)
			{
				for(int j=0;j<512;j++)
				{
					if(img[i][j]==labels[p])
					{
						area[p]++;
					}
				}
			}
		}
		int dummy[] = new int[labels.length];
		for(int i=0;i<dummy.length;i++)
		{
			dummy[i]=labels[i];
		}
        /*SIZE FILTERING*/
		for(int i=0;i<area.length;i++)
		{
			if(area[i]>8912)
			{
				dummy[i]=0;
			}
		}
		for(int i=0;i<area.length;i++)
		{
			if(area[i]<8912)
			{
				labels[i]=0;
			}
		}
		for(int p=0;p<labels.length;p++)
		{
			if(dummy[p]!=0)
			{
				for(int i=0;i<512;i++)
					
				{
					for(int j=0;j<512;j++)
					{
						if(img[i][j]==dummy[p])
						{
							img[i][j]=255;
						}
					}
				}
			}
		}
		imag(img);
	}


	/*Function calculates the coordinates of Centroids*/
	void posit(int img[][])
	{
		
		int xt=0;
		int yt=0;
		int x=0,y=0;
		int dum[][]=new int[4][2];
		System.out.println("The location of the centroids are");
		for(int p=0;p<labels.length;p++)
		{
			if(labels[p]!=0)
			{
				for(int i=0;i<512;i++)
				{
					for(int j=0;j<512;j++)
					{
						if(img[i][j]==labels[p])
						{
						xt+=i;
						yt+=j;
						}
					}
			}
			xt=xt/area[p];
			yt=yt/area[p];
			System.out.print(xt+" ");
			System.out.println(yt);
			}
		}
	}


	/*Function calculates the perimeter and compactness*/
	void boundary(int img[][])
	{
		int peri,comp;
		System.out.println("The perimeter and compactness of the objects are");
		for(int p=0;p<labels.length;p++)
		{
			peri=0;comp=0;
			if(labels[p]!=0)
			{
				for(int i=0;i<512;i++)
				{
					for(int j=0;j<512;j++)
					{
						if(img[i][j]==labels[p])
						{
							if(img[i-1][j]==255 || img[i][j-1]==255 || img[i][j+1]==255 || img[i+1][j]==255)
							{
								peri++;
							}
						}
					}
				}
			}
			if(peri>0)
			{
				comp=(peri*peri)/area[p];
				System.out.print(peri+" ");
				System.out.println(comp);
			}
		}
	}


	/*Function calculates the axis of orientation and eccentricities of the objects*/
	void orientation(int img[][])
	{
		int a,b,c,tot,x1,x2;
		double e;
		System.out.println("The orientations and eccentricities are");
		for(int p=0;p<labels.length;p++)
		{
			a=0;b=0;c=0;tot=0;x1=0;x2=0;e=0.0;
			for(int i=0;i<512;i++)
			{
				for(int j=0;j<512;j++)
				{
					if(img[i][j]==labels[p])
					{
						a+=i*i;
						b+=i*j;
						c+=j*j;
					}
				}
			}
			if((a-c)!=0)
			{
			tot=(b/(a-c));
			System.out.print(Math.atan(tot)+" ");
			x1=(int) (0.5*(a+c)+0.5*(a-c)*Math.cos((int)2*Math.atan(tot))+0.5*b*Math.sin((int)2*Math.atan(tot)));
			x2=(int)(0.5*(a+c)+(a-c)*Math.cos(90-2*Math.atan(tot))+0.5*b*Math.sin(90-(int)2*Math.atan(tot)));
			e=Math.sqrt(x2)/Math.sqrt(x1);
			System.out.println(e);
			}
		}
	}


	/*Function calculates the bounding box*/
	void bound(int img[][])
	{
		int xmin,xmax,ymin,ymax;
		System.out.println("The bound box is");
		for(int p=0;p<labels.length;p++)
		{
			xmin=522;xmax=0;ymin=522;ymax=0;
			if(labels[p]!=0)
			{
			for(int i=0;i<512;i++)
			{
				for(int j=0;j<512;j++)
				{
					if(img[i][j]==labels[p])
					{
						if(i<xmin)
						{
							xmin=i;
						}
						if(i>xmax)
						{
							xmax=i;
						}
						if(j<ymin)
							ymin=j;
						if(j>ymax)
							ymax=j;
					}
				}
			}
			System.out.println(xmin +" "+ymin);
			System.out.println(xmax +" "+ymax);
			}
		}
	}

	
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
		Read ob = new Read();
	    ob.thresholding(arr);
	    ob.label(arr);
	    ob.areaa(arr);
	    ob.posit(arr);
	    ob.boundary(arr);
	    ob.orientation(arr);
	    ob.bound(arr);
	}
}
					
