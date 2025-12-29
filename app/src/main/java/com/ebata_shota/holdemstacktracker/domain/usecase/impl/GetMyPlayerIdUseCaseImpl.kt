package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMyPlayerIdUseCase
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetMyPlayerIdUseCaseImpl
@Inject
constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
) : GetMyPlayerIdUseCase {
    override suspend fun invoke(): PlayerId? {
        return firebaseAuthRepository.myPlayerIdFlow.firstOrNull()
    }
}