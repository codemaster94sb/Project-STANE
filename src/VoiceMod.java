import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by CodeName_SB on 20-May-16.
 */
public class VoiceMod implements Runnable
{
    ConfigurationManager cm;
    Recognizer rcgr;
    Boolean flag;
    String spokentext;
    VoiceManager vm ;
    Voice v;
    Thread t;
    Process p;
    File f;
    String dir;
    public VoiceMod()
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

            // start the microphone or exit if the programm if this is not possible
            Microphone microphone = (Microphone) cm.lookup("microphone");
            if (!microphone.startRecording()) {
                System.out.println("Cannot start microphone.");
                rcgr.deallocate();
                System.exit(1);
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception: "+e);
        }
        t.start();
    }

    public void run()
    {
        try
        {
            while (true) {
                if(flag.equals(true))
                {
                    System.out.println("Start speaking. Press Ctrl-C to quit.\n");
                    //p=new ProcessBuilder("cmd","/c","start","C:\\Users\\Storm\\Desktop\\pic.jpg").start();

                    Result result = rcgr.recognize();

                    if (result != null)
                    {
                        spokentext = result.getBestFinalResultNoFiller();
                    }
                    else
                    {
                        System.out.println("I can't hear what you said.\n");
                    }
                    if(spokentext.equals("open my computer") || spokentext.equals("view my computer"))
                    {
                        //Process p;
                        v.speak("Giving a View of Your Computer");
                        p = new  ProcessBuilder("cmd", "/c", "start", "explorer.exe").start();
                        System.out.println("You said: " + spokentext + "\n");
                        dir=spokentext;
                        f = new File(dir);

                        if(f.isDirectory()) {
                            System.out.println("Structure of " + dir + " : \n");
                            List<String> l = new ArrayList<>();
                            String s[]=f.list();

                            for(int i=0;i<s.length;i++)
                            {
                                l.add(s[i]);
                            }

                            Iterator<String> q = l.iterator();
                            while(q.hasNext()) {
                                int i = 1;
                                String m = q.next();
                                System.out.println(m);
                            }
                        }
                        else
                        {  System.out.println("Its not a directory..");    }
                    }
                    else if(spokentext.equals("open program files") || spokentext.equals("view program files"))
                    {
                        v.speak("Showing Program Files.");
                        p = new ProcessBuilder("cmd", "/c", "start", "explorer.exe", "/root,","\"C:\\Program Files (x86)\"").start();
                        System.out.println("You said: " + spokentext + "\n");
                        dir="C:\\Program Files (x86)\\";
                        f = new File(dir);

                        if(f.isDirectory()) {
                            System.out.println("Structure of " + dir + " : \n");
                            List<String> l = new ArrayList<>();
                            String s[]=f.list();

                            for(int i=0;i<s.length;i++)
                            {
                                l.add(s[i]);
                            }

                            Iterator<String> q = l.iterator();
                            while(q.hasNext()) {
                                int i = 1;
                                String m = q.next();
                                System.out.println(m);
                            }
                        }
                        else
                        {  System.out.println("Its not a directory..");    }

                    }
                    else if(spokentext.equals("open browser"))
                    {
                        //Process p;
                        v.speak("Opening Browser. It may take some time, please be patient");
                        p= new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe").start();
                        System.out.println("You said: " + spokentext + "\n");
                        dir="C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
                        f = new File(dir);

                        if(f.isDirectory()) {
                            System.out.println("Structure of " + dir + " : \n");
                            List<String> l = new ArrayList<>();
                            String s[]=f.list();

                            for(int i=0;i<s.length;i++)
                            {
                                l.add(s[i]);
                            }

                            Iterator<String> q = l.iterator();
                            while(q.hasNext()) {
                                int i = 1;
                                String m = q.next();
                                System.out.println(m);
                            }
                        }
                        else
                        {  System.out.println("Its not a directory..");    }

                    }
                    /*else if(spokentext.equals("babaji ka thullu"))
                    {
                        v.speak("Giving a Babaji Kaa Thullu");
                        p=new ProcessBuilder("cmd","/c","start","C:\\Users\\Storm\\Desktop\\pic.jpg\\"+).start();
                        System.out.println("You said: " + spokentext + "\n");
                    }*/
                    else if(spokentext.equals("open face book"))
                    {
                        //Process p;
                        v.speak("Opening Facebook. It may take sometime, Please be patient. Have a Happy time Socialising.");
                        p= new ProcessBuilder("cmd","/c","start","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe","www.facebook.com").start();
                        System.out.println("You said: " + spokentext + "\n");
                        dir=spokentext;
                        f = new File(dir);

                        if(f.isDirectory()) {
                            System.out.println("Structure of " + dir + " : \n");
                            List<String> l = new ArrayList<>();
                            String s[]=f.list();

                            for(int i=0;i<s.length;i++)
                            {
                                l.add(s[i]);
                            }

                            Iterator<String> q = l.iterator();
                            while(q.hasNext()) {
                                int i = 1;
                                String m = q.next();
                                System.out.println(m);
                            }
                        }
                        else
                        {  System.out.println("Its not a directory..");    }

                    }
                    else if(spokentext.equals("Exit Voice Module"))
                    {
                        v.speak("Concluding Voice Module");
                        flag=false;
                    }
      
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception: "+e);
        }
    }
}
