﻿# Projekt - Inteligentne Skrzyżowanie

W tym opisie postaram się zawrzeć wszystkie informacje związane z tym projektem, począwszy od researchu i wyboru algorytmu, aż po gotową aplikację.

Chciałem się skupić na konkretnym typie skrzyżowań - stare skrzyżowania z jednym pasem w wjeżdżającym i jednym wyjeżdżającym. Takie, które nie zostały w żaden sposób przystosowane do zwiększonego ruchu (bo na przykład były budowane 25 lat temu) i czekają na przebudowanie. Zastosowany algorytm ma na celu jak najszybciej rozładowywać ruch na takim skrzyżowaniu.

## Przybliżenie rzeczywistego zachowania na skrzyżowaniu

Starając się przybliżyć rzeczywistość przyjąłem pewne założenia:

 - Pewne kierunki blokują się, wtedy pierwszeństwo ma osoba po prawej stronie, przykładowo jeżeli samochód z południa jedzie prosto, a ze wschodu chce skręcić w lewo, to przejedzie tylko samochód ze wschodu (nie jest to nic nierealistycznego, tak samo działa np. skrzyżowanie pod moim domem).
 - Gdy dwa samochody jadą w to samo miejsce, na przykład jednocześnie na wschód z północy i południa, to nie blokują się one - samochód z południa znajdzie się na drodze szybciej od tego z północy, więc oba zdążą przejechać.
 - Zawracanie na skrzyżowaniu jest niedozwolone.
 - Maksymalną liczbą zielonych świateł jest 2.
 - Kierowcy zawsze jeżdżą zgodnie z przepisami i światłami ;)
 - Brak światła żółtego w symulacji - jedyną zmianą byłaby zmiana wizualna/opóźnienie o 1 krok.
 - Wprowadzenie minimalnego czasu trwania danego ustawienia świateł - żeby nie "mrugać" światłami co jeden krok.

## Wybór algorytmu

Po krótkim zastanowieniu uznałem, że ciekawym pomysłem będzie użycie mojego ulubionego algorytmu - Monte Carlo Tree Search (tu w zasadzie z tree niewiele zostało - Flat Monte Carlo Search). Jest to algorytm, który w tym przypadku na wejściu otrzymuje możliwe ustawienia świateł i, przeprowadzając dużą liczbę losowych symulacji, decyduje, które z ustawień jest najlepsze w danym celu.
Jest on najczęściej stosowany w grach takich jak szachy, go, warcaby, kółko i krzyżyk..., lecz na tym jego zastosowania się nie kończą, co pokazują wyniki tego eksperymentu. <br>
Najbardziej na ten wybór nakierował mnie podział w pliku JSON na kolejne kroki symulacji, co idealnie przygotowuje pole pod ten algorytm, oraz to, że napływ samochodów jest względnie losowy (ale możliwe jest wprowadzenie danych o ruchu przy małych modyfikacjach algorytmu). <br>
Dla zainteresowanych polecam świetny (i krótki!) film o istocie pełnego algorytmu MCTS: https://www.youtube.com/watch?v=UXW2yZndl7U

## Heurystyka

Najlepszą częścią tej heurystyki jest to... że jej nie ma! Dzięki zastosowaniu algorytmu MCTS możemy mu po prostu powiedzieć "Optymalizuj średni czas oczekiwania" i on to zrobi! Tak więc całe poszukiwanie heurystyki i  wymyślanie skomplikowanych wzorów jest całkowicie niepotrzebne i sprowadza się do wzoru na średnią: <br>
<p align="center">
<img src=https://latex.codecogs.com/png.image?\inline&space;\LARGE&space;\dpi{150}\bg{white}\sum_{0}^{3}\frac{(n_i-1)*n_i}{2} </>
</p>
Sumując po kolejnych kierunkach. Bierze się to stąd, że jeśli na danym pasie jest zielone światło, to pierwszy samochód nie czeka wcale, drugi czeka 1 krok, trzeci 2 itd.

## Zalety MCTS

Po za brakiem heurystyki ważną zaletą jest to, że MCTS nie wymaga idealnego modelowania, ani pełni danych - co jest tu moim zdaniem kluczowe, bo jakie skrzyżowanie będzie wiedziało, czy ktoś planuje jechać prosto, czy w prawo? Podanie takich informacji algorytmowi to lekkie nagięcie rzeczywistości. <br>
W zastosowanym przeze mnie podejściu MCTS dostaje jedynie informacje: na drodze X stoi Y samochodów, aktualnie są zapalone takie światła. Zostawia to dodatkowo sporo pola do poprawy, przykładowo, jeżeli mamy dane o tym, jak kształtuje się ruch o danej porze dnia, możemy łatwo wprowadzić to do algorytmu, a on **sam będzie priorytetyzował kierunki z większym ruchem**! <br>

