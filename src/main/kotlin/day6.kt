import java.lang.IllegalStateException

fun main() {
    val orbitInfo = readLinesFromFile("day6.txt")
        .map { it.toOrbitRelationship() }


    val objects = ObjectOrbitInfo("COM", 0, orbitInfo, null).getOrbiters()
    println(objects.sumBy { it.distanceToCOM })

    val you = objects.find { it.objectName == "YOU" }?:throw IllegalStateException("I think therefore I ... should be?")
    val san = objects.find { it.objectName == "SAN" }?:throw IllegalStateException("What!!? Santa isn't real!!!??")

    val youToCom = you.getPathToCOM()
    val sanToCom = san.getPathToCOM()

    val sharedPath = youToCom.intersect(sanToCom)

    println((youToCom.size + sanToCom.size) - (2 * sharedPath.size) - 2)



}

fun buildOrbitOf(objectName: String, distanceToCOM: Int, orbitInfo: List<OrbitalRelationship>) {

}

val orbitRegex = """^(.*)\)(.*)$""".toRegex()
private fun String.toOrbitRelationship(): OrbitalRelationship {
    val (centre, orbiter) =
    orbitRegex.matchEntire(this)
        ?.destructured
        ?: throw IllegalArgumentException("String $this does not match regex")
    return OrbitalRelationship(centre, orbiter)
}

data class OrbitalRelationship(val centre: String, val orbiter: String)

class ObjectOrbitInfo(val objectName: String, val distanceToCOM: Int, orbitInfo: List<OrbitalRelationship>, val orbitingObject: ObjectOrbitInfo?) {

    val objectsOrbitingIt : List<ObjectOrbitInfo> =
        orbitInfo.filter { it.centre == objectName }
            .map { ObjectOrbitInfo(it.orbiter, distanceToCOM + 1, orbitInfo, this) }

    fun getOrbiters(): List<ObjectOrbitInfo> =
        objectsOrbitingIt.flatMap { it.getOrbiters() }
            .let { it.plus(this) }

    fun getPathToCOM(): List<ObjectOrbitInfo> =
        (orbitingObject?.getPathToCOM()?:emptyList()).let { it.plus(this) }
}