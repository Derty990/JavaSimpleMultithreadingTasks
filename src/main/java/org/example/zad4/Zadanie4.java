package org.example.zad4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Zadanie4 {

    private static final int N = 18;

    /**
     * Metoda wykonująca intensywne obliczenia, aby celowo obciążyć jeden rdzeń procesora.
     * Jest to bezpośrednie przeniesienie logiki z przykładu w C++.
     */
    public static void count() {
        int x;
        for (int j = 0; j < 3; j++) {
            x = 18273;
            ///Pętla wykonuje prawie 1.9 miliarda operacji mnożenia, co jest zadaniem czysto obliczeniowym. Litera L oznacza typ long
            for (long i = 0; i < 0x70000000L; i++) {
                x *= 12739;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        /// Pobieram i wyświetlam liczbe dostępnych dla Javy procesorów logicznych (rdzeni/wątków)
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Komputer posiada: " + availableProcessors + " rdzeni/wątków logicznych.");
        System.out.println("Uruchamiam test dla N = " + N + " wątków.");

        /// Zapisuję czas startu w nanosekundach dla jak największej precyzji
        long start = System.nanoTime();
        /// Tworzę listę do przechowywania referencji do moich wątków
        List<Thread> threads = new ArrayList<>();

        /// Pętla tworząca i uruchamiająca N wątków
        for (int i = 0; i < N; i++) {
            /// Tworzę nowy wątek, przekazując mu metodę `count` do wykonania
            Thread thread = new Thread(Zadanie4::count);
            /// Dodaję wątek do listy
            threads.add(thread);
            /// Uruchamiam wątek
            thread.start();
        }

        /// Główny wątek programu musi poczekać aż wszystkie stworzone wątki zakończą swoją prac
        /// Iteruj po liście i na każdym wątku wywołuję metodę join()
        for (Thread thread : threads) {
            thread.join();
        }

        /// Zapisuje czas zakończenia pracy wszystkich wątków
        long stop = System.nanoTime();
        /// Obliczam różnice czasu i konwertuję ją na sekundy
        double durationSeconds = (double)(stop - start) / TimeUnit.SECONDS.toNanos(1);

        /// Wywwietlam wynik pomiaru do 4 liczb po przecinku
        System.out.printf("Czas wykonania: %.4f s\n", durationSeconds);
    }
}