package pl.edu.agh;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import com.google.common.primitives.Doubles;
import smile.Network;

/**
 * Created by oem on 2016-02-01.
 */
public class GUIApp extends App {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, URISyntaxException, IOException {
        GUIApp guiApp=new GUIApp();
        Network network=configureApp();
        guiApp.setClientProperties(network);
    }

    @Override
    protected void setClientProperties(Network network) {

        setClientProperty(CLIENT_PROPERTIES.keySet().iterator(), network);

    }

    private void setClientProperty(final Iterator<String> propertiesIterator,final Network network) {
        final String clientProperty=propertiesIterator.next();
        final JFrame choiceWindow=new JFrame("Choose property for "+clientProperty);
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        final ButtonGroup bg=new ButtonGroup();
        for(String choice:CLIENT_PROPERTIES.get(clientProperty)) {
            JRadioButton rb=new JRadioButton(choice,false);
            bg.add(rb);
            rb.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(rb,BorderLayout.CENTER);
        }
        bg.getElements().nextElement().setSelected(true);

        JButton button=new JButton("OK");
        button.addActionListener(new ActionListener() {
            private Iterator<String> iterator=propertiesIterator;
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton button;
                int i=0,propertyIndex=0;

                for(Enumeration<AbstractButton> en=bg.getElements();en.hasMoreElements();) {
                    button=en.nextElement();
                    if(button.isSelected())
                        propertyIndex=i;
                }

                network.setEvidence(clientProperty,CLIENT_PROPERTIES.get(clientProperty).get(propertyIndex));
                choiceWindow.dispose();
                if(iterator.hasNext()) {
                    setClientProperty(iterator,network);
                } else {
                    writeCarMatchingResult(network);
                }
            }
        });
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(button, BorderLayout.CENTER);

        choiceWindow.add(panel);
        choiceWindow.setLocationRelativeTo(null);
        choiceWindow.setSize(200,150);
        choiceWindow.setVisible(true);
    }

    protected void writeCarMatchingResult(Network network) {
        network.updateBeliefs();
        //super.writeCarMatchingResult(network);
        final java.util.List<CarMatchingResult> carMatchingResults=getCarMatchingResults(network);
        Collections.sort(carMatchingResults, new Comparator<CarMatchingResult>() {
            @Override
            public int compare(CarMatchingResult o1, CarMatchingResult o2) {
                return Doubles.compare(o2.getBuyValue(), o1.getBuyValue());
            }
        });
        final JFrame resultsFrame=new JFrame("Models you should buy.");
        resultsFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
        DefaultListModel model=new DefaultListModel();
        for(CarMatchingResult carMatchingResult:carMatchingResults) {
            model.addElement(carMatchingResult.getName());
        }

        final JPanel resultsAndImagePane=new JPanel();

        JList resultList=new JList();
        resultList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                repaintResultAndImagePanel(resultsAndImagePane,carMatchingResults.get(e.getFirstIndex()));
                resultsFrame.repaint();
            }
        });
        resultList.setModel(model);
        JScrollPane listScroller=new JScrollPane(resultList);

        resultsFrame.add(listScroller);
        resultsFrame.add(resultsAndImagePane);
        resultsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        repaintResultAndImagePanel(resultsAndImagePane,carMatchingResults.get(0));
        resultsFrame.setSize(400,300);
        resultsFrame.setVisible(true);
    }

    private void repaintResultAndImagePanel(JPanel panel,CarMatchingResult carMatchingResult) {
        JPanel resultsPane=new JPanel();
        BufferedImage myPicture=null;

        try {
            //System.out.println(ClassLoader.getSystemResource(".").getPath());
            myPicture = ImageIO.read(new File(ClassLoader.getSystemResource("images/"+carMatchingResult.getName()+".jpg").getFile()));
        } catch(IOException e) {
            e.printStackTrace();
        }

        JLabel picLabel=new JLabel(new ImageIcon(myPicture));
        JLabel resultLabel=new JLabel("Buy value: "+Double.toString(carMatchingResult.getBuyValue())+"\tNot buy value: "+Double.toString(carMatchingResult.getBuyValue()));
        resultsPane.setLayout(new BoxLayout(resultsPane,BoxLayout.PAGE_AXIS));
        resultsPane.add(resultLabel);
        resultsPane.add(picLabel);
        panel.removeAll();
        panel.add(resultsPane);
        panel.updateUI();
    }
}
