package com.example.techquiz.app.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.example.techquiz.data.local")
class LocalDataModule
