package com.geby.saldo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geby.saldo.data.model.Category
import com.geby.saldo.data.repository.KategoriRepository
import kotlinx.coroutines.launch

class KategoriViewModel(private val repository: KategoriRepository) : ViewModel() {

    private val _kategoriList = MutableLiveData<List<Category>>()
    val kategoriList: LiveData<List<Category>> = _kategoriList

    fun loadKategori() {
        viewModelScope.launch {
            repository.insertDefaultIfEmpty() // inject dummy data if empty
            val data = repository.getAll()
            _kategoriList.postValue(data)
        }
    }

    fun tambahKategori(category: Category) {
        viewModelScope.launch {
            repository.insert(category)
            loadKategori() // refresh setelah insert
        }
    }

    fun categoryExisted(category: Category?): Boolean {
        return _kategoriList.value?.any {
            it.name.equals(category?.name, ignoreCase = true) &&it.type == category?.type
        } == true

    }
}
