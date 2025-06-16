package com.geby.saldo.di

import android.content.Context
import com.geby.saldo.data.repository.KategoriRepository
import com.geby.saldo.data.repository.TransaksiRepository


object Injection {
    fun provideTransaksiRepository(context: Context): TransaksiRepository {
        return TransaksiRepository.getInstance(context)
    }
    fun provideKategoriRepository(context: Context): KategoriRepository {
        return KategoriRepository.getInstance(context)
    }
}