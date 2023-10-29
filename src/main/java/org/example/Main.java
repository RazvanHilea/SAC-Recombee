package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.*;
import com.recombee.api_client.bindings.DeleteMoreItemsResponse;
import com.recombee.api_client.bindings.Item;
import com.recombee.api_client.bindings.Recommendation;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.exceptions.ApiException;
import com.recombee.api_client.util.Region;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws ApiException {
        RecombeeClient client = new RecombeeClient(
                "sac-dev",
                "lfKXuspzLpxBhTUpAxCzRjRGBspCIbGxjAiglXyH48Gu18GAF654LEgqmPalDtlg"
        ).setRegion(Region.EU_WEST);

//        addItemsToDB(client);

        registerPurchases(client, "user1", 3);
        registerPurchases(client, "user2", 10);
        registerPurchases(client, "user3", 20);

        RecommendationResponse recommended = client.send(new RecommendItemsToUser("user1", 5));
        for(Recommendation rec: recommended) System.out.println(rec.getId());

    }

    private static void addItemsToDB(RecombeeClient client) {
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

                    client.send(new SetItemValues(x[1], values)
                            .setCascadeCreate(true)
                    );
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private static Item[] getItemsFromDB(RecombeeClient client, int itemCount) {
        try {
            return client.send(new ListItems().setCount(itemCount));
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerPurchases(RecombeeClient client, String userId, int itemCount) throws ApiException {

        Item[] result = getItemsFromDB(client, itemCount);

        for (Item r: result) {
            Request req = new AddPurchase(userId,
                    r.getItemId())
                    .setCascadeCreate(true);
            client.send(req);
        }
    }
}