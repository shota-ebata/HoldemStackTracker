package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import java.util.UUID
import javax.inject.Inject

class RandomIdRepositoryImpl
@Inject
constructor(

) : RandomIdRepository {

    override fun generateRandomId(): String {
        return UUID.randomUUID().toString().take(6)
    }
}