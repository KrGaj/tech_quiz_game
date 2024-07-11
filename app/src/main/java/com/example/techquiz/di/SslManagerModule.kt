package com.example.techquiz.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

private const val KEYSTORE_PATH = "keystore/keystore.bks"
private const val KEYSTORE_PASSWORD = "DemoApka"
private const val SSL_MANAGER_PROTOCOL = "TLSv1.3"

val sslManagerModule = module {
    single {
        val sslManagerConfig: SslManagerConfig = { config ->
            config.engine {
                sslManager = {
                    val keyStoreFile = androidContext().assets
                        .open(KEYSTORE_PATH)
                    val keyStorePassword = KEYSTORE_PASSWORD.toCharArray()

                    it.sslSocketFactory = getSSLContext(
                        keyStoreFile,
                        keyStorePassword,
                    )?.socketFactory

                    keyStoreFile.close()
                }
            }
        }

        sslManagerConfig
    }
}

private fun getSSLContext(
    keyStoreFile: InputStream,
    keyStorePassword: CharArray,
): SSLContext? =
    SSLContext.getInstance(
        SSL_MANAGER_PROTOCOL,
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
        TrustManagerFactory.getDefaultAlgorithm(),
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
