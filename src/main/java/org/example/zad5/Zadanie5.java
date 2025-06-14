package org.example.zad5;

import java.io.IOException;
import java.util.Scanner;

public class Zadanie5 {

    /// Stała określająca czas na cały egzamin w sekundach (dla przyspieszenia symulacji)
    private static final int TOTAL_TIME_SECONDS = 15;
    /// Stała określająca czas na odpowiedź na pojedyncze pytanie w milisekundach
    private static final int TIME_PER_QUESTION_MS = 3000;

    ///Współdzielona flaga informująca o końcu całego egzaminu
    /// Słowo kluczowe 'volatile' zapewnia, że zmiana wartości tej zmiennej
    ///przez jeden watek będzie natychmiast widoczna dla drugiego
    private static volatile boolean totalTimeUp = false;

    public static void main(String[] args) {
        /// --- Wątek odliczający całkowity czas egzaminu ---
        Thread examTimerThread = new Thread(() -> {
            try {
                /// Ten wątek po prostu "śpi" przez cały czas trwania egzaminu
                Thread.sleep(TOTAL_TIME_SECONDS * 1000L);
                /// Po obudzeniu ustawia flagę na true, co zakończy główną pętlę
                totalTimeUp = true;
                System.out.println("\n\n!!! CZAS CAŁKOWITY NA EGZAMIN UPŁYNĄŁ !!!");
            } catch (InterruptedException e) {
                /// Przerwanie wątku oznacza natychmiastowe zakończenie
                Thread.currentThread().interrupt();
            }
        });
        examTimerThread.start(); /// Uruchamiam ten wątek w tle

        /// --- Główny wątek programu (obsługa pytań) ---
        /// Scaner służy do wczytywania inputu
        Scanner scanner = new Scanner(System.in);
        int score = 0;
        int questionNumber = 1;

        System.out.printf("Rozpoczynasz egzamin. Masz %d sekund. Na każde pytanie masz %d sekund.\n",
                TOTAL_TIME_SECONDS, TIME_PER_QUESTION_MS / 1000);

        /// Główna pętla egzaminu, działa dopóki wątek timera nie ustawi flagi totalTimeUp
        while (!totalTimeUp) {
            System.out.printf("\n--- Pytanie %d ---\n", questionNumber);
            System.out.print("Podaj odpowiedź: ");

            long questionStartTime = System.currentTimeMillis();
            boolean answered = false;
            String answer = "";

            /// Pętla do obsługi pojedynczego pytania z limitem czasu
            while (System.currentTimeMillis() - questionStartTime < TIME_PER_QUESTION_MS && !totalTimeUp) {
                try {
                    /// Sprawdzam, czy w buforze wejścia są jakieś dane od użytkownika
                    if (System.in.available() > 0) {
                        /// Jeśli tak, to bezpiecznie odczytuję linię (nie zablokuje programu)
                        answer = scanner.nextLine();
                        answered = true;
                        break; /// Przerywam pętlę pytania, bo mam już odpowiedź
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }

            //// Po opuszczeniu pętli sprawdzam, co było tego powodem
            if (answered) {
                /// Jeśli udało się udzielić odpowiedzi...
                if (!totalTimeUp) {
                    /// ...i cały egzamin wciąż trwa, to zapisuję punkt...
                    System.out.printf("Odpowiedź '%s' została zapisana.\n", answer);
                    score++;
                } else {
                    /// ...ale w ostatniej chwili czas egzaminu się skończył, odpowiedź przepada
                    System.out.println("Niestety, czas na cały egzamin minął. Odpowiedź nie została zapisana.");
                }
            } else if (!totalTimeUp) {
                /// Jeśli nie udzieliłem odpowiedzi, ALE egzamin wciąż trwa,
                /// oznacza to, że skończył się czas TYLKO na to pytanie. Wyświetlam komunikat
                System.out.println("\n>> Czas na odpowiedź minął! Odpowiedź nie została zapisana.");
            }
            /// W ostatnim możliwym przypadku (nie odpowiedziałem ORAZ czas całego egzaminu minął),
            ///nie robię nic. Główna pętla i tak zaraz się zakończy, a komunikat o końcu
            ///egzaminu już został wyswietlony przez drugi wątek

            questionNumber++;
        }

        System.out.println("\n--- KONIEC EGZAMINU ---");
        System.out.println("Twój wynik końcowy: " + score);

        /// Na koniec, jeśli wątek timera wciąż działa (co jest mało prawdopodobne), przerywam go i zamykam scanner
        examTimerThread.interrupt();
        scanner.close();
    }
}