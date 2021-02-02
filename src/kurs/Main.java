package kurs;

/*
Сегодня будем имитировать работу с базой данных. Только вместо базы данных у нас будет файл с JSON.
Сам файл можно скачать отсюда - https://drive.google.com/file/d/1CbAYvkwlFdviNWoA8MPu-WrF42znhjV6/view
Это небольшая база с пользователями, которая содержит следующие колонки -
ID пользователя, имя, фамилия, возраст, текущая страна, и булевое значение —
считается ли пользователь совершеннолетним в этой стране.
Известно, что в разных странах совершеннолетие считается с разного возраста. В файле принимают
участие четыре страны — Россия (с 18 лет), Япония (с 20 лет), США (с 21 года) и Тайланд (с 20 лет).
Наша задача — написать класс, которые работает с этой базой. Он должен содержать следующие методы:
1) Получить всех пользователей из указанной страны. Страна приходит параметром.
2) Получить всех пользователей указанного возраста. Возраст приходит параметром.
3) Получить всех пользователей старше указанного возраста. Возраст приходит параметром.
4) Получить всех пользователей младше указанного возраста или равного ему. Возраст приходит параметром.
5) Получить всех совершеннолетних.
6) Получить всех тинов.
7) Найти все битые записи. Битые записи — это когда пользователь для текущей локации на самом деле должен быть
совершеннолетним или тином, а в базе поле is_teen проставлено неправильно.
Само собой, нужно спроектировать класс так, чтобы было как можно меньше дублирования в коде.
----------------------------------------------------------------------------------------------------
В качестве более сложной задачи мы к задаче номер 1 добавим еще немного методов:
8) Исправить все записи. Функция должна читать файл, анализировать - является ли пользователь на самом деле
тином в текущей локации или нет, и при необходимости корректировать ему поле is_teen, перезаписывая файл.
9) Перевезти пользователя в текущую локацию. Локация приходит параметром. Если локация не одна из четырех -
Россия, Япония, США, Тайланд - функция пишет, что не можно перевезти туда пользователя. Если страна из этих
четырех - перевозит, меняя запись в файле и перепроверяя и корректирую поле is_teen при необходимости.
Например, если мы пользователя, которому 18 лет, перевозим из РФ в США, он должен перестать считаться
совершеннолетним.
10) Добавить страну. Да-да, выходит, что возраст совершеннолетия для страны надо хранить в каком-то конфиге,
который можно дополнять. При вызове этого метода название страны приходит параметром, вторым параметром приходит
возраст совершеннолетия. После добавления страны пользователей можно перевозить в эту страну также (метод 9).
*/

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //создаем базу пользователей из json файла
        Users users = new Users();
        //выводим меню, запрашиваем, что делать
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("--------------------------------------------------------");
            System.out.println("1. Получить всех пользователей из указанной страны");
            System.out.println("2. Получить всех пользователей указанного возраста");
            System.out.println("3. Получить всех пользователей старше указанного возраста");
            System.out.println("4. Получить всех пользователей не старше указанного возраста");
            System.out.println("5. Получить всех совершеннолетних (по полю teen)");
            System.out.println("6. Получить всех тинов (по полю teen)");
            System.out.println("7. Найти все битые записи");
            System.out.println("8. Исправить битые записи, перезаписать файл");
            System.out.println("9. Переехать в другую страну");
            System.out.println("10. Добавить страну");
            System.out.print("Введите номер операции (0 - для выхода): ");
            int answer = scanner.nextInt();
            if (answer == 0) break;
            if (answer == 1) usersByCountryName(scanner, users);
            if (answer == 2) usersByAge(scanner, users);
            if (answer == 3) usersByAgeOlder(scanner, users);
            if (answer == 4) usersByAgeUnder(scanner, users);
            if (answer == 5) outUsers(users.usersByCondition(null, null, Users.COND_ADULT));
            if (answer == 6) outUsers(users.usersByCondition(null, null, Users.COND_TEEN));
            if (answer == 7) outUsers(users.usersByCondition(null, null, Users.COND_WRONG));
            if (answer == 8) users.correctWrong();
            if (answer == 9) migrate(scanner, users);
            if (answer == 10) usersAddCountry(scanner, users);
        }
        scanner.close();
    }

    private static void usersByCountryName(Scanner scanner, Users users) {
        String countryName = enterCountry(scanner);
        outUsers(users.usersByCondition(countryName, null, Users.COND_COUNTRY));
    }

    private static void usersAddCountry(Scanner scanner, Users users) {
        String countryName = enterCountry(scanner);
        if (users.countryExists(countryName)) {
            System.out.println("Такая страна уже есть!");
            return;
        }
        Long age = enterAge(scanner);
        users.addCountry(countryName, age);
    }

    private static void migrate(Scanner scanner, Users users) {
        Long id = enterId(scanner);
        User u = users.byId(id);
        if (u == null) {
            System.out.println("Не найден пользователь по id!");
            return;
        }
        String countryName = enterCountry(scanner);
        Country c = users.countryByName(countryName);
        if (c == null) {
            System.out.println("В эту страну нельзя переехать (нет в справочнике, сначала добавьте ее)!");
            return;
        }
        u.setCountry(c);
        outUsers(users.usersByCondition(countryName, null, Users.COND_COUNTRY));
    }

    private static void usersByAge(Scanner scanner, Users users) {
        Long age = enterAge(scanner);
        outUsers(users.usersByCondition(null, age, Users.COND_AGE));
    }

    private static void usersByAgeOlder(Scanner scanner, Users users) {
        Long age = enterAge(scanner);
        outUsers(users.usersByCondition(null, age, Users.COND_AGE_OLDER));
    }

    private static void usersByAgeUnder(Scanner scanner, Users users) {
        Long age = enterAge(scanner);
        outUsers(users.usersByCondition(null, age, Users.COND_AGE_UNDER));
    }

    private static Long enterAge(Scanner scanner) {
        System.out.print("Введите возраст: ");
        return scanner.nextLong();
    }

    private static String enterCountry(Scanner scanner) {
        System.out.print("Введите название страны: ");
        return scanner.next();
    }

    private static Long enterId(Scanner scanner) {
        System.out.print("Введите id пользователя: ");
        return scanner.nextLong();
    }

    private static void outUsers(User[] ua) {
        for (User user : ua)
            System.out.println(user.info());
    }
}
