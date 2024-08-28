package org.example;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.time.*;
import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;
import java.nio.file.*;

public class usersManagement { // class that contains all user actions (getting the users, saving to file, bookmark, debookmark, show bookmarked)

    public ArrayList getUsers(String name, int page) {
        // this function will fetch the data from the created link and print them out, the key is stored in a .env file *
        Dotenv dotenv = Dotenv.configure().directory("./src/main/java/org/example").load();
        Gson gson = new Gson(); // **
        HttpResponse<String> getresponse = null;
        JsonArray items = null;
        ArrayList usersStrings = null;

        try {
            URL userurl = new URL("https://api.stackexchange.com/2.3/users?inname=" + name +
                    "&site=stackoverflow" + "&key=" + dotenv.get("auth") +
                    "&filter=!BTeL*ManaQaxU16Q3OS0rS7qq2eqUC&pagesize=100&page=" + page);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(userurl.toString()))
                    .GET()
                    .build();

            getresponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject response = gson.fromJson(getresponse.body(), JsonObject.class);
            items = response.getAsJsonArray("items");
            usersStrings = new ArrayList();
            String location = "";
            for (int i = 0; i < items.size(); i++) {
                JsonObject firstUser = items.get(i).getAsJsonObject();
                String displayName = firstUser.get("display_name").getAsString();
                String userType = firstUser.get("user_type").getAsString();
                location = (firstUser.get("location") != null) ? firstUser.get("location").getAsString() : "not found";
                int userId = firstUser.get("user_id").getAsInt();
                int reputation = firstUser.get("reputation").getAsInt();
                int remaining = response.get("quota_remaining").getAsInt();
                int lastAccessedDateUnix = firstUser.get("last_access_date").getAsInt();
                int accountId = firstUser.get("account_id").getAsInt();
                Date lastAccessedData = Date.from(Instant.ofEpochSecond(lastAccessedDateUnix));
                String formatedDate = new SimpleDateFormat("dd-MM-yyyy").format(lastAccessedData);
                usersStrings.add(userId + "\t" + accountId + "\t" + displayName +"\t" + reputation + "\t" + location + "\t" + userType + "\n");
                System.out.println(i + "- " + "name: " + displayName + ", user id: " + userId + ", reputation " + reputation + ", " + "last accessed data: " + formatedDate + " - " + remaining);

            }
            return usersStrings;
        } catch (Exception e) {
            System.out.println("error while fetching data " + getresponse.statusCode());
        }
        return usersStrings;
    }

    public static void saveUsers(ArrayList<String> users) throws IOException {
        // this function will take the fetched users and write appends them to the file while also modifying the counters
        System.out.println("Saving users...");

        Path path = Paths.get("./src/main/java/org/example/savedusers.sofusers");

        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        StringBuilder fileContent = new StringBuilder();
        String firstLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
            firstLine = br.readLine();

            if (firstLine == null) {
                firstLine = "0\t0";
            }

            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }

        String[] counters = firstLine.split("\t");
        int counter1 = Integer.parseInt(counters[0]) + 100;
        int counter2 = Integer.parseInt(counters[1]) + 1;
        firstLine = counter1 + "\t" + counter2;

        try {
            FileWriter fw = new FileWriter(path.toFile());
            fw.write(firstLine + "\n");
            fw.write(fileContent.toString());

            for(int i = 0; i < users.size(); i++) {
                fw.write(users.get(i).toString());
            }
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException("error writing to file", e);
        }
        System.out.println("users saved successfully");
    }

    public static void bookMarkUser(ArrayList<String> users, int userIndex) throws IOException {
        // this function appends to the bookmark file
        Path path = Paths.get("./src/main/java/org/example/bookmarked.txt");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        try {
            FileWriter fw = new FileWriter(path.toFile(), true);
            fw.write(users.get(userIndex).toString());
            System.out.println("bookmarked successfully");
            fw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void debookMarkUser(ArrayList<String> users, int userIndex){
        // this function removes the selected index from the file
        Path path = Paths.get("./src/main/java/org/example/bookmarked.txt");
        String userToRemove = users.get(userIndex);

        StringBuilder fileContent = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals(userToRemove.trim())) {
                    fileContent.append(line + "\n");
                }
            }
            br.close();
        } catch (Exception e) {
            throw new RuntimeException("error reading file", e);
        }

        try {
            FileWriter fw = new FileWriter(path.toFile());
            fw.write(fileContent.toString());
            fw.close();
        } catch (Exception e) {
            throw new RuntimeException("Error writing file", e);
        }
        System.out.println("debookmarked successfully");
    }

    public ArrayList getBookmarkedUsers() throws IOException { // this function gets the bookmarked users from a file *
        ArrayList bookmarkedUsers = new ArrayList();
        String line;
        Path path = Paths.get("./src/main/java/org/example/bookmarked.txt");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
            int i = 0;
            while ((line = br.readLine()) != null) {
                bookmarkedUsers.add(line);
                System.out.println(i + "-" + line);
                i++;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return bookmarkedUsers;
    }
}
