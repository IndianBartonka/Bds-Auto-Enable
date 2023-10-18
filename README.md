<div align="center">

# BDS-Auto-Enable

Jest to program do zarządzania BDS wykorzystywany na serverze **Huje22**

</div>

# Uwaga

* Program wymaga przynajmniej `1GB` ram do działania
* Zaleca się używać integracji z discord (JDA)
* Program wspiera użycie [**WINE**](https://github.com/wine-mirror/wine)

# Program zawiera

* Automatyczne włączenie servera po crashu
* Łatwe załadowanie innej wersji
* Tworzenie backupów świata co dany czas i manualnie (___Ładowanie backup wymaga nadal wielkiej poprawy, wymaga debugu właczonego___)
* Pisanie w konsoli w 99% (**Mogą wystąpić małe błędy**)
* AutoMessages
* Licznik czasu gry gracza (Wymaga [BDS-Auto-Enable-Management-Pack](https://github.com/Huje22/BDS-Auto-Enable-Management-Pack))
* Licznik śmierci (Wymaga [BDS-Auto-Enable-Management-Pack](https://github.com/Huje22/BDS-Auto-Enable-Management-Pack))
* Integracje z Discordem
  (Wymaga [BDS-Auto-Enable-Management-Pack](https://github.com/Huje22/BDS-Auto-Enable-Management-Pack) do obsługi większej
  ilości funkcji)
* **Rest API** z czasem gry , liczbą śmierci i graczami online/offline

# Polecenia

### W konsoli

* `backup` - natychmiastowo wywołuje tworzenie backupa
* `version` - pokazuje załadowaną versie minecraft + versie oprogramowania (w konsoli i graczom online)
* `stats` - statystyki servera i aplikacji
* `playtime` - top 20 graczy z największym czasem gry
* `deaths` - top 20 graczy z największą ilością śmierci
* `end` - zamyka server i aplikacje

### W Bocie

* Wszystkie je jak i także ich opisy znajdziesz po wpisaniu `/` (Wymaga dodania bota z
  ___&scope=bot+applications.commands___ inaczej mogą wystąpić problemy)

# Program nie wspiera

* Wtyczek do Minecraft

# Szybkie info

* Paczka sama się pobierze do twojego świata i załaduje , potrzebujesz jedynie włączonych experymentów w tym świecie!

  ----

![bStats Servers](https://img.shields.io/bstats/servers/19727?style=for-the-badge)
![bStats Players](https://img.shields.io/bstats/players/19727?style=for-the-badge)
![Latest Tag](https://img.shields.io/github/v/tag/Huje22/Bds-Auto-Enable?label=LATEST%20TAG&style=for-the-badge) <br>
