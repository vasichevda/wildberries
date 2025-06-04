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

// Задание 1 Ответ
// Значения кошельков не должны измениться
// Но из-за race condition в deposit() и withdraw() (они не синхронизированы), итоговые значения могут быть непредсказуемыми

// Задание 2 Решение

class WBWallet(
    initialBalance: Int,
    val owner: String
) {
    private var balance: Int = initialBalance
    private val history: MutableList<Transaction> = mutableListOf()

    fun getBalance(): Int = synchronized(this) { balance }

    fun getHistory(): List<Transaction> = synchronized(this) { history.toList() }

    fun deposit(amount: Int) = synchronized(this) {
        balance += amount
        history.add(Transaction(Operation.DEPOSIT, amount))
    }

    fun withdraw(amount: Int) = synchronized(this) {
        balance -= amount
        history.add(Transaction(Operation.WITHDRAW, amount))
    }

    fun transferTo(otherWallet: WBWallet, amount: Int) {
        //fix deadlock
        val firstLock = if (this.hashCode() < otherWallet.hashCode()) this else otherWallet
        val secondLock = if (firstLock == this) otherWallet else this

        synchronized(firstLock) {
            synchronized(secondLock) {
                balance -= amount
                history.add(Transaction(Operation.TRANSFER_OUT, amount, otherWallet.owner))
                
                otherWallet.balance += amount
                otherWallet.history.add(Transaction(Operation.TRANSFER_IN, amount, owner))
            }
        }
    }
}

enum class Operation { DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT }

data class Transaction(
    val operation: Operation,
    val amount: Int,
    val counterparty: String? = null
)

suspend fun main() = coroutineScope {
    val wallet1 = WBWallet(1000, "Roman")
    val wallet2 = WBWallet(1000, "Sergey")

    val jobs = listOf(
        launch { repeat(100) { wallet1.deposit(10) } },
        launch { repeat(100) { wallet1.withdraw(10) } },
        launch { repeat(100) { wallet1.transferTo(wallet2, 10) } },
        launch { repeat(100) { wallet2.transferTo(wallet1, 10) } }
    )

    jobs.joinAll()
    
    println("Wallet 1 balance: ${wallet1.getBalance()}")  // 1000
    println("Wallet 2 balance: ${wallet2.getBalance()}")  // 1000
}
