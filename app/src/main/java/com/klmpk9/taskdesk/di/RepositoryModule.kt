package com.klmpk9.taskdesk.di

import com.klmpk9.taskdesk.data.repository.TicketRepoImpl
import com.klmpk9.taskdesk.data.repository.TicketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTicketRepository(
        impl: TicketRepoImpl
    ): TicketRepository
}