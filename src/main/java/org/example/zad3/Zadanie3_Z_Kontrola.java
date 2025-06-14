package org.example.zad3;

import java.util.ArrayList;
import java.util.List;

public class Zadanie3_Z_Kontrola {

    /// Obiekt, który posłuży jako "zamek" (monitor) do synchronizacji wątków
    /// Tylko jeden wątek na raz może wejść do bloku zsynchronizowanego na tym obiekcie
    private static final Object lock = new Object();

    /// Współdzielona zmienna, która przechowuje informację o tym, który wątek ma teraz działać
    private static int turn = 0;

    public static void main(String[] args) throws InterruptedException {
        /// Przetwarzany napis
        String text = "Hello World";
        /// Lista do przechowywania wątków
        List<Thread> threads = new ArrayList<>();

        System.out.println("######## Wersja z kontrolą (synchronizacją)");

        /// Iteruje po napisie, tworząc dla każdego znaku osobny wątek
        for (int i = 0; i < text.length(); i++) {
            /// Przechowuje numer tury i znak dla bieżącego wątku w stałych lokalnych
            final int myTurn = i;
            final char myChar = text.charAt(i);

            /// Tworzę nowy wątek
            Thread thread = new Thread(() -> {
                /// każdy wątek próbuje wejść do bloku synchronizowanego na obiekcie 'lock'.
                synchronized (lock) {
                    try {
                        /// Pętla 'while' sprawdza, czy nadeszła kolej na ten watek
                        /// Jeśli nie (myTurn != turn), wątek zwalnia zamek i przechodzi w stan oczekiwania
                        while (myTurn != turn) {
                            lock.wait();
                        }
                        ///gdy wątek zostanie obudzony i jego warunek jest spełniony, wykonuje swoja pracę - wypisuje znak
                        System.out.print(myChar);
                        ///wątek informuje, że zakończył swoją pracę, inkrementując licznik 'turn'
                        turn++;
                        /// Budzę wszystkie inne oczekujące wątki, aby mogły sprawdzić, czy teraz ich kolej
                        lock.notifyAll();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            });
            /// dodaje stworzony wątek do listy
            threads.add(thread);
        }

        /// Uruchamiam wszystkie wątki. Od tego momentu zaczynają one rywalizować o dostęp do bloku 'synchronized'
        for (Thread thread : threads) {
            thread.start();
        }

        ///Czekam na zakończenie wszystkich wątków, aby mieć pewność, że cały napis został wypisany
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\n// WNIOSEK:");
        System.out.println("Dzięki mechanizmom synchronizacji (w tym przypadku monitora 'lock' oraz metod wait/notifyAll),");
        System.out.println("byłem w stanie wymusić konkretną kolejność wykonywania się wątków. Zmienna 'turn' działała jak");
        System.out.println("bilet, który pozwalał na pracę tylko jednemu wątkowi w danym momencie, co zapewniło poprawny napis.");
    }
}