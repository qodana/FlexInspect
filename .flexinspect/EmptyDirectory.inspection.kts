import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import org.intellij.lang.annotations.Language
import com.intellij.psi.PsiManager


@Language("HTML")
val htmlDescription = """
    <html>
    <body>
        This custom inspection is based on the existing "Empty directory" inspection
    </body>
    </html>
""".trimIndent()

fun isReadActionNeeded(): Boolean {
    return false
}
fun getPathRelativeToModule(file: VirtualFile, project: Project): String? {
    val rootManager = ProjectRootManager.getInstance(project)
    val contentRoots = rootManager.contentRootsFromAllModules
    for (otherRoot in contentRoots) {
        if (VfsUtilCore.isAncestor(otherRoot, file, false)) {
            return VfsUtilCore.getRelativePath(file, otherRoot, '/')
        }
    }
    return null
}
val emptyDirectoryInspection = globalInspection { inspection ->
    val onlyReportDirectoriesUnderSourceRoots = false
    val project: Project = inspection.project
    val index: ProjectFileIndex = ProjectRootManager.getInstance(project).fileIndex
    val psiManager = PsiManager.getInstance(project)
    index.iterateContent { file: VirtualFile ->
        if (onlyReportDirectoriesUnderSourceRoots && !index.isInSourceContent(file)) {
            return@iterateContent true
        }
        if (!file.isDirectory || file.children.isNotEmpty()) {
            return@iterateContent true
        }
        val relativePath: String? = getPathRelativeToModule(file, project)
        if (relativePath == null) {
            return@iterateContent true
        }
        inspection.registerProblem(psiManager.findFile(file), "Empty directory $relativePath")
        return@iterateContent true
    }

}

listOf(
    InspectionKts(
        id = "EmptyDirectoryFlexInspectInspection", // inspection id (used in qodana.yaml)
        globalTool = emptyDirectoryInspection,
        name = "Empty directory (FlexInspect duplicate)", // Inspection name, displayed in UI
        htmlDescription = htmlDescription,
        level = HighlightDisplayLevel.WARNING,
    )
)