II. 

////////////////////////////////////////////////////////////////////////////////

// Реализуй метод isType, который проверяет, является ли объект объектом класса T, 
// либо его наследником.
// Пример кода использования:
isType<Boolean>(123) // false
isType<Number>(123) // true
isType<Int>(123) // true

isTypeStrict<Boolean>(123) // false
isTypeStrict<Number>(123) // false   <== в прошлом задании возвращал true
isTypeStrict<Int>(123) // true

// Решение
// Проверяет, является ли obj экземпляром T или его наследником (аналог instanceof в Java/Kotlin)
fun <T : Any> isType(obj: Any?, T: Class<T>): Boolean {
    return obj != null && T.isInstance(obj)
}

// Проверяет, является ли obj именно T (без учета наследников)
fun <T : Any> isTypeStrict(obj: Any?, T: Class<T>): Boolean {
    return obj != null && obj.javaClass == T
}
