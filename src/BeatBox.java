import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by adavi on 07.09.2017.
 */
public class BeatBox {

    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    JTextField userMessage;
    ArrayList <JCheckBox> checkBoxeList;
    int nextNum;
    Vector <String> listVector = new Vector<String>();
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();

    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;

    String [] instrumentNames = {"Bass Drum", "Closed HI-Hat", "Open Hi-Hat",
            "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
            "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom",
            "High Agogo", "Open Hi Conga"};

    int [] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

    public void startUp(String name) {
        this.userName = name;
        try{
            Socket sock = new Socket("127.0.0.1",5000);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        }catch(Exception e){
            System.out.println("couldn't connect -- you will have to play alone");
        }
        setUpMidi();
        buildGUI();
    }

    private void buildGUI() {
        theFrame = new JFrame("Cyber BeatBox");
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkBoxeList = new ArrayList<JCheckBox>();

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        JButton start  = new JButton("Start");
        start.addActionListener(new MyStartListner());
        buttonBox.add(start);


        JButton stop  = new JButton("Stop");
        start.addActionListener(new MyStoptListner());
        buttonBox.add(stop);



        JButton upTempo  = new JButton("Tempo Up");
        start.addActionListener(new MyUpTempoListner());
        buttonBox.add(upTempo);

        JButton downTempo  = new JButton("Tempo Down");
        start.addActionListener(new MyDownTempoListner());
        buttonBox.add(downTempo);

        JButton sendId = new JButton("Send id");
        sendId.addActionListener(new MySendIdListner());
        buttonBox.add(sendId);

        userMessage = new JTextField();
        buttonBox.add(userMessage);

        incomingList = new JList();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++){
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BoxLayout.EAST, buttonBox);
        background.add(BoxLayout.WEST, buttonBox);

        theFrame.getContentPane().add(background);
        GridLayout grit = new GridLayout(16,16);
        grit.setVgap(1);
        grit.setVgap(2);
        mainPanel = new JPanel(grit);
        background.add(BoxLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++){
            JCheckBox c  = new JCheckBox();
            c.setSelected(false);
            checkBoxeList.add(c);
            mainPanel.add(c);
        }
        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);

    }
    public void setUpMidi(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence =  new Sequence(Sequence.PPQ,4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}