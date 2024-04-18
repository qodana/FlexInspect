import kotlinx.coroutines.*

class Main {
    fun main() = runBlocking {
        doWorld()
        println("Done")
    }


    suspend fun doWorld() = coroutineScope {
        launch {
            delay(1000L)
            for (n in 1..10) { // triggers the "No ensureActive in loop inside suspend function" inspection
                println("World 1")
            }
        }
        println("Hello")
    }
}