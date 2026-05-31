package com.chatbot;

import java.util.Scanner;


public class Main {

    public static void main (String[] args) {

        System.out.println("Welcome!  What would you like to do today?");

        String input; 
        Scanner scanner = new Scanner(System.in);

        while (!(input = scanner.nextLine().trim()).matches("/exit")) {
            System.out.println("You wanted to do this right? " + input);
        }


    }
}