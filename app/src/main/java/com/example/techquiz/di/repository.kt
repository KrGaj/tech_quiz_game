package com.example.techquiz.di

import com.example.techquiz.data.repository.CategoryRepository
import com.example.techquiz.data.repository.CategoryRepositoryDefault
import com.example.techquiz.data.repository.GivenAnswerRepository
import com.example.techquiz.data.repository.GivenAnswerRepositoryDefault
import com.example.techquiz.data.repository.QuestionRepository
import com.example.techquiz.data.repository.QuestionRepositoryDefault
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.data.repository.StatsRepositoryDefault
import com.example.techquiz.data.repository.UserRepository
import com.example.techquiz.data.repository.UserRepositoryDefault
import io.ktor.client.HttpClient
import io.ktor.http.URLProtocol
import io.ktor.http.path
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

val repositoryModule = module {
    val questionApiUrlBuilder: UrlBuilderBlock = {
        protocol = URLProtocol.HTTPS
        host = "quizapi.io"
        path("/api/v1/")
        parameters.append("apiKey", "")
    }

    val answerApiUrlBuilder: UrlBuilderBlock = {
        protocol = URLProtocol.HTTPS
        host = ""
    }

    single<CategoryRepository> {
        CategoryRepositoryDefault(
            getHttpClient(questionApiUrlBuilder, null)
        )
    }

    single<QuestionRepository> {
        QuestionRepositoryDefault(
            getHttpClient(questionApiUrlBuilder, null)
        )
    }

    single<GivenAnswerRepository> {
        GivenAnswerRepositoryDefault(
            getHttpClient(answerApiUrlBuilder, get<AdditionalHttpClientConfig>())
        )
    }

    single<StatsRepository> {
        StatsRepositoryDefault(
            getHttpClient(answerApiUrlBuilder, get<AdditionalHttpClientConfig>())
        )
    }

    single<UserRepository> {
        UserRepositoryDefault(
            getHttpClient(answerApiUrlBuilder, get<AdditionalHttpClientConfig>())
        )
    }
}

private fun Scope.getHttpClient(
    urlBuilder: UrlBuilderBlock,
    additionalConfig: AdditionalHttpClientConfig?,
): HttpClient = get {
    parametersOf(urlBuilder, additionalConfig)
}
