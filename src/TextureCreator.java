import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class TextureCreator extends Frame{
	//Config:
	private int textureSize = 1024; //Target Texture size
	private float strength = 0.9f;  //hardness of the blend
	private int normalDivisor = 4; //used to downsample the Normal map for smoother results (1 very sharp - 5 very blury)    !!DON'T SET TO 0!!
	private boolean loadFromFile = true; //Load config from file
	//---
	
	private static final long serialVersionUID = 4483596174469520707L;
	private Button start = new Button("Create");
	private Button selectInput = new Button("Select Input");
	private FileDialog fd;
	private File input;
	private Label status = new Label("Select Input...");
	private Image ico = new BufferedImage(16,16,1);
	private Panel parentlayout = new Panel();
	
	public static void main(String[] args) {
		new TextureCreator();
	}
	
	public TextureCreator() {
		super();
		
		//set up Frame:
		setBackground(Color.darkGray);
		setTitle("<<BULK>> Texture Creator (by Erarnitox)");

		addWindowListener(new WindowListener());
		
	    ico.getGraphics().setColor(Color.CYAN);
	    ico.getGraphics().drawOval(0, 0, 8, 8);
	    ico.getGraphics().drawOval(0, 0, 16, 16);
	    this.setIconImage(ico);
	    		
		//set up Frame Content:
		start.setForeground(Color.WHITE);                    
	    start.setBackground(Color.GRAY);	    
	    start.addActionListener(new StartListener());
	  
	    selectInput.setForeground(Color.WHITE);                    
	    selectInput.setBackground(Color.GRAY);                  
	    selectInput.addActionListener(new FileListener());
	    
	    status.setForeground(Color.WHITE);
	    status.setFont(Font.decode("Bold"));
	    
		//add Frame Content:
	    parentlayout.setLayout(new BorderLayout());
	    parentlayout.add(start, BorderLayout.SOUTH);
	    parentlayout.add(selectInput, BorderLayout.NORTH);
	    parentlayout.add(status, BorderLayout.CENTER);
	    
	    add(parentlayout);
		
		//finish Frame:
	    this.setMinimumSize(new Dimension(500,50));
	    this.setResizable(false);
		pack();
		setVisible(true);
		
		fd = new FileDialog(this,"Input Directory");
		
		if(this.loadFromFile) {
			File configFile = new File("Texture.conf");
			
			if(configFile.exists()) {
				try {
					Scanner fr = new Scanner(configFile);
					while(fr.hasNext()) {
						String curr = fr.nextLine();
						if(curr.contains("Size")) {
							this.textureSize = Integer.parseInt(fr.nextLine().trim());
						}
						else if(curr.contains("Strength")) {
							this.strength = Float.parseFloat(fr.nextLine().trim());
						}
						else if(curr.contains("Divisor")) {
							this.normalDivisor = Integer.parseInt(fr.nextLine().trim());
						}
					}
					fr.close();
				} catch (FileNotFoundException e) {
					status.setText(e.getMessage());
				}
			}
			else {
				try {
					PrintWriter fos = new PrintWriter(configFile);
					
					fos.println("Size:");
					fos.println(this.textureSize+"\n");
					
					fos.println("Strength:");
					fos.println(this.strength+"\n");
					
					fos.println("Divisor:");
					fos.println(this.normalDivisor+"\n");
					
					fos.close();		
				} catch (FileNotFoundException e) {
					status.setText(e.getMessage());
				}	
			}
		}
	}
	
	class WindowListener extends WindowAdapter
	  {
	    public void windowClosing(WindowEvent e)
	    {
	      e.getWindow().dispose();                  
	      System.exit(0);                            
	    }    	
	  }
	
	class StartListener implements ActionListener
	  {
	    public void actionPerformed(ActionEvent e) 
	    {
	      try {
	    	if(input != null) {
	    		status.setText("0%...");
				bulkTextures(input);
	    	}else {
	    		status.setText("No Selection!");
	    	}
		} catch (IOException e1) {
			status.setText("Error!");
			}
	    }         	
	  }
	
	class FileListener implements ActionListener
	  {
	    public void actionPerformed(ActionEvent e) 
	    {
	    	try {
	    		fd.setVisible(true);
	    		input = new File(fd.getDirectory());
	    		status.setText(".../"+input.getName()+"/");
	    	}catch(Exception ext) {
	    		status.setText("No Selection!");
	    	}
	    }         	
	  }
	
	public void bulkTextures(File dir) throws IOException {
		    int count = 0;
		    int maxCount = dir.list().length;
		    int progress;
		    
			File[] Listing = dir.listFiles(); //Array of all Pictures in that Folder
				if (Listing != null) { //if there are pictures in the Folder 
					for(File image : Listing) { //do for all pictures 
						progress = (int)(((float)count/(float)maxCount)*100);
						status.setText(String.format("%d/%d - %d%%...", count, maxCount, progress));
						System.out.println(String.format("%d/%d - %d%%...", count, maxCount, progress));
						this.repaint();
						BufferedImage upperLayer = ImageIO.read(image);	
						if(upperLayer.getHeight() > upperLayer.getWidth()) {
							upperLayer = upperLayer.getSubimage(0, 0, upperLayer.getWidth(), upperLayer.getWidth());
						}
						else if(upperLayer.getHeight() < upperLayer.getWidth()){
							upperLayer = upperLayer.getSubimage(0, 0, upperLayer.getHeight(), upperLayer.getHeight());
						}
							
						upperLayer = resize(upperLayer, this.textureSize, this.textureSize);	
						
						BufferedImage back = upperLayer.getSubimage(0, 0, upperLayer.getWidth(), upperLayer.getHeight());
						BufferedImage lowerLayer = new BufferedImage(upperLayer.getWidth()*2, upperLayer.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
						
						lowerLayer.getGraphics().drawImage(upperLayer, 0, 0, null);
						lowerLayer.getGraphics().drawImage(upperLayer, upperLayer.getWidth(), 0, null);
						lowerLayer.getGraphics().drawImage(upperLayer, upperLayer.getWidth(), upperLayer.getHeight(), null);
						lowerLayer.getGraphics().drawImage(upperLayer, 0, upperLayer.getHeight(), null);
						
						lowerLayer = lowerLayer.getSubimage(upperLayer.getWidth()/2, upperLayer.getHeight()/2, upperLayer.getWidth(), upperLayer.getHeight());
						upperLayer = convertToARGB(upperLayer);
						
						Graphics2D g = upperLayer.createGraphics();
				        g.drawImage(lowerLayer, 0, 0, null);

				        Point point = new Point();
				        point.x = upperLayer.getWidth()/2;
				        point.y = upperLayer.getHeight()/2;
				        
				        int radius = this.textureSize/2;
				        float fractions[] = { 0.0f, this.strength, 1.0f };
				        Color colors[] = { 
				        	    new Color(0,0,0,255), 
				        	    new Color(0,0,0,255), 
				        	    new Color(0,0,0,0) 
				        	};
				        RadialGradientPaint paint = 
				            new RadialGradientPaint(point, radius, fractions, colors);
				        g.setPaint(paint);

				        g.setComposite(AlphaComposite.DstOut);
				        g.fillOval(point.x-radius, point.y-radius, this.textureSize, this.textureSize);
				        g.dispose();
							
						back.getGraphics().drawImage(upperLayer, 0, 0, null);
				        
						int normSize = textureSize/normalDivisor;
				        BufferedImage normalMap = new BufferedImage(normSize, normSize, BufferedImage.TYPE_INT_ARGB);
				        BufferedImage over = new BufferedImage(normSize, normSize, BufferedImage.TYPE_INT_ARGB);
				        
				        Graphics2D graphics = normalMap.createGraphics();
				        graphics.setPaint(new Color(128, 128, 255));
				        graphics.fillRect(0, 0, normalMap.getWidth(), over.getHeight());
				        
				        BufferedImage temp = resize(back, normSize, normSize);
						BufferedImage grayScale = new BufferedImage(normSize, normSize, BufferedImage.TYPE_BYTE_GRAY);  
							Graphics gray = grayScale.getGraphics();  
							gray.drawImage(temp, 0, 0, null); 
							gray.dispose();
				        
				        
				        for(int y=1; y<grayScale.getHeight()-1; y++) {
				        	for(int x=1; x<grayScale.getWidth()-1; x++) {
				        		
				        		float xLeft = grayScale.getRGB(x-1, y)*0.2f;
				                float xRight = grayScale.getRGB(x+1, y)*0.2f;
				                float yUp = grayScale.getRGB(x, y-1)*0.2f;
				                float yDown = grayScale.getRGB(x, y+1)*0.2f;
				                float xDelta = ((xLeft-xRight)+1)*0.2f;
				                float yDelta = ((yUp-yDown)+1)*0.2f;
				                
				                if(xDelta > 1) {
				                	xDelta = 1;
				                }else  if(xDelta < 0) {
				                	xDelta = 0;
				                }
				                
				                if(yDelta > 1) {
				                	yDelta = 1;
				                }else  if(yDelta < 0) {
				                	yDelta = 0;
				                }
				        		
				        		//Color normalColor1 = new Color(normal[0], normal[1], normal[2]);
				        		Color normalColor = new Color(xDelta, yDelta, 1.0f, yDelta);
				        		//normalMap.setRGB(x, y, (normalColor1.getRGB()));
				        		over.setRGB(x, y, (normalColor.getRGB()));
				        	}
				        }
				        
				        normalMap.getGraphics().drawImage(over, 0, 0, null);
				        normalMap.getGraphics().dispose();
				        
				        temp = invert(convertToARGB(back));
				        
				        normalMap = normalMap.getSubimage(1, 1, normalMap.getWidth()-2, normalMap.getHeight()-2);
				        
				        BufferedImage tiledNormal = new BufferedImage(normalMap.getWidth()*3, normalMap.getHeight()*3, BufferedImage.TYPE_INT_ARGB);
				        BufferedImage tiledRough = new BufferedImage(temp.getWidth()*3, temp.getHeight()*3, BufferedImage.TYPE_BYTE_GRAY);
				        
				        for(int x=0; x<3; x++) {
				        	for(int y=0; y<3; y++) {
				        		tiledNormal.getGraphics().drawImage(normalMap, normalMap.getWidth()*y, normalMap.getHeight()*x, null);
				        		tiledRough.getGraphics().drawImage(temp, temp.getWidth()*y, temp.getHeight()*x, null);
				        	}
				        }
				      
				        tiledNormal = blur(tiledNormal);				        
				        tiledNormal = resize(tiledNormal, this.textureSize*3, this.textureSize*3);
				        tiledNormal = blur(tiledNormal);
				        
				        tiledRough = blur(tiledRough);
				        
				        normalMap=tiledNormal.getSubimage(this.textureSize+1, this.textureSize+1, this.textureSize, this.textureSize);
				        temp=tiledRough.getSubimage(this.textureSize+1, this.textureSize+1, this.textureSize, this.textureSize);
				        
				        BufferedImage roughness = new BufferedImage(1024, 1024, BufferedImage.TYPE_BYTE_GRAY);
				        Graphics rough = roughness.getGraphics();  
						rough.drawImage(temp, 0, 0, null); 
						rough.dispose();
				        
						
					    Graphics2D g2d = back.createGraphics();
					    g2d.setComposite(AlphaComposite.SrcOver.derive(0.3f));
					    int x = (back.getWidth() - roughness.getWidth()) / 2;
					    int y = (back.getHeight() - roughness.getHeight()) / 2;
					    g2d.drawImage(roughness, x, y, null);
					    g2d.dispose();
					    
						File output = new File("Output");
						if (!output.exists() || !output.isDirectory()) {
							output.mkdir();
						}
						File outputfile = new File(String.format("./Output/%s_%04d_albedo.png",image.getName(), count));
						ImageIO.write(back, "png", outputfile);
						
						outputfile = new File(String.format("./Output/%s_%04d_normal.png",image.getName(), count));
						ImageIO.write(normalMap, "png", outputfile);
						
						outputfile = new File(String.format("./Output/%s_%04d_rough.png",image.getName(), count));
						ImageIO.write(roughness, "png", outputfile);
						
						++count;
					}
				}	
	}
	
	private static BufferedImage convertToARGB(BufferedImage image)
    {
        BufferedImage newImage =
            new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
	
	 private static BufferedImage resize(BufferedImage img, int height, int width) {
	        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2d = resized.createGraphics();
	        g2d.drawImage(tmp, 0, 0, null);
	        g2d.dispose();
	        return resized;
	    }
	 
	 public static BufferedImage invert(BufferedImage input) {
	        BufferedImage image = input.getSubimage(0, 0, input.getWidth(), input.getHeight());	 	
	        for (int x = 0; x < image.getWidth(); x++) {
	            for (int y = 0; y < image.getHeight(); y++) {
	                int rgba = image.getRGB(x, y);
	                Color col = new Color(rgba, true);
	                col = new Color(255 - col.getRed(),
	                                255 - col.getGreen(),
	                                255 - col.getBlue());
	                image.setRGB(x, y, col.getRGB());
	            }
	        }
	        return image;
	    }
	 
	 private static BufferedImage blur(BufferedImage proxyimage) {

			// the new image to be stored as a denoised image
			BufferedImage image2 = new BufferedImage(proxyimage.getWidth(),proxyimage.getHeight(), BufferedImage.TYPE_INT_ARGB);

			// the current position properties
			int x = 0;
			int y = 0;

			// the image width and height properties
			int width = proxyimage.getWidth();
			int height = proxyimage.getHeight();

			// loop through pixels getting neighbors and resetting colors
			for (x = 1; x < width - 1; x++) {
				for (y = 1; y < height - 1; y++) {

					// get the neighbor pixels for the transform
					Color c00 = new Color(proxyimage.getRGB(x - 1, y - 1));
					Color c01 = new Color(proxyimage.getRGB(x - 1, y));
					Color c02 = new Color(proxyimage.getRGB(x - 1, y + 1));
					Color c10 = new Color(proxyimage.getRGB(x, y - 1));
					Color c11 = new Color(proxyimage.getRGB(x, y));
					Color c12 = new Color(proxyimage.getRGB(x, y + 1));
					Color c20 = new Color(proxyimage.getRGB(x + 1, y - 1));
					Color c21 = new Color(proxyimage.getRGB(x + 1, y));
					Color c22 = new Color(proxyimage.getRGB(x + 1, y + 1));

					int r = c00.getRed() / 9 + c01.getRed() / 9 + c02.getRed() / 9
							+ c10.getRed() / 9 + c11.getRed() / 9 + c12.getRed()
							/ 9 + c20.getRed() / 9 + c21.getRed() / 9
							+ c22.getRed() / 9;

					int g = c00.getGreen() / 9 + c01.getGreen() / 9
							+ c02.getGreen() / 9 + c10.getGreen() / 9
							+ c11.getGreen() / 9 + c12.getGreen() / 9
							+ c20.getGreen() / 9 + c21.getGreen() / 9
							+ c22.getGreen() / 9;

					int b = c00.getBlue() / 9 + c01.getBlue() / 9 + c02.getBlue()
							+ c10.getBlue() / 9 + c11.getBlue() / 9 + c12.getBlue()
							/ 9 + c20.getBlue() / 9 + c21.getBlue() / 9
							+ c22.getBlue() / 9;

					//set the new rgb values
					r = Math.min(255, Math.max(0, r));
					g = Math.min(255, Math.max(0, g));
					b = Math.min(255, Math.max(0, b));

					Color c = new Color(r, g, b);
					image2.setRGB(x, y, c.getRGB());
				}
			}
			return image2;
		}
}
