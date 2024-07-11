package com.example.techquiz.di

import com.example.techquiz.data.repository.CategoryRepository
import com.example.techquiz.data.repository.CategoryRepositoryDefault
import com.example.techquiz.data.repository.GivenAnswerRepository
import com.example.techquiz.data.repository.GivenAnswerRepositoryDefault
import com.example.techquiz.data.repository.QuestionRepository
import com.example.techquiz.data.repository.QuestionRepositoryDefault
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.data.repository.StatsRepositoryDefault
import com.example.techquiz.data.repository.UserDataStoreRepository
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
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

val repositoryModule = module {
    val questionApiUrlBuilder: UrlBuilderBlock = {
        protocol = URLProtocol.HTTPS
        host = "quizapi.io"
        path("/api/v1/")
        parameters.append("apiKey", "x7AsPaezRnoHTJs2LMOAbFkZlwTlqtEnyyjViMXT")
    }

    val answerApiUrlBuilder: UrlBuilderBlock = {
        protocol = URLProtocol.HTTPS
        host = ""
    }

    configureCategoryRepository(questionApiUrlBuilder)
    configureQuestionRepository(questionApiUrlBuilder)
    configureGivenAnswerRepository(answerApiUrlBuilder)
    configureStatsRepository(answerApiUrlBuilder)
    configureUserDataStoreRepository()
    configureUserRepository(answerApiUrlBuilder)
}

private fun Module.configureCategoryRepository(
    questionApiUrlBuilder: UrlBuilderBlock,
) = scope<CategoryViewModel> {
    scoped<CategoryRepository> {
        CategoryRepositoryDefault(
            getHttpClient(
                urlBuilder = questionApiUrlBuilder,
                sslManagerConfig = null,
            )
        )
    }
}

private fun Module.configureQuestionRepository(
    questionApiUrlBuilder: UrlBuilderBlock,
) = scope<QuestionViewModel> {
    scoped<QuestionRepository> {
        QuestionRepositoryDefault(
            getHttpClient(
                urlBuilder = questionApiUrlBuilder,
                sslManagerConfig = null,
            )
        )
    }
}

private fun Module.configureGivenAnswerRepository(
    answerApiUrlBuilder: UrlBuilderBlock,
) = scope<GivenAnswerViewModel> {
    scoped<GivenAnswerRepository> {
        GivenAnswerRepositoryDefault(
            getHttpClient(
                urlBuilder = answerApiUrlBuilder,
                sslManagerConfig = get<SslManagerConfig>(),
            )
        )
    }
}

private fun Module.configureStatsRepository(
    answerApiUrlBuilder: UrlBuilderBlock,
) = scope<StatsViewModel> {
    scoped<StatsRepository> {
        StatsRepositoryDefault(
            getHttpClient(
                urlBuilder = answerApiUrlBuilder,
                sslManagerConfig = get<SslManagerConfig>(),
            )
        )
    }
}

private fun Module.configureUserDataStoreRepository() =
    singleOf(::UserDataStoreRepository)

private fun Module.configureUserRepository(
    answerApiUrlBuilder: UrlBuilderBlock,
) = scope<LoginViewModel> {
    scoped<UserRepository> {
        UserRepositoryDefault(
            getHttpClient(
                urlBuilder = answerApiUrlBuilder,
                sslManagerConfig = get<SslManagerConfig>(),
            )
        )
    }
}

private fun Scope.getHttpClient(
    urlBuilder: UrlBuilderBlock,
    sslManagerConfig: SslManagerConfig?,
): HttpClient = get {
    parametersOf(urlBuilder, sslManagerConfig)
}
