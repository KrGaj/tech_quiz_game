package com.example.techquiz.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

val sslManagerModule = module {
    single {
        val sslManagerConfig: AdditionalHttpClientConfig = { config ->
            config.engine {
                sslManager = {
                    val keyStoreFile = androidContext().assets.open("keystore/keystore.bks")
                    val keyStorePassword = "DemoApka".toCharArray()

                    it.sslSocketFactory = getSSLContext(keyStoreFile, keyStorePassword)
                        ?.socketFactory

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
