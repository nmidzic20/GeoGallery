# GeoGallery

## Projektni tim
(svi članovi tima moraju biti iz iste seminarske grupe)

Ime i prezime | E-mail adresa (FOI) | JMBAG | Github korisničko ime | Seminarska grupa
------------  | ------------------- | ----- | --------------------- | ----------------
Hrvoje Lukšić | hluksic20@foi.hr | 0016148613 | HLuksic | G01
David Kajzogaj | dkajzogaj20@foi.hr | 0016146827 | davidkajzogaj | G01
Noa Midžić | nmidzic20@foi.hr | 0108082571 | nmidzic20 | G01

## Opis domene
GeoGallery običnu galeriju pretvara u nešto zanimljivo, poučno i interaktivno. Snimljene fotografije, video i audio zapisi postavljaju se na kartu pomoću lokacije mobilnog uređaja i Googleovih servisa. Ovisno o razini zooma grupiraju se u skupine po kontinentu, državi, gradu itd. sve do pojedinačnih lokacija. O lokacijama se po mogućnosti povlače kratke informacije s wikipedije. Korisnik može imenovati, opisivati, grupirati i premještati medije po želji te ih zajedno s informacijama dijeliti na društvenim mrežama.

## Specifikacija projekta
Oznaka | Naziv | Kratki opis | Odgovorni član tima
------ | ----- | ----------- | -------------------
F01 | Korištenje kamere | Za osnovne funkcije GeoGalleryja potrebno je omogućiti korištenje kamere | ...
F02 | Korištenje mikrofona | Za osnovne funkcije GeoGalleryja potrebno je omogućiti korištenje mikrofona | ...
F03 | Korištenje lokacije | Iako nije nužno, GeoGallery može koristiti lokaciju mobilnog uređaja da bi automatski postavio medije na njihovu lokaciju na karti | ...
F04 | Korištenje Google karti | GeoGallery za uslugu karte koristi Googleove servise | ...
F05 | CRUD funkcionalnosti nad medijima | Korisnik može dodavati, pregledavati, mijenjati i brisati medije i njihove informacije | ...
F06 | Dohvat informacija s Wikipedije | GeoGallery radi opisa lokacije (gdje je moguće) koristi informacije s Wikipedije | ...
F07 | Ručno postavljanje medija | Korisnik može ručno postavljati medije na bilo koju lokaciju na karti | ...
F08 | Dijeljenje medija | Korisnik može medije dijeliti na društvenim mrežama zajedno s odabranim informacijama | ...
F09 | Statistika | Korisnik može vidjeti koliko je zapisa snimio na pojedinom kontinentu, državi, gradu itd. | ...

## Tehnologije i oprema
GeoGallery je namijenjen za Android uređaje. Za razvoj ćemo koristiti Android studio i programski jezik Kotlin. Potrebni vanjski servisi su API za Google karte i Wikpediju.

## Baza podataka i web server
--

## .gitignore
Uzmite u obzir da je u mapi Software .gitignore konfiguriran za nekoliko tehnologija, ali samo ako će projekti biti smješteni direktno u mapu Software ali ne i u neku pod mapu. Nakon odabira konačne tehnologije i projekta obavezno dopunite/premjestite gitignore kako bi vaš projekt zadovoljavao kriterije koji su opisani u ReadMe.md dokumentu dostupnom u mapi Software.
