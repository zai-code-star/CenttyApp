package com.example.admin.ui.listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.admin.data.model.ListItem
import com.example.admin.data.repository.ListingRepository

class ListingViewModel(private val repository: ListingRepository) : ViewModel() {

    private val _data: MutableLiveData<List<ListItem>> = MutableLiveData()
    val data: LiveData<List<ListItem>>
        get() = _data

    // Fungsi untuk mengambil semua data dari Firebase Realtime Database
    fun getAllDataFromFirebase() {
        repository.getAllDataFromFirebase { items ->
            _data.postValue(items)
        }
    }

    // Fungsi untuk mengambil data dari Firebase Realtime Database berdasarkan jenis
    fun getDataFromFirebaseByType(tipe: String) {
        repository.getDataFromFirebaseByType(tipe) { items ->
            _data.postValue(items)
        }
    }
}

