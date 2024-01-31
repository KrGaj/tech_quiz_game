package com.example.techquiz.data.resources

import com.example.techquiz.data.dto.GivenAnswerDTO
import io.ktor.resources.Resource

@Resource("/answer")
class GivenAnswerRes(val answer: GivenAnswerDTO)