## Wady MCTS
Główną znalezioną przeze mnie wadą tego podejścia są problemy przy bardzo małym ruchu, niektóre kierunki potrafią mieć przez to potencjalnie wydłużone czasy oczekiwania, gdyż stoi tam na przykład tylko 1 samochód. <br>
Jednak za cel uznałem rozładowywanie dużego ruchu, a ten problem jest dość prosty do poprawienia, stosując niewiele zmieniony algorytm MCTS uwzględniający obecne czasy oczekiwania na przejazd (do momentu uruchomienia symulacji) pierwszych samochodów, lub znalezienie innej wartości do optymalizacji - zamiast średniego czasu oczekiwania możemy przykładowo **minimalizować maksymalny czas oczekiwania**. <br>
Inną możliwą zmianą w tym wypadku jest uruchamianie tego modelu dopiero gdy ruch przekracza możliwości stałych zmian świateł. <br>
Zdecydowałem się jednak nie wprowadzać żadnych z tym zmian, aby nie komplikować dodatkowo algorytmu, oraz korzystać z założeń - ruchu większego niż ten, do którego przystosowane jest skrzyżowanie.


## Aplikacja

Aplikację napisałem w języku Java przy użyciu Java SDK 21, biblioteki JavaFX do stworzenia prostego GUI, oraz biblioteki Jackson do obsługi plików JSON. 
Buduję ją przy użyciu Gradle.

![](https://i.imgur.com/nQTBFVq.png)

Aplikacja udostępnia najważniejsze statystki, wizualny podgląd aktualnego stanu skrzyżowania, oraz suwak zmieniający prędkość symulacji - od jej zatrzymania po 100 kroków na sekundę.
Wspomniane 100 kroków na sekundę nie stanowi problemu dla algorytmu, co świadczy o jego wydajności, pomimo dużej liczby obliczeń.

## Uruchamianie aplikacji

Aplikację uruchamia się następująco:
Dla symulacji losowej:
```bash
gradlew run
```
lub
```bash
gradlew run --args="--FREQUENCY=int --NEW_CARS=int"
```
Dla symulacji z pliku JSON
```bash
gradlew run --args="--in=path"
```
lub
```bash
gradlew run --args="--in=path --out=path"
```
lub
```bash
gradlew run --args="--FREQUENCY=int --in=path --out=path"
```
Gdzie parametr FREQUENCY to minimalny czas pomiędzy zmianą świateł, liczony w krokach, aby zwiększyć realizm, a parametr NEW_CARS określa ile maksymalnie nowych aut w symulacji losowej może się pojawić w danym kroku, losowo od 1 do NEW_CARS. Można również uruchomić symulację bez żadnych, lub bez jednego parametru, wtedy przypisana zostanie wartość domyślna.

## Testy
W celu uruchomienia testów należy wykonać polecenie:
```bash
gradlew clean test
```

Z uwagi na losowość algorytmu nie zawsze wybierze on to samo ustawienie w takim samym układzie samochodów, chyba, że będzie ono **znacznie lepsze** od pozostałych, w związku z czym testy samego algorytmu ograniczają się właśnie do takich przypadków. <br>
Dodatkowo przetestowałem i odpowiednio zabezpieczyłem także różne błędy wejścia m. in. niepełne dane, błędne kierunki, literówki, dzięki czemu nawet jeśli wystąpi błąd w trakcie odczytu pliku to błędna komenda zostanie pominięta, bez powodowania zawieszenia całej aplikacji, a sam błąd zostanie wypisany w konsoli.

## Struktura pliku JSON

```json
{  
  "commands": [  
    {  
      "type": "addVehicle",  
      "vehicleId": "vehicle1",  
      "startRoad": "south",  
      "endRoad": "north"  
  },
  {
	  "type": "step"
  }
  ]
}
```

Dostępne komendy to addVehicle oraz step. vehicleId może być dowolnym napisem, startRoad oraz endRoad muszą należeć do [north, south, east, west].

## Wyjście

Jeżeli zostanie podany poprawny parametr in wraz z parametrem out, wyjście będzie wyglądać następująco:
```json
{  
  "stepStatuses" : [  
    {  
      "leftVehicles" : [  
        "vehicle1"  
  ]  
    } 
  ]  
}
```
Gdzie leftVehicles to samochody które opuściły skrzyżowanie w danym kroku symulacji.

## Podsumowanie
MCTS jest dobrze działającym podejściem w tym przypadku, poradziłby sobie jeszcze lepiej gdyby wprowadzić zagregowane, uśrednione informacje o ruchu w danym momencie (przykładowo: wiemy, że w piątki o 15:00 70% samochodów napływa z kierunku północnego, w tym 50% jedzie prosto a pozostałe są równo dzielone pomiędzy pozostałe kierunki), mając takie dane efektywność algorytmu zwiększyłaby się przynajmniej o kilkanaście procent. <br>
W moich dłuższych testach na losowych danych, przy założeniu braku wydłużania symulacji przez żółte światła, oraz z domyślnymi ustawieniami symulacji, średni czas oczekiwania zbiega do 11-12 kroków symulacji, co jest moim zdaniem bardzo dobrym wynikiem - to średnio ~30 sekund oczekiwania na przejazd, lepiej niż na niejednym skrzyżowaniu!
