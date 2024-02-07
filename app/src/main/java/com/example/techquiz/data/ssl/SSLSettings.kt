package com.example.techquiz.data.ssl

import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object SSLSettings {
    fun getSSLContext(
        keyStoreFile: InputStream,
        keyStorePassword: CharArray,
    ): SSLContext? =
        SSLContext.getInstance(
            "TLS",
        ).also {
            it.init(
                null,
                getTrustManagerFactory(
                    keyStoreFile,
                    keyStorePassword,
                )?.trustManagers,
                null,
            )
        }

    private fun getTrustManagerFactory(
        keyStoreFile: InputStream,
        keyStorePassword: CharArray,
    ): TrustManagerFactory? =
        TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        ).also {
            it.init(
                getKeyStore(
                    keyStoreFile,
                    keyStorePassword,
                )
            )
        }

    private fun getKeyStore(
        keyStoreFile: InputStream,
        keyStorePassword: CharArray,
    ): KeyStore =
        KeyStore.getInstance(
            KeyStore.getDefaultType(),
        ).also {
            it.load(
                keyStoreFile,
                keyStorePassword,
            )
        }
}
