package com.example.pokeapi.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pokeapi.model.Question
import com.example.pokeapi.ui.navigation.PokeApiSreen


class PokeViewModel : ViewModel() {

    // Estados del UI
    private val _pokeUiState: MutableState<PokeUiState> = mutableStateOf(PokeUiState.Loading)
    val pokeUiState: State<PokeUiState> get() = _pokeUiState

    private val _questions = mutableStateListOf<Question>()
    val questions: List<Question> get() = _questions

    private val _totalQuestions = mutableStateOf(0)
    val totalQuestions: Int get() = _totalQuestions.value

    private val _currentQuestionIndex = mutableStateOf(0)
    val currentQuestionIndex: Int get() = _currentQuestionIndex.value

    private val _score = mutableStateOf(0)
    val score: Int get() = _score.value

    private val _record = mutableStateOf(0)
    val record: Int get() = _record.value

    private val _gameOver = mutableStateOf(false)
    val gameOver: Boolean get() = _gameOver.value

    private val _answerShown = mutableStateOf(false)
    val answerShown: Boolean get() = _answerShown.value

    private val _currentGenerationId = mutableStateOf(1)
    val currentGenerationId: Int get() = _currentGenerationId.value

    private val _currentTypeName = mutableStateOf("normal")
    val currentTypeName: String get() = _currentTypeName.value

    private val gameFunctions = GameFunctions(this)
    private val uiFunctions = UiFunctions(this)


    // Métodos accesibles para GameFunctions

    fun startGame(generationId: Int, typeName: String, questionCount: Int) {
        _currentGenerationId.value = generationId
        _currentTypeName.value = typeName
        gameFunctions.startGame(questionCount)
    }

    fun moveToNextQuestion(navController: NavController) {
        gameFunctions.moveToNextQuestion(navController)
    }

    // Envía la respuesta seleccionada por el usuario
    fun submitAnswer(selectedIndex: Int, shuffledOptions: List<String>) {
        // Obtener la pregunta actual
        val currentQuestion = questions[currentQuestionIndex]

        if (!answerShown) {
            // Asegúrate de que la respuesta correcta es una lista (puede ser uno o más tipos)
            val correctAnswers = currentQuestion.correct_answer // Esta debería ser una lista de tipos

            // Verifica si la respuesta seleccionada está en la lista de respuestas correctas
            val selectedAnswer = shuffledOptions[selectedIndex]
            if (correctAnswers.contains(selectedAnswer)) {
                updateScore(score + 1)
            }

            setAnswerShown(true)
        }
    }



    // Los setters y getters de estos estados deben estar disponibles
    fun setTotalQuestions(value: Int) {
        _totalQuestions.value = value
    }

    fun updateCurrentQuestionIndex(value: Int) {
        _currentQuestionIndex.value = value
    }

    fun updateScore(value: Int) {
        _score.value = value
    }

    fun setGameOver(value: Boolean) {
        _gameOver.value = value
    }

    fun setAnswerShown(value: Boolean) {
        _answerShown.value = value
    }

    fun clearQuestions() {
        _questions.clear()
    }

    // Método de UI
    fun setPokeUiState(state: PokeUiState) {
        _pokeUiState.value = state
    }

    // Modificar el método fetchQuestions para llamar la función de UiFunctions
    fun fetchQuestions(generationId: Int, questionCount: Int) {
        uiFunctions.fetchQuestions(generationId, questionCount)
    }

    fun addQuestions(newQuestions: List<Question>) {
        _questions.addAll(newQuestions)
    }

    fun checkAndUpdateRecord(percentage: Int) {
        if (percentage > _record.value) {
            _record.value = percentage
        }
    }

    fun updateRecord(percentage: Int) {
        checkAndUpdateRecord(percentage)
    }

    fun endGame() {
        _pokeUiState.value = PokeUiState.GameOver // Actualiza el estado a GameOver
    }

    fun updateRecordAndNavigate(navController: NavController) {
        // Calcula el porcentaje de aciertos
        val percentage = (score * 100) / totalQuestions
        // Actualiza el récord si el porcentaje es mayor
        if (percentage > record) {
            updateRecord(percentage)
        }

        // Solo cambiar el estado a GameOver si no lo ha hecho antes
        if (_pokeUiState.value != PokeUiState.GameOver) {
            // Llamar a endGame() para actualizar el estado a GameOver
            endGame()

            // Navegar a la pantalla de Game Over y asegurarse de que se vuelva al inicio correctamente
            navController.navigate(PokeApiSreen.GameOver.name) {
                // Limpiar la pila de navegación para que no se regrese a la pantalla de juego
                popUpTo(PokeApiSreen.Game.name) { inclusive = true }
                launchSingleTop =
                    true // Asegura que no se creen instancias adicionales de la pantalla
            }
        }
    }
}

