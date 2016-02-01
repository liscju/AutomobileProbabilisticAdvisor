package pl.edu.agh;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
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
    private static final List<String> LIST_OF_AVAILABLE_CARS =
            Arrays.asList("BugattiVeyron", "MacerattiGranTurismo", "Maybach_57S",
                          "HammerH3", "KiaCedd", "Fiat_125p", "Renault_Kangoo");

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
        setClientProperties(network);

        network.updateBeliefs();

        writeCarMatchingResult(network);
    }

    private static void setClientProperties(Network network) {
        Scanner in = new Scanner(System.in);
        for (String clientProperty : CLIENT_PROPERTIES.keySet()) {
            System.out.println("Choose property for: " + clientProperty);
            System.out.println(CLIENT_PROPERTIES.get(clientProperty));
            System.out.println("Write number to set property for value(starts from 0)");
            int propertyIndex = in.nextInt();
            network.setEvidence(clientProperty,
                    CLIENT_PROPERTIES.get(clientProperty).get(propertyIndex));
        }
    }

    private static void writeCarMatchingResult(Network network) {
        List<CarMatchingResult> carMatchingResults = getCarMatchingResults(network);
        Collections.sort(carMatchingResults, new Comparator<CarMatchingResult>() {
            @Override
            public int compare(CarMatchingResult o1, CarMatchingResult o2) {
                return Doubles.compare(o1.getBuyValue(), o2.getBuyValue());
            }
        });
        Collections.reverse(carMatchingResults);
        for (CarMatchingResult carMatchingResult : carMatchingResults) {
            System.out.println(carMatchingResult);
        }
    }

    private static List<CarMatchingResult> getCarMatchingResults(Network network) {
        List<CarMatchingResult> carMatchingResults = new LinkedList<>();
        for (String car : LIST_OF_AVAILABLE_CARS) {
            Double buyValue = null;
            Double notBuyValue = null;
            for (int i = 0; i < network.getOutcomeCount(car); i++) {
                String outcomeName = network.getOutcomeId(car, i);
                double outcomeValue = network.getNodeValue(car)[i];
                if ("Buy".equals(outcomeName)) {
                    buyValue = outcomeValue;
                }
                else if ("NotBuy".equals(outcomeName)) {
                    notBuyValue = outcomeValue;
                }
            }

            if (buyValue == null || notBuyValue == null) {
                throw new UnsupportedOperationException("Car should have in outcome values Buy and NotBuy");
            }

            carMatchingResults.add(new CarMatchingResult(car, buyValue, notBuyValue));
        } return carMatchingResults;
    }
}
