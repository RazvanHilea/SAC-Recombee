package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.AddItem;
import com.recombee.api_client.api_requests.SetItemValues;
import com.recombee.api_client.exceptions.ApiException;
import com.recombee.api_client.util.Region;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

public class Main {
    public static void main(String[] args) {
        RecombeeClient client = new RecombeeClient(
                "sac-dev",
                "lfKXuspzLpxBhTUpAxCzRjRGBspCIbGxjAiglXyH48Gu18GAF654LEgqmPalDtlg"
        ).setRegion(Region.EU_WEST);

        try (CSVReader reader = new CSVReader(new FileReader("C:\\Users\\Razvycs\\IdeaProjects\\SAC\\src\\main\\resources\\target_products_dataset.csv"))) {
            List<String[]> r = reader.readAll();
            r.forEach(x -> {
                try {
                    client.send(new AddItem(x[1]));

                    Map<String, Object> values = new HashMap<>();
                    values.put("title", x[0]);
                    values.put("description", x[2]);
                    values.put("url", x[3]);
                    values.put("image", x[4]);
                    URL url = new URL(x[4]);
                    Image image = ImageIO.read(url);
                    values.put("image", image);

                    client.send(new SetItemValues(x[1], values)
                            .setCascadeCreate(false)
);
                } catch (ApiException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }
}