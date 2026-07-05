package br.pucpr.authserver.storage

import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isRegularFile

@Component
@Profile("local")
class LocalStorageService : StorageService {
    override fun save(path: String, contentType: String, bytes: ByteArray): String {
        val root = Paths.get(ROOT)
        val destinationFile = root.resolve(path)
            .normalize()
            .toAbsolutePath()
        Files.createDirectories(destinationFile.parent)
        Files.write(destinationFile, bytes)
        return urlFor(path)
    }

    override fun urlFor(name: String): String =
        "http://localhost:8080/api/files/" +
                URLEncoder.encode(
                    name.replace("/", "--"),
                    StandardCharsets.UTF_8
                )

    override fun load(path: String): Resource? =
        Paths.get(ROOT, path.replace("--", "/"))
            .takeIf { it.isRegularFile() }
            ?.let { UrlResource(it.toUri()) }

    companion object {
        const val ROOT = "./fs"
    }
}
