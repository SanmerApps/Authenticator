package dev.sanmer.authenticator.ui.screen.trash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.authenticator.Const.INSTANT_ZERO
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.repository.DbRepository
import kotlinx.coroutines.launch

class TrashViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    var data by mutableStateOf<LoadData<List<Auth>>>(LoadData.Loading)
        private set

    private val _selected = mutableStateListOf<Long>()
    val selected get() = _selected.size
    val isPick get() = _selected.isNotEmpty()

    private val logger = Logger.Android("TrashViewModel")

    init {
        logger.d("init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            dbRepository.getTrashedAuthAsFlow()
                .collect { list ->
                    data = LoadData.Success(
                        list.sortedByDescending { it.trashedAt }
                    )
                }
        }
    }

    fun isSelected(auth: Auth) = _selected.contains(auth.id)

    fun pick(auth: Auth) = if (isSelected(auth)) {
        _selected.remove(auth.id)
    } else {
        _selected.add(auth.id)
    }

    fun clearSelected() {
        _selected.clear()
    }

    fun restore() {
        viewModelScope.launch {
            _selected.forEach { authId ->
                runCatching {
                    dbRepository.trash(authId, INSTANT_ZERO)
                }.onFailure {
                    logger.e(it)
                }
            }
            clearSelected()
        }
    }

    fun delete() {
        viewModelScope.launch {
            _selected.forEach { authId ->
                runCatching {
                    dbRepository.delete(authId)
                }.onFailure {
                    logger.e(it)
                }
            }
            clearSelected()
        }
    }
}