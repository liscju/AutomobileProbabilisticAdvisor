package pl.edu.agh;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import smile.Network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.*;

public class App {
    private static final Map<String, List<String>> CLIENT_PROPERTIES =
            new HashMap<String, List<String>>() {{
                put("ClientAge", Arrays.asList("YoungClient", "MidAgeClient", "OldClient"));
                put("ClientMoney", Arrays.asList("PoorClient", "MidWealthClient", "RichClient"));
                put("ClientFamily", Arrays.asList("SingleClient", "FamilyClient"));
                put("ClientLocation", Arrays.asList("BigCity", "Village"));
            }};

    public static void main( String[] args ) throws NoSuchFieldException, IllegalAccessException, URISyntaxException, IOException {
        Network network = configureApp();
        runApp(network);
    }

    private static Network configureApp() throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        System.loadLibrary("jsmile");

        String networkContent =
                IOUtils.toString(classLoader.getResourceAsStream("automobileadvisor.xdsl"));

        Network network = new Network();
        network.readString(networkContent);
        return network;
    }

    private static void runApp(Network network) {
        Scanner in = new Scanner(System.in);

        for (String clientProperty : CLIENT_PROPERTIES.keySet()) {
            System.out.println("Choose property for: " + clientProperty);
            System.out.println(CLIENT_PROPERTIES.get(clientProperty));
            System.out.println("Write number to set property for value(starts from 0)");
            int propertyIndex = in.nextInt();
            network.setEvidence(clientProperty,
                    CLIENT_PROPERTIES.get(clientProperty).get(propertyIndex));
        }

        network.updateBeliefs();

        for (int i = 0; i < network.getOutcomeCount("BugattiVeyron"); i++) {
            String parentOutcomeId = network.getOutcomeId("BugattiVeyron", i);
            double expectedUtility = network.getNodeValue("BugattiVeyron")[i];
            System.out.println(parentOutcomeId + " " + expectedUtility);
        }
    }
}
