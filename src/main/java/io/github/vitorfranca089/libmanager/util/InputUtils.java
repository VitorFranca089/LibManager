package io.github.vitorfranca089.libmanager.util;

import java.time.LocalDateTime;
import java.util.InputMismatchException;
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
        return getInt(null);
    }

    public static int getInt(String prompt){
        int option = 0;
        boolean validInput = false;

        do{
            try{
                if(prompt != null) System.out.println(prompt);
                option = in.nextInt();
                in.nextLine();
                validInput = true;
            }catch(InputMismatchException e) {
                System.out.println("Digite um número inteiro.");
                System.out.println();
                in.nextLine();
            }
        }while(!validInput);

        return option;
    }

    public static int getIntYear(String prompt){
        int year = -1;
        boolean isValid = false;

        do{
            try{
                System.out.println(prompt);
                year = in.nextInt();
                in.nextLine();
                if(year > 0 && year <= LocalDateTime.now().getYear()){
                    isValid = true;
                }else{
                    System.out.println("Insira um ano válido.");
                    System.out.println();
                }
            }catch(InputMismatchException e){
                System.out.println("Entrada inválida. Insira um ano como um número inteiro.");
                System.out.println();
                in.nextLine();
            }
        }while(!isValid);

        return year;
    }

    public static char getCharOptions(String prompt){
        System.out.println(prompt);
        return in.nextLine().toUpperCase().charAt(0);
    }

}
