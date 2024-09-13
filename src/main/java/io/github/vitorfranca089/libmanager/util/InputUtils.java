package io.github.vitorfranca089.libmanager.util;

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

    public static int getIntOptions(){
        int option;
        option = in.nextInt();
        in.nextLine();
        return option;
    }

}
