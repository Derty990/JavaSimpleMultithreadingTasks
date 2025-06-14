package org.example.zad6;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Zadanie6 {

    public static void main(String[] args) {
        /// Pobieram dane od użytkownika
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number for factorial: ");
        int n = scanner.nextInt();

        System.out.print("Enter number of threads: ");
        int threadCount = scanner.nextInt();
        scanner.close();

        if (n < 0 || threadCount <= 0) {
            System.out.println("Numbers must be positive.");
            return;
        }

        System.out.printf("Calculating %d! using %d threads...\n", n, threadCount);
        long startTime = System.nanoTime();

        /// tworzę pule wątków, która będzie wykonywać moje zadania
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        /// Lista, w której będę trzymać "obietnice" wyników z każdego wątku
        List<Future<BigInteger>> futureResults = new ArrayList<>();

        /// dzielę pracę na mniejsze kawałki dla każdego wątku
        long step = n / threadCount;
        for (int i = 0; i < threadCount; i++) {
            /// Ustalam, od jakiej do jakiej liczby dany wątek ma mnożyc
            long start = i * step + 1;
            long end = (i == threadCount - 1) ? n : (i + 1) * step;

            /// Zabezpieczenie dla 0! i 1!
            if (n < 2) {
                start = 1;
                end = 1;
            }

            /// Tworzę zadanie dla wątku przy pomocy lambdy.
            /// Zmienne dla lambdy muszą być "final".
            ///W tym przypadku są effectively final i nie wymagają słowa kluczowego "final" przed inicjalizacją zmiennej
            /// ponieważ są zdefiniowane w scope pętli, a ich wartość się nie zmienia
            long finalStart = start;
            long finalEnd = end;
            Callable<BigInteger> task = () -> {
                BigInteger partialResult = BigInteger.ONE;
                for (long j = finalStart; j <= finalEnd; j++) {
                    partialResult = partialResult.multiply(BigInteger.valueOf(j));
                }
                return partialResult;
            };

            /// Wrzucam zadanie do wykonania i dodaję "obietnicę" wyniku do listy
            futureResults.add(executor.submit(task));

            ///dla n < 2 przerywam pętlę, bo jeden wątek wystarczy
            if (n < 2) break;
        }

        /// Zbieram wyniki cząstkowe od wszystkich wątków
        ///Inicjalizacja BigInteger na wartość = 1
        BigInteger finalResult = BigInteger.ONE;
        try {
            for (Future<BigInteger> future : futureResults) {
                /// .get() czeka na wynik z danego wątku i go zwraca
                finalResult = finalResult.multiply(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            /// Zamykam pule wątków po skończonej pracy
            executor.shutdown();
        }

        long endTime = System.nanoTime();
        double durationMs = (double) (endTime - startTime) / 1_000_000.0;

        System.out.println("Result: " + finalResult);
        ///wynik wyswietlany do 4 cyfr po przecinku
        System.out.printf("Calculation time: %.4f ms\n", durationMs);
    }
}