package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.repository.RemoteConfigRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class RemoteConfigRepositoryImpl
@Inject
constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : RemoteConfigRepository {
    private val _isMaintenance = MutableStateFlow(false)
    override val isMaintenance = _isMaintenance.asStateFlow()

    private val _minVersionCode = MutableStateFlow(0)
    override val minVersionCode = _minVersionCode.asStateFlow()

    init {
        remoteConfig.addOnConfigUpdateListener(
            object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    remoteConfig.fetchAndActivate().addOnCompleteListener {
                        _isMaintenance.update {
                            remoteConfig.getBoolean("is_maintenance")
                        }
                        _minVersionCode.update {
                            remoteConfig.getLong("min_app_version_code").toInt() // まあ、いいか。
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    firebaseCrashlytics.recordException(error)
                }
            }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _isMaintenance.update {
                    remoteConfig.getBoolean("is_maintenance")
                }
            }
        }
    }
}