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
import io.ktor.http.URLProtocol
import io.ktor.http.path
import org.koin.core.parameter.parametersOf
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
            get {
                parametersOf(questionApiUrlBuilder, null)
            }
        )
    }

    single<QuestionRepository> {
        QuestionRepositoryDefault(
            get {
                parametersOf(questionApiUrlBuilder, null)
            }
        )
    }

    single<GivenAnswerRepository> {
        GivenAnswerRepositoryDefault(
            get {
                parametersOf(answerApiUrlBuilder, get<AdditionalHttpClientConfig>())
            }
        )
    }

    single<StatsRepository> {
        StatsRepositoryDefault(
            get {
                parametersOf(answerApiUrlBuilder, get<AdditionalHttpClientConfig>())
            }
        )
    }

    single<UserRepository> {
        UserRepositoryDefault(
            get {
                parametersOf(answerApiUrlBuilder, get<AdditionalHttpClientConfig>())
            }
        )
    }
}
