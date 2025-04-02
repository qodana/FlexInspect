import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.analysis.api.symbols.KtFunctionSymbol
import org.jetbrains.kotlin.psi.*

@Language("HTML")
val htmlDescription = """
    <html>
    <body>
        Loops in suspend functions should check cancellation to achieve better cancellability
    </body>
    </html>
""".trimIndent()


val ensureActiveInLoopsOfSuspendFunctionsInspection = localInspection { psiFile, inspection ->
    fun isSuspendFunction(function: KtNamedFunction): Boolean {
        return analyze(function) {
            (function.getFunctionLikeSymbol() as? KtFunctionSymbol)?.isSuspend ?: false
        }
    }

    val suspendFunctions = psiFile.descendantsOfType<KtNamedFunction>()
        .filter { function -> isSuspendFunction(function) }

    suspendFunctions.forEach { suspendFunction ->
        val loops = suspendFunction.descendantsOfType<KtLoopExpression>()
        loops.forEach { loop ->
            val isEnsureActivePresent = loop.descendantsOfType<KtCallExpression>()
                .any { call -> (call.calleeExpression as? KtNameReferenceExpression)?.getReferencedName() == "ensureActive" }

            if (!isEnsureActivePresent) {
                inspection.registerProblem(loop, "No ensureActive in loop inside suspend function")
            }
        }
    }
}

listOf(
    InspectionKts(
        id = "KotlinEnsureActiveInLoopsOfSuspendFunctions",
        localTool = ensureActiveInLoopsOfSuspendFunctionsInspection,
        name = "Check ensureActive in loops of suspend functions",
        htmlDescription = htmlDescription,
        level = HighlightDisplayLevel.WARNING,
    )
)