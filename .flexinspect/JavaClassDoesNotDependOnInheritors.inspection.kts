import com.intellij.psi.*
import org.intellij.lang.annotations.Language

/**
 * This is an auto-generated template Java custom inspection
 * Reports all local variables inside all class methods
 *
 * The inspection is applied automatically and executed on-fly: to see the inspection results, open the Java file in the editor
 * 
 * In this example, the inspection algorithm is the following:
 *   1. Take all classes in file
 *   2. If the class is not an interface, take all its declared methods, otherwise ignore
 *   3. For each declared method, take all nodes that correspond to local variables and are descendants of method node 
 *   4. Reports variables' name and type
 */

/**
 * Full HTML description of inspection: Describe here motivation, examples, etc.
 */
@Language("HTML")
val htmlDescription = """
    <html>
    <body>
        The inspection reports classes that depend on their inheritors.
    </body>
    </html>
""".trimIndent()

/**
 * Inspection operates with file's PSI tree: to see the PSI tree of a file, open the PSI Viewer 
 * PSI tree is an AST representing file's source code with PsiFile is a root node 
 * You can traverse the tree/call API methods of specific PsiElements to retrieve some data

 * You can use the following utility methods to traverse the PSI tree from given PsiElement:
 * * PsiElement.getChildren() – all children nodes
 * * PsiElement.descendants() – all children nodes recursively (with children's children)
 * * PsiElement.descendantsOfType<...>() – all children of specified type recursively
 * * PsiElement.getParent() – parent node 
 * * PsiElement.parents(withSelf = false) – all parent nodes recursively
 * * PsiElement.siblings(forward = true, withSelf = false) – all forward siblings: nodes with the same parent located after this element
 * * PsiElement.siblings(forward = false, withSelf = false) – all backward siblings: nodes with the same parent located before this element
 
 * Call `inspection.findPsiFileByRelativeToProjectPath(String)` to get other PSI file 
 * Call `inspection.registerProblem(PsiElement, String)` function to report a problem from inspection
 * 
 * See the PSI Viewer for available APIs and PSI tree structure
 * Invoke "Open PSI Viewer" in the banner above or select "Tools | View PSI structure of Current File..." from the top menu
 * 
 * How to debug the inspection: call inspection.registerProblem(PsiElement, "your debug message") and see highlighting in the editor, also check PSI viewer
 */
val JavaClassDoesNotDependOnInheritorsInspection = localInspection { psiFile, inspection ->
    fun isInheritorOfPsiClass(psiType: PsiType, psiClass: PsiClass): Boolean {
        return psiClass.asPsiClassType() in psiType.getAllSuperTypes()
    }

    val notFinalClasses = psiFile.descendantsOfType<PsiClass>()
        .filter { javaClass ->
            val isFinal = javaClass.modifierList?.text?.contains("final") ?: true
            !isFinal
        }

    notFinalClasses.forEach { javaClass ->
        // check return type of called methods
        val methodCalls = javaClass.descendantsOfType<PsiCallExpression>()
            .filter { call -> call !is PsiNewExpression } // constructor will be checked by code below (reference to type)

        methodCalls.forEach { call ->
            val returnType = call.type ?: return@forEach
            if (isInheritorOfPsiClass(returnType, javaClass)) {
                inspection.registerProblem(call, "Class ${javaClass.name} must not depend on its inheritor ${returnType.canonicalText}")
            }
        }

        // references to type: parameter/variable type, return type, instanceof, etc.
        val references = javaClass.descendantsOfType<PsiJavaCodeReferenceElement>()
        references.forEach { reference ->
            val referenceType = reference.asPsiClassType()
            if (isInheritorOfPsiClass(referenceType, javaClass)) {
                inspection.registerProblem(reference, "Class ${javaClass.name} must not depend on its inheritor ${referenceType.className}")
            }
        }
    }
}

// You can define multiple inspections in one .inspection.kts file 
listOf(
    InspectionKts(
        id = "JavaClassDoesNotDependOnInheritors", // inspection id (used in qodana.yaml)
        localTool = JavaClassDoesNotDependOnInheritorsInspection,
        name = "Java class must not depend on inheritors", // Inspection name, displayed in UI
        htmlDescription = htmlDescription,
        level = HighlightDisplayLevel.WARNING,
    )
    // ...
)