import com.intellij.openapi.vfs.VirtualFile
import org.intellij.lang.annotations.Language
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import java.util.function.Consumer

/**
 * This is an auto-generated template of custom global inspection
 * It doesn't report anything, you need to implement your logic in [globalInspection]
 * and call [inspection.registerProblem] to report result from inspection
 *
 * The global inspections are executed ONLY in full project analysis: in Qodana Analysis or Inspect Code...
 * 
 * How to run Qodana Locally: https://www.jetbrains.com/help/qodana/quick-start.html#quickstart-run-in-ide 
 */

/**
 * Full HTML description of inspection: Describe here motivation, examples, etc.
 */
@Language("HTML")
val htmlDescription = """
    <html>
    <body>
        The inspection reports classes instantiated more than once.
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
val javaClassInstantiatedMoreThanOnce = globalInspection { inspection ->

    val project = inspection.project
    val javaFiles: MutableCollection<VirtualFile> = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
    javaFiles.forEach(Consumer forEach@{ file: VirtualFile ->
        val psiFile = PsiManager.getInstance(project).findFile(file) as? PsiJavaFile ?: return@forEach
        for (aClass in psiFile.classes) {
            var referencesCount = 0
            ReferencesSearch.search(aClass, GlobalSearchScope.projectScope(project)).forEach { reference ->
                referencesCount++

            }
            println("Debugging message: ${aClass.asPsiClassType()}")
            if (referencesCount > 1) {
                inspection.registerProblem(aClass, "The class is instantiated more than once")
            }
        }
    })

}

// You can define multiple inspections in one .inspection.kts file 
listOf(
    InspectionKts(
        id = "JavaClassInstantiatedMoreThanOnce", // inspection id (used in qodana.yaml)
        globalTool = javaClassInstantiatedMoreThanOnce,
        name = "Java class instantiated more than once once", // Inspection name, displayed in UI
        htmlDescription = htmlDescription,
        level = HighlightDisplayLevel.WARNING,
    )
)