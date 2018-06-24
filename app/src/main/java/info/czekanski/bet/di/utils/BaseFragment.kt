package info.czekanski.bet.di.utils

import android.arch.lifecycle.*
import android.content.Context
import android.support.v4.app.Fragment
import dagger.android.support.AndroidSupportInjection
import info.czekanski.bet.di.module.view_model.DiViewModelFactory
import javax.inject.Inject

open class BaseFragment : Fragment() {
    @Inject lateinit var viewModelFactory: DiViewModelFactory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    inline fun <reified T : ViewModel> viewModel(): T = ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
}

