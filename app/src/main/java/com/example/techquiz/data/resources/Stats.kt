package com.example.techquiz.data.resources

import io.ktor.resources.Resource

@Resource("/stats")
class Stats {
    @Resource("most_answered_categories")
    class MostAnsweredCategories(val parent: Stats = Stats())

    @Resource("answered_questions_count")
    class AnsweredQuestionsCount(val parent: Stats = Stats())

    @Resource("correct_answers_count")
    class CorrectAnswersCount(val parent: Stats = Stats())
}