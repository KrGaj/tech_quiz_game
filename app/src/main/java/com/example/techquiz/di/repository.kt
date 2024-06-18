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
import com.example.techquiz.viewmodel.CategoryViewModel
import com.example.techquiz.viewmodel.GivenAnswerViewModel
import com.example.techquiz.viewmodel.LoginViewModel
import com.example.techquiz.viewmodel.QuestionViewModel
import com.example.techquiz.viewmodel.StatsViewModel
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

    scope<CategoryViewModel> {
        scoped<CategoryRepository> {
            CategoryRepositoryDefault(
                getHttpClient(
                    urlBuilder = questionApiUrlBuilder,
                    additionalConfig = null,
                )
            )
        }
    }

    scope<QuestionViewModel> {
        scoped<QuestionRepository> {
            QuestionRepositoryDefault(
                getHttpClient(
                    urlBuilder = questionApiUrlBuilder,
                    additionalConfig = null,
                )
            )
        }
    }

    scope<GivenAnswerViewModel> {
        scoped<GivenAnswerRepository> {
            GivenAnswerRepositoryDefault(
                getHttpClient(
                    urlBuilder = answerApiUrlBuilder,
                    additionalConfig = get<AdditionalHttpClientConfig>(),
                )
            )
        }
    }

    scope<StatsViewModel> {
        scoped<StatsRepository> {
            StatsRepositoryDefault(
                getHttpClient(
                    urlBuilder = answerApiUrlBuilder,
                    additionalConfig = get<AdditionalHttpClientConfig>(),
                )
            )
        }
    }

    scope<LoginViewModel> {
        scoped<UserRepository> {
            UserRepositoryDefault(
                getHttpClient(
                    urlBuilder = answerApiUrlBuilder,
                    additionalConfig = get<AdditionalHttpClientConfig>(),
                )
            )
        }
    }
}

private fun Scope.getHttpClient(
    urlBuilder: UrlBuilderBlock,
    additionalConfig: AdditionalHttpClientConfig?,
): HttpClient = get {
    parametersOf(urlBuilder, additionalConfig)
}
