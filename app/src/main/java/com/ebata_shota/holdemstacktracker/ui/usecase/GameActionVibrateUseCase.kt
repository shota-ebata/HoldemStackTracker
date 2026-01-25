package com.ebata_shota.holdemstacktracker.ui.usecase

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.ebata_shota.holdemstacktracker.domain.model.AutoCheckOrFoldType
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GameActionVibrateUseCase
@Inject
constructor(
    private val vibrator: Vibrator,
) {
    private fun startVibrate(primitiveId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrator.vibrate(
                VibrationEffect.startComposition()
                    .addPrimitive(primitiveId)
                    .compose()
            )
        }
    }

    fun onFold() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startVibrate(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL)
        }
    }

    fun onCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrator.vibrate(
                VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f, 150)
                    .compose()
            )
        }
    }

    fun onClickAllIn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startVibrate(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE)
        }
    }

    fun onCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startVibrate(VibrationEffect.Composition.PRIMITIVE_CLICK)
        }
    }

    fun onRaise() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startVibrate(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE)
        }
    }

    fun onChangeRaiseSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startVibrate(VibrationEffect.Composition.PRIMITIVE_TICK)
        }
    }

    fun onAutoCheckFold(autoCheckOrFoldType: AutoCheckOrFoldType) {
        when (autoCheckOrFoldType) {
            is AutoCheckOrFoldType.ByGame -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    startVibrate(VibrationEffect.Composition.PRIMITIVE_LOW_TICK)
                }
            }

            is AutoCheckOrFoldType.None -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    startVibrate(VibrationEffect.Composition.PRIMITIVE_CLICK)
                }
            }
        }
    }

    fun onChangeRaiseSlider(
        isEnableRaiseUpSliderStep: Boolean,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startVibrate(
                if (isEnableRaiseUpSliderStep) {
                    VibrationEffect.Composition.PRIMITIVE_TICK
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        VibrationEffect.Composition.PRIMITIVE_LOW_TICK
                    } else {
                        VibrationEffect.Composition.PRIMITIVE_TICK
                    }
                }
            )
        }
    }
}