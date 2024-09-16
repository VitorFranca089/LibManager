package io.github.vitorfranca089.libmanager.util;

import java.time.LocalDateTime;
import java.util.Scanner;

public class InputUtils {

    private static Scanner in = new Scanner(System.in);

    public static String getStringNotBlank(String prompt){
        String response;
        do{
            System.out.println(prompt);
            response = in.nextLine();
        }while(response.isBlank());
        return response;
    }

    public static String getString(String prompt){
        System.out.println(prompt);
        return in.nextLine();
    }

    public static int getInt(){
        int option;
        option = in.nextInt();
        in.nextLine();
        return option;
    }

    public static int getInt(String prompt){
        int option;
        System.out.println(prompt);
        option = in.nextInt();
        in.nextLine();
        return option;
    }

    public static int getIntYear(String prompt){
        int year;
        do{
            System.out.println(prompt);
            year = in.nextInt();
            in.nextLine();
        }while(year > 0 && year >= LocalDateTime.now().getYear());
        return year;
    }

    public static char getCharOptions(String prompt){
        System.out.println(prompt);
        return in.nextLine().toUpperCase().charAt(0);
    }

}
