# test-race
# Задание 
<p>Напишите консольное приложение на языке Java, которое симулирует гонки по кругу. В гонках участвуют разные типы транспортных средств: грузовики, легковушки и мотоциклы. Все транспортные средства стартуют одновременно, проходят один круг и останавливаются. Как только все они останавливаются, приложение выводит таблицу первенства и спрашивает пользователя, хочет ли он отправить их ещё на один круг.</p>
<p>У каждого из транспортных средств есть своя скорость и вероятность прокола колеса. Когда колесо прокалывается, транспортное средство останавливается на какое-то время и сообщает о проколе в консоль.</p>
<p>Перед началом движения каждое транспортное средство пишет в консоль свои параметры: скорость и вероятность прокола колеса. Дополнительно грузовик должен написать, какой у него вес груза, легковушка – сколько в ней людей, а мотоцикл – есть ли у него коляска. Во время движения каждое транспортное средство пишет, какое расстояние оно уже прошло. </p>
<p>Количество и типы транспортных средств, их параметры и длина круга считываются из конфигурационного файла.  В случае неясностей в условиях задачи сделайте разумные предположения, опишите их в сопроводительном тексте и отталкивайтесь при решении задания от этих предположений.</p>

#Предположения

1.  Какого формата должен быть конфигурационный файл? /  сделал xml
2.  Единица измерения времени, расстояния при расчетах / секунда, метр
3.  Как часто ТС должно сообщать о пройденном пути? / каждые 10 секунд
4.  Вероятность прокола считается для каждого колеса отдельно или сразу для машины целиком / сделал целиком
5.  В каких единицах измерения в конфиге указывается дистанция гонки, скорость транспортного средства , вероятность прокола, вес груза /километры, км/ч, нецелое число от нуля до 1, килограммы
6.  Как реализовать динамику движения транспорта? / в данный момент каждое средство за одну секунду набирает 1/10 от своей максимальной скорости. При торможении теряет 1/15 от максимальной скорости. Считается что каждую расчетную единицу времени транспортное средство движется с фиксированной скоростью
7.  Какова длительность устранения прокола колеса? Надо ли учитывать что прокол может устранятся за разное время на разном транспорте? / длительность равна 10 минутам. Все колеса на любом транспорте меняются за одинаковое время
8.  После заезда необходимо указывать кто какое место занял ? или достаточно вывести отсортированный по времени прибытия список транспортных средств / выводится отсортированный  по времени список
9.  Что делать если в конфиге будут ошибки? / если нарушена структура документа или невозможно считать дистанцию гонки, выполнение будет прервано. Если ошибка в описании одного или нескольких транспортов то загружаем тех кого сможем и запускаем выполнение программы.
10. Какие команды вводит пользователь для начала конца круга? / y – продолжить n - завершить
11. Как различать ТС? / в конфиг добавил возможность писать имена 
12. Если путь к конфигурационному файлу не будет указан / система завершит свое выполнение сообщив об этом

#Сведения для сборки и запуска<br>

Проект можно собрать с помощью ant. Цель для сборки jar файла default.<br>
При запуске jar файла в аргументе необходимо указать путь к файлу конфигурации.
