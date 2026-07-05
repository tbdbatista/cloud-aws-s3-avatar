package br.pucpr.authserver.users

import br.pucpr.authserver.storage.StorageService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.math.BigInteger
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Service
class AvatarService(val storage: StorageService) {
    private val restTemplate = RestTemplate()

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.trim().lowercase().toByteArray(StandardCharsets.UTF_8)))
            .toString(16)
            .padStart(32, '0')
    }

    fun save(user: User): String {
        val userId = user.id ?: throw IllegalArgumentException("User id cannot be null")
        val hash = md5(user.email)
        val gravatarUrl = "https://www.gravatar.com/avatar/$hash?d=404"

        val bytes = try {
            restTemplate.getForObject(gravatarUrl, ByteArray::class.java)
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatus.NOT_FOUND) {
                val uiUrl = "https://ui-avatars.com/api/?name=${URLEncoder.encode(user.name, StandardCharsets.UTF_8)}&format=png"
                restTemplate.getForObject(uiUrl, ByteArray::class.java)
            } else {
                log.error("Error downloading from Gravatar", e)
                null
            }
        } catch (e: Exception) {
            log.error("Unexpected error fetching gravatar", e)
            null
        }

        if (bytes == null || bytes.isEmpty()) {
            log.warn("Could not download avatar. Using default.")
            return DEFAULT_AVATAR
        }

        val path = "$userId/a_$userId.png"
        try {
            storage.save("$ROOT/$path", "image/png", bytes)
            return path
        } catch (e: Exception) {
            log.error("Failed to upload avatar to storage for user $userId", e)
            return DEFAULT_AVATAR
        }
    }

    fun urlFor(path: String): String =
        storage.urlFor("$ROOT/$path")

    companion object {
        const val ROOT = "avatars"
        const val DEFAULT_AVATAR = "default.png"
        private val log = LoggerFactory.getLogger(AvatarService::class.java)
    }
}
