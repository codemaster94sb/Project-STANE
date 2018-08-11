import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.List;

/**
 * Created by CodeName_SB on 20-May-16.
 */
public class KineticMod implements Runnable
{
    ConfigurationManager cm;
    Boolean flag=true;
    String spokentext="";
    VoiceManager vm;
    Voice v;
    Recognizer rcgr;
    Thread t;
    Mat frame= new Mat(),imggray,diff,frame1,diff2;
    double areaMin = 1,cdepth=0,defangle,mindepth=30.0,mfdepth=40.0, mfangle=120.0;
    double areaC=200,areaC1=0;
    double posX=0, posY=0;
    int m=0, size=0;
    int c=1;
    int i,row,col;
    Scalar color;
    Point[] contourpts;
    Point start,end,inner;
    List<MatOfPoint> contour1,hull;
    MatOfInt chulls ;
    MatOfInt4 defects;
    MatOfPoint chull;
    Mat heirarchy;
    Moments expt;
    JFrame result, orig,hsv,thresh;
    JLabel h_label,s_label,v_label;
    VideoCapture vc;
    BufferConvert image;
    JSlider h_slide,s_slide,v_slide;

    JPanel panel;


    public KineticMod()
    {
        vm = VoiceManager.getInstance();
        v = vm.getVoice("kevin16");
        t = new Thread(this);
        flag=true;
        spokentext="";
        try {
            cm = new ConfigurationManager(VoiceMod.class.getResource("VoiceMod.config.xml"));
            rcgr = (Recognizer) cm.lookup("recognizer");
            rcgr.allocate();
            v.allocate();
            v.setRate(120);
            v.setPitch(125);
            Microphone microphone = (Microphone) cm.lookup("microphone");
            if (!microphone.startRecording()) {
                System.out.println("Cannot start microphone.");
                rcgr.deallocate();
                System.exit(1);
            }
            result = new JFrame("Final Result");
            orig = new JFrame("Original");
            hsv = new JFrame("HSV");
            heirarchy=new Mat();
            thresh = new JFrame("Thresh");
            hull = new ArrayList<>();
            contour1=new ArrayList<>();
            defects= new MatOfInt4();
            chulls = new MatOfInt();
            color = new Scalar(255,255,255);
            result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            image = new BufferConvert();
            Robot robot = new Robot();
            vc = new VideoCapture();
            vc.open(0);
            vc.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
            vc.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
            vc.set(Videoio.CAP_PROP_FPS, 30);
            vc.set(Videoio.CAP_PROP_AUTO_EXPOSURE, 1);
            //vc.set(Videoio.CAP_MODE_RGB,1);

            imggray = Mat.zeros((int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH), 1);
            result.setSize((int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH), (int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT));
            orig.setSize((int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH), (int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT));
            diff = Mat.zeros((int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH), CvType.CV_8UC1);
            diff2 = Mat.zeros((int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH), 1);
            //System.out.println(vc.get(Videoio.CAP_PROP_FRAME_WIDTH)+"x"+vc.get(Videoio.CAP_PROP_FRAME_HEIGHT));
            frame1 = Mat.zeros((int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH), CvType.CV_8UC1);
        }
        catch(Exception e)
        {
            System.out.println("Exception: "+e);
        }

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        h_label = new JLabel("",JLabel.LEFT);
        s_label = new JLabel("",JLabel.CENTER);
        v_label = new JLabel("",JLabel.RIGHT);

        h_slide = new JSlider(JSlider.HORIZONTAL,0,100,20);
        s_slide = new JSlider(JSlider.HORIZONTAL,0,100,20);
        v_slide = new JSlider(JSlider.HORIZONTAL,0,100,20);

        h_slide.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                h_label.setText("H: "+((JSlider)e.getSource()).getValue());
            }
        });

        s_slide.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                s_label.setText("S: "+((JSlider)e.getSource()).getValue());
            }
        });

        v_slide.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                v_label.setText("V: "+((JSlider)e.getSource()).getValue());
            }
        });
        orig.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.add(h_slide);
        panel.add(s_slide);
        panel.add(v_slide);

        orig.setLayout(new GridLayout(3,3));
        orig.add(h_label);
        orig.add(s_label);
        orig.add(v_label);
        orig.add(panel);
        orig.setVisible(true);

        t.start();
    }



    public void run()
    {
        try {
            while (true) {
                if (flag.equals(true))
                {
                    v.speak("The Kinetic Sensing Module has been initialised.");
                    System.out.println("To exit say 'Exit Kinetic Module'\n");
                    //p=new ProcessBuilder("cmd","/c","start","C:\\Users\\Storm\\Desktop\\pic.jpg").start();

                    Result res = rcgr.recognize();

                    if (res != null)
                    {
                        spokentext = res.getBestFinalResultNoFiller();
                    }
                    else
                    {
                        System.out.println("I can't hear what you said.\n");
                    }

                    chull = null;
                    vc.read(frame);
                    Core.flip(frame, frame, 1);
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
                    //Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2RGB);
                    draw image1 = new draw(image.convert(frame), (int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH));
                    hsv.setContentPane(image1);
                    hsv.setVisible(true);
                    Imgproc.blur(frame, frame, new Size(10, 10));
                    //Imgproc.GaussianBlur(frame,frame,new Size(3,3),1,1);
                    //Imgproc.medianBlur(frame,frame1,1);
                    //Core.inRange(frame,new Scalar(40,50,75),new Scalar(95,70,85),frame1);
                    //Core.inRange(frame,new Scalar(100,75,55),new Scalar(120,95,75),frame1);
                    Core.inRange(frame, new Scalar(h_slide.getValue() - 10, s_slide.getValue() - 10, v_slide.getValue() - 10), new Scalar(h_slide.getValue() + 10, s_slide.getValue() + 10, v_slide.getValue() + 10), diff);
                    //Core.inRange(frame,new Scalar(200,30,42),new Scalar(250,60,70),frame1);
                    //Imgproc.adaptiveThreshold(diff, diff, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
                    Imgproc.threshold(diff, diff, 160, 255, Imgproc.THRESH_BINARY_INV);
                    draw image2= new draw(image.convert(frame),(int)vc.get(Videoio.CAP_PROP_FRAME_HEIGHT),(int)vc.get(Videoio.CAP_PROP_FRAME_WIDTH));
                    thresh.setContentPane(image2);
                    thresh.setVisible(true);

                    Imgproc.findContours(diff, contour1, heirarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);


                    // areaC = 0;

                    size = (int) contour1.size();
                    System.out.println("Size" + size);
                    areaC = Imgproc.contourArea(contour1.get(0));
                    for (i = 1; i < size; i++)
                    {
                        areaC1 = Imgproc.contourArea((contour1.get(i)));

                        if ((areaC1 >= areaC)) {
                            areaC = areaC1;
                            m = i;
                        }

                    }


                    Imgproc.drawContours(diff, contour1, m, color, 1);
                    Imgproc.convexHull(contour1.get(m), chulls);
                    chull = conversion.getNewContourFromIndices(contour1.get(m), chulls);
                    Imgproc.convexityDefects(contour1.get(m), chulls, defects);
                    hull.add(chull);
                    Imgproc.drawContours(diff, hull, 0, color, 4);

                    if (defects.size().width > 0) {
                        hull.add(chull);
                        Imgproc.drawContours(diff, hull, 0, color, 4);
                    }



                    expt = Imgproc.moments(diff);
                    if (cdepth < mindepth) {
                        posX = expt.get_m10() / expt.get_m00();
                        posY = expt.get_m01() / expt.get_m00();
                        // Imgproc.floodFill(diff,new Mat(),new Point(posX,posY),new Scalar(170,40,50));
                        Imgproc.rectangle(diff, new Point(posX - 10, posY - 10), new Point(posX + 10, posY + 10), new Scalar(255, 255, 255), 2);

                        draw image3 = new draw(image.convert(diff), (int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH));
                        result.setContentPane(image3);
                        result.setVisible(true);
                        c++;
                        hull.remove(chull);
                        //robot.mouseMove((int)posX,(int)posY);
                        //}
                        frame.release();
                        imggray.release();
                        //diff.release();
                        heirarchy.release();
                    }

                    if (spokentext.equals("Exit Kinetic module"))
                    {
                        flag = false;
                    }
                }
            }
        }

        catch(Exception e) {
                    System.out.println("Exception: "+e);
        }

    }
}
