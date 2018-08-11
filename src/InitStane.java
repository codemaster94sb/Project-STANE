import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * Created by CodeName_SB on 19-May-16.
 */
public class InitStane
{
    public static void main(String args[])
    {
        ConfigurationManager cm;
        Boolean flag=true;
        String spokentext="";
        VoiceManager vm = VoiceManager.getInstance();
        Voice v = vm.getVoice("kevin16");
        VoiceMod vmod;
        KineticMod kmod;
        int cv=0,ck=0;
        try
        {
            if(args.length > 0)
            {
                cm = new ConfigurationManager(args[0]);
            }

            else
            {
                cm = new ConfigurationManager(InitStane.class.getResource("InitStane.config.xml"));
            }

            Recognizer rcgr = (Recognizer) cm.lookup("recognizer");
            rcgr.allocate();
            v.allocate();
            v.setRate(120);

            Microphone microphone = (Microphone) cm.lookup("microphone");
            if(!microphone.startRecording()) {
                System.out.println("Cannot start microphone");
                rcgr.deallocate();
                System.exit(1);
            }

            System.out.println("Start speaking");
            while(true)
            {
                Result result = rcgr.recognize();

                if (result != null)
                {
                    spokentext = result.getBestFinalResultNoFiller();

                    if(spokentext.equals("Voice"))
                    {
                        vmod= null;
                        v.speak("Initialising Voice module");
                        if(cv==0)
                        {
                            vmod= new VoiceMod();
                            cv++;
                        }
                        if(cv>0)
                        {
                          if(vmod.t.isAlive() == true)
                          {
                              vmod = new VoiceMod();
                              cv++;
                          }
                            else
                          {
                              v.speak("The voice module is already running.");
                          }
                        }
                    }
                    else if(spokentext.equals("Kinetic"))
                    {
                        kmod = null;
                        v.speak("Initialising Kinetic module");
                        if(ck==0)
                        {
                            kmod= new KineticMod();
                            ck++;
                        }
                        if(ck>0)
                        {
                            if(kmod.t.isAlive())
                            {
                                kmod = new KineticMod();
                                ck++;
                            }
                            else
                            {
                                v.speak("The voice module is already running.");
                            }
                        }

                    }
                    /*else
                    {
                        v.speak("Command not found in the Command Dictionary");
                    }*/
                }
                else
                {
                    System.out.println("I can't hear what you said.\n");
                }

            }
        }

        catch(Exception e)
        {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }
}
