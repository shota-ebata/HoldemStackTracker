package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.usecase.RandomIdRepository
import java.util.UUID
import javax.inject.Inject

class RandomIdRepositoryImpl
@Inject
constructor(

) : RandomIdRepository {

    override fun generateRandomId(): String {
        return UUID.randomUUID().toString()
    }
}