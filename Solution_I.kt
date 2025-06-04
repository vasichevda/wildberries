I. 

// Задача: По клику на кнопку, должно отсчитаться 5 секунд
// и текст "hello world" должен измениться на "goodbye world".
// Напиши как ты обычно решаешь такую задачу у себя на продакшн проекте.
// В рамках задачи можно использовать любые библиотеки и подходы.
/**     +--------------------------------+
        |         hello world            |
        |         [ Click me ]           |
        +--------------------------------+      **/

// Первое решение - только compose
@Composable
fun TextChangeScreen() {
    var text by remember { mutableStateOf("hello world") }
    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(isClicked) {
        if (isClicked) {
            delay(5_000)
            text = "goodbye world"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { isClicked = true }) {
            Text("Click me")
        }
    }
}

// Второе решение - через viewModel

class TextChangeViewModel : ViewModel() {

    private val _text = MutableStateFlow("hello world")
    val text: StateFlow<String> = _text.asStateFlow()

    fun onButtonClick() {
        viewModelScope.launch {
            delay(5_000)
            _text.value = "goodbye world"
        }
    }
}

@Composable
fun TextChangeScreen(viewModel: TextChangeViewModel = viewModel()) {
    val text by viewModel.text.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.onButtonClick() }) {
            Text("Click me")
        }
    }
}
