object Config {
    const val minSdk = 21
    const val targetSdk = 32
}

object Publish {
    const val group = "com.github.pgreze"
    const val artifactId = "android-reactions"
    const val githubUrl = "https://github.com/pgreze/android-reactions"

    val tagVersion: String? = System.getenv("GITHUB_REF")?.split('/')?.last()
    val version: String = tagVersion?.trimStart('v') ?: "WIP"
}
