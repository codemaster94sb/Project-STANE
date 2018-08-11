import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by CodeName_SB on 03-Apr-16.
 */
public class BufferConvert
{
    MatOfByte mob;
    byte b[];
    InputStream in;
    BufferedImage bi1,bi2;

    public BufferConvert()
    {
    }

    BufferedImage convert(Mat image) throws Exception
    {
        mob = new MatOfByte();
        Imgcodecs.imencode(".jpeg",image,mob);
        b = mob.toArray();
        in= new ByteArrayInputStream(b);
        bi1 = ImageIO.read(in);
        bi2 = bi1;
        bi1 = null;
        return bi2;
    }
}
