package com.example.composecamera.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhotoViewModel: ViewModel() {

    private val _state: MutableStateFlow<List<Bitmap>> = MutableStateFlow(mutableListOf())
    val state: StateFlow<List<Bitmap>> = _state.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap){
        println("Photo taken")
        _state.value += bitmap
    }
}