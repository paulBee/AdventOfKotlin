package year2022

import utils.aoc.readLinesFromFile
import utils.collections.chunkWhen
import utils.collections.headAndTail
import utils.collections.sumLong
import utils.collections.sumLongBy

fun main() {
    val input = readLinesFromFile("2022/day7.txt")

    val interactions = input.chunkWhen { _, s2 -> s2.startsWith("$ ") }
        .map { it.toInteraction() }

    val root = Directory("root")
    var currentDir = root

    interactions.forEach { interaction ->  when(interaction.program) {
        "cd" -> currentDir = when (val path = interaction.arguments.first()) {
                                "/" -> root
                                ".." -> currentDir.parent
                                else -> currentDir.directories.first { it.name == path }
                            }
        "ls" -> {
            val (directories, files) = interaction.output.partition { it.startsWith("dir ") }
            directories.forEach {
                val dirName = it.drop(4)
                if (currentDir.directories.any { it.name == dirName }) {
                    println("Cheeky Bugger")
                } else {
                    currentDir.directories.add(Directory(dirName, currentDir))
                }
            }
            files.forEach {
                val (fileSize, fileName) = it.split(" ")
                if (currentDir.files.any { it.name == fileName }) {
                    println("Cheeky Bugger")
                } else {
                    currentDir.files.add(File(fileName, fileSize.toLong()))
                }
            }
        }
        else -> throw RuntimeException("Dont Know this command!!")
    } }

    val directories = listOf(root) + root.allSubDirs()

    println(directories.map { it.allFiles().sumLongBy { it.size } }
        .filter { it <= 100000L }
        .sumLong())

    val totalDiskSpace = 70000000L
    val requiredFreeDiskSpace = 30000000L
    val totalUsedSpace = root.allFiles().sumLongBy { it.size }
    val requiredSpaceToDelete = requiredFreeDiskSpace - (totalDiskSpace - totalUsedSpace)

    println(totalDiskSpace)
    println(requiredFreeDiskSpace)
    println(totalUsedSpace)
    println(requiredSpaceToDelete)

    println(directories.map { it.allFiles().sumLongBy { it.size } }
        .filter { it > requiredSpaceToDelete }
        .min()
    )
}

private fun List<String>.toInteraction(): Interaction {
    val (command, output) = this.headAndTail()
    val words = command.split(" ")
    return Interaction (
        program = words[1],
        arguments = words.drop(2),
        output
    )
}

data class Interaction (
    val program: String,
    val arguments: List<String>,
    val output: List<String> = emptyList()
)

class Directory(val name: String, parent: Directory? = null)  {

    val parent = parent ?: this
    val files = mutableListOf<File>()
    val directories = mutableListOf<Directory>()

    fun allSubDirs(): List<Directory> {
        return directories + directories.flatMap { it.allSubDirs() }
    }

    fun allFiles(): List<File> = files + allSubDirs().flatMap { it.files }
}

data class File(val name: String, val size: Long)



