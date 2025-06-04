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

// Решение согласно SOLID

// 1. Принцип единственной ответственности (Single Responsibility)
interface UserValidator {
    fun validateUsername(username: String): Boolean
    fun validateEmail(email: String): Boolean
}

class DefaultUserValidator : UserValidator {
    override fun validateUsername(username: String): Boolean {
        return username.length in 3..20 && 
               username.matches(Regex("[a-zA-Z0-9_]+"))
    }
    
    override fun validateEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))
    }
}

// 2. Принцип открытости/закрытости (Open/Closed)
interface UserRepository {
    fun save(user: User)
}

class DatabaseUserRepository : UserRepository {
    override fun save(user: User) {
        // Реальная реализация сохранения в БД
    }
}

// 3. Принцип подстановки Лисков (Liskov Substitution)
interface EmailService {
    fun sendConfirmation(email: String)
}

class SmtpEmailService : EmailService {
    override fun sendConfirmation(email: String) {
        // Реализация через SMTP
    }
}

class MockEmailService : EmailService {
    override fun sendConfirmation(email: String) {
        // Тестовая реализация
    }
}

// 4. Принцип разделения интерфейсов (Interface Segregation)
interface UserRegistrationResult {
    val success: Boolean
}

// 5. Принцип инверсии зависимостей (Dependency Inversion)
class UserManager(
    private val validator: UserValidator = DefaultUserValidator(),
    private val userRepository: UserRepository = DatabaseUserRepository(),
    private val emailService: EmailService = SmtpEmailService()
) {
    fun registerUser(username: String, email: String): UserRegistrationResult {
        return when {
            !validator.validateUsername(username) -> InvalidUsernameResult
            !validator.validateEmail(email) -> InvalidEmailResult
            else -> {
                val user = User(username, email)
                userRepository.save(user)
                emailService.sendConfirmation(email)
                SuccessRegistrationResult(user)
            }
        }
    }
}

// Модели данных
data class User(val username: String, val email: String)

// Результаты регистрации
object InvalidUsernameResult : UserRegistrationResult {
    override val success = false
}

object InvalidEmailResult : UserRegistrationResult {
    override val success = false
}

data class SuccessRegistrationResult(val user: User) : UserRegistrationResult {
    override val success = true
}
