package br.pucpr.authserver.storage

import org.springframework.core.io.Resource

interface StorageService {
    fun save(path: String, contentType: String, bytes: ByteArray): String
    fun load(path: String): Resource?
    fun urlFor(name: String): String
}
