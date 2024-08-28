package org.example;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        usersManagement users = new usersManagement(); //get the class
        Scanner scanner = new Scanner(System.in); //scanner to get inputs
        ArrayList bookmarkedUsers; // array to store bookmarked users
        int index = -1; // index for bookmaring and debookmarkin
        String name = ""; // to store the searched for name
        boolean run = true; // used to run the loops
        int currentPage = 1; // to store which page the user is in when fetching data

        while (run) { // first loop to either search or show book marked user
            System.out.print("would you like to show bookmarked users(sb) or search for a name(s)? (sb/s): ");
            String action = scanner.nextLine().trim().toLowerCase(); // get input
            index = -1; // reset index before each operation


            if ("sb".equals(action)) { // action to show book marked users
                bookmarkedUsers = users.getBookmarkedUsers(); // get the users from the file *
                if (bookmarkedUsers.isEmpty()) { // check if empty
                    System.out.println("you have no bookmarked users");
                } else { // else ask either debookmark by index or "no" to return to the main menu
                    System.out.print("would you like to debookmark any of these users? \"no\" to skip: ");
                    String deBookmarkInput = scanner.nextLine().trim().toLowerCase(); // get the index to debookmark

                    if (!deBookmarkInput.equals("no")) { // input is not "no" get the index
                        try {
                            index = Integer.parseInt(deBookmarkInput); // get the index
                            if (index >= 0 && index < bookmarkedUsers.size()) { // check if the index does exist
                                users.debookMarkUser(bookmarkedUsers, index); // debookmark the user
                            } else {
                                System.out.println("invalid index");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("invalid input, enter a number or \"no\" to skip");
                        }
                    }
                }
            } else if ("s".equals(action)) { // action to search
                name = ""; //reset the name
                while (name.isBlank()) { // make sure the name is not empty
                    System.out.println("enter a name to search for(not null) *note the search is for exact name (100 per page): ");
                    name = scanner.nextLine(); // get the name
                }

                while (run) { // second loop which has action after searching
                    ArrayList user = users.getUsers(name, currentPage); // get the first page after taking the name ^
                    System.out.print("would you like to go to the next page(n), previous page(p), save the shown results to a file(s), or show bookmarked(sb)? (n/p/s/sb/exit): ");
                    System.out.println("\nto bookmark a user use \"b:index\"");
                    String[] input = scanner.nextLine().trim().toLowerCase().split(":", 2); // split to get the action and index if there is

                    if (input.length > 2) {
                        index = Integer.parseInt(input[1].trim());
                    }

                    switch (input[0]) {
                        case "n": // add 1 and then break to get to the top of the loop and fetch the data of the next page from the api
                            if (currentPage < 10) {
                                currentPage++;
                            } else {
                                System.out.println("you are already on the last page");
                            }
                            break;
                        case "p":// deduct 1 and then break to get to the top of the loop and fetch the data of the previous page from the api
                            if (currentPage > 1) {
                                currentPage--;
                            } else {
                                System.out.println("you are already on the first page");
                            }
                            break;
                        case "sb": // action to show book marked users
                            bookmarkedUsers = users.getBookmarkedUsers();
                            if (bookmarkedUsers.isEmpty()) {
                                System.out.println("you have no bookmarked users");
                            } else { // else ask either debookmark by index or "no" to return to the main menu
                                System.out.print("would you like to debookmark any of these users? \"no\" to skip: ");
                                String deBookmarkInput = scanner.nextLine().trim().toLowerCase();
                                if (!deBookmarkInput.equals("no")) {
                                    try {
                                        index = Integer.parseInt(deBookmarkInput) - 1;
                                        if (index >= 0 && index < bookmarkedUsers.size()) {
                                            users.debookMarkUser(bookmarkedUsers, index);
                                            System.out.println("user has been debookmarked");
                                        } else {
                                            System.out.println("invalid index");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("invalid input, enter a number or \"no\" to skip");
                                    }
                                }
                            }
                            break;
                        case "s": // action to save the users to a file
                            users.saveUsers(user);
                            break;
                        case "b": // action to bookmars
                            users.bookMarkUser(user, index);
                            break;
                        case "exit":
                            run = false;
                            System.out.println("exiting the program...");
                            break;
                        default:
                            System.out.println("invalid input, use n, p, s, sb or exit.");
                    }
                }
            } else {
                System.out.println("invalid input, use 'sb' or 's'");
            }
        }
        scanner.close();
    }
}

