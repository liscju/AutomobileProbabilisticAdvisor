package pl.edu.agh;

import org.apache.commons.io.IOUtils;
import smile.Network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

public class App {
    public static void main( String[] args ) throws NoSuchFieldException, IllegalAccessException, URISyntaxException, IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        System.loadLibrary("jsmile");

        String networkContent =
                IOUtils.toString(classLoader.getResourceAsStream("automobileadvisor.xdsl"));

        Network network = new Network();
        network.readString(networkContent);

        System.out.println("List of all nodes:");
        for (String arg : network.getAllNodeIds()) {
            System.out.println(arg);
        }
        System.out.println("End of list of all nodes");

        network.setEvidence("ClientAge", "YoungClient");
        network.setEvidence("ClientMoney", "RichMoney");
        network.setEvidence("ClientFamily", "SingleClient");
        network.setEvidence("ClientLocation", "BigCity");

        network.updateBeliefs();

        for (int i = 0; i < network.getOutcomeCount("BugattiVeyron"); i++) {
            String parentOutcomeId = network.getOutcomeId("BugattiVeyron", i);
            double expectedUtility = network.getNodeValue("BugattiVeyron")[i];
            System.out.println("");
        }
    }
}
