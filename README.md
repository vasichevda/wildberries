# wildberries
interview wildberries

I. 

////////////////////////////////////////////////////////////////////////////////

// Задача: По клику на кнопку, должно отсчитаться 5 секунд
// и текст "hello world" должен измениться на "goodbye world".
// Напиши как ты обычно решаешь такую задачу у себя на продакшн проекте.
// В рамках задачи можно использовать любые библиотеки и подходы.
/**     +--------------------------------+
        |         hello world            |
        |         [ Click me ]           |
        +--------------------------------+      **/

@Composable
fun TextChangeScreen() {
    Button {
        Text("hello world")
    }
}



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
isTypeStrict<Int>(null)



III. 

////////////////////////////////////////////////////////////////////////////////

// У нас есть кошелек, мы можем его пополнять, выводить с него деньги, либо переводить их на другой кошелек.
// Баланс может уходить в минус.
//
// Задание 1: Что будет выведено в консоль?
// Задание 2: Проведи рефакторинг кода

class WBWallet(initialBalance: Int, val owner: String) {
    var balance: Int = initialBalance
    val history: MutableList<Transaction> = mutableListOf()

    fun deposit(amount: Int) {
        balance += amount
        history += Transaction("+$amount")
    }

    fun withdraw(amount: Int) {
        balance -= amount
        history += Transaction("-$amount")
    }

    fun transferTo(otherWallet: WBWallet, amount: Int) {
        synchronized(this) {
            synchronized(otherWallet) {
                balance -= amount
                history += Transaction("-$amount")
                otherWallet.balance += amount
                otherWallet.history += Transaction("+$amount")
            }
        }
    }
}

data class Transaction(
    val amount: String // "+10", "-10"
)

fun main() {
    // Пример использования
    val wallet1 = WBWallet(1000, "Roman")
    val wallet2 = WBWallet(1000, "Sergey")

    val jobs = MutableList<Job>()
    
    jobs += scope.launch {
        for (i in 1..100) {
            wallet1.deposit(10)
        }
    }
    jobs += scope.launch {
        for (i in 1..100) {
            wallet1.withdraw(10)
        }
    }
    jobs += scope.launch {
        for (i in 1..100) {
            wallet1.transferTo(wallet2, 10)
        }
    }
    jobs += scope.launch {
        for (i in 1..100) {
            wallet2.transferTo(wallet1, 10)
        }
    }

    jobs.joinAll()
    
    println("Wallet 1 balance: ${wallet1.balance}")
    println("Wallet 2 balance: ${wallet2.balance}")
}



IV. 

////////////////////////////////////////////////////////////////////////////////

// Задача: провести рефакторинг кода.

class UserManager {

    fun registerUser(username: String, email: String) {
        if (isValidUsername(username) && isValidEmail(email)) {
            val user = User(username, email)
            saveUserToDatabase(user)
            sendEmailConfirmation(email)
        } else {
            println("Invalid username or email.")
        }
    }

    private fun isValidUsername(username: String): Boolean {
        // Validation logic for username
        return username.isNotEmpty()
    }
    
    private fun isValidEmail(email: String): Boolean {
        // Validation logic for email
        return email.contains("@")
    }

     private fun saveUserToDatabase(user: User) {
        // Code to save user to database
    }

    private fun sendEmailConfirmation(email: String) {
        // Code to send email confirmation
    }
    
}

data class User(val username: String, val email: String)
