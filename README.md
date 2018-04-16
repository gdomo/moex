Читает из stdin строки вида <br/> ``` <B|S> <количество> <рубли>.<копейки>``` <br/>
После ввода пустой строки выполняет расчет и выводит в stdout строку вида <br/>``` <количество> <рубли>.<копейки>```

# Сборка и запуск:

В папке проекта:

./gradlew jar

java -jar /build/libs/moex.jar

Будет запущена дефолтная реализация на массиве

Для запуска реализации на TreeSet:

./gradlew jar

java -cp build/libs/moex.jar com.example.moex.TreeSetEngineLauncher
