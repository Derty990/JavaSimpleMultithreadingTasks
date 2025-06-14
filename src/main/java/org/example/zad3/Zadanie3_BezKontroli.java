package org.example.zad3;

import java.util.ArrayList;
import java.util.List;

public class Zadanie3_BezKontroli {
    public static void main(String[] args) throws InterruptedException {
        ///Przetwarzany napis
        String text = "Hello world";

        ///Lista do przechowywania wątków
        List<Thread> threads = new ArrayList<>();

        System.out.println("######## Wersja bez kontroli");

        ///Iteracja kolejno po każdym znaku z przetwarzanego napisu. Metoda ToCharArray zamienia napis na tablicę znaków
        for (char c : text.toCharArray()){
            ///Tworzenie nowego wątku. Użyta tutaj jest klasa anonimowa, aby nie tworzyć niepotrzebnego nowego obiektu
            Thread thread = new Thread(()->{
                ///Wypisanie odpowiedniego znaku z przetwarzanego tekstu z tablicy utworzonej przez text.toCharArray()
                System.out.println(c);
                try{
                    /// Próba wywolania metody Sleep na wątku, by zwiększyć szansę na przetasowanie wątków
                    Thread.sleep(100);
                    /// Złapanie ewentualnego wyjątku jeśli coś pójdzie źle
                }catch (Exception e){
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            });
            /// Dodanie wątków do wcześniej utworzonej listy threads
            threads.add(thread);
            /// Rozpoczęcie wątku od razu
            thread.start();
        }

        /// Czekam na zakończenie wszystkich wątków
        for(Thread t : threads){
            t.join();
        }

        System.out.println("\nWNIOSEK:");
        System.out.println("Kolejność wypisywanych liter jest chaotyczna i nieprzewidywalna.");
        System.out.println("Dzieje się tak, ponieważ system operacyjny zarządza wątkami w sposób niedeterministyczny,");
        System.out.println("uruchamiając je w kolejności, którą uzna za optymalną, a nie w kolejności ich tworzenia.");
    }
}
