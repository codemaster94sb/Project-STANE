import org.opencv.core.CvType;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

/**
 * Created by CodeName_SB on 10-Apr-16.
 */
public class conversion
{
    public static MatOfPoint getNewContourFromIndices(MatOfPoint origContour, MatOfInt indices) {
        int height = (int) indices.size().height;
        MatOfPoint2f newContour = new MatOfPoint2f();
        newContour.create(height, 1, CvType.CV_32FC2);
        for (int i = 0; i < height; ++i) {
            int index = (int) indices.get(i, 0)[0];
            double[] point = new double[] {
                    origContour.get(index, 0)[0],
                    origContour.get(index, 0)[1]
            };
            newContour.put(i, 0, point);
        }
        return convert(newContour);
    }

    public static MatOfPoint convert(MatOfPoint2f mat)
    {
        return new MatOfPoint(mat.toArray());
    }
}

