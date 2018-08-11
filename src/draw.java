import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by CodeName_SB on 01-Apr-16.
 */
public class draw extends JPanel
{

    BufferedImage bi;
    int height,width;
    public draw(BufferedImage bi,int height,int width) throws Exception
    {
        this.bi = bi;
        this.height = height;
        this.width = width;
    }

    public void paint(Graphics g)
    {
        g.drawImage(bi,0,0,width,height,this);
    }
}
