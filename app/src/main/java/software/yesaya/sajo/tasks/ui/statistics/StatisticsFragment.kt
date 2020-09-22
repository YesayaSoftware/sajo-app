package software.yesaya.sajo.tasks.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import software.yesaya.sajo.R
import software.yesaya.sajo.SajoApplication
import software.yesaya.sajo.databinding.FragmentStatisticsBinding
import software.yesaya.sajo.utils.setupRefreshLayout

/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentStatisticsBinding

    private val viewModel by viewModels<StatisticsViewModel> {
        StatisticsViewModelFactory(
            (requireContext().applicationContext as SajoApplication).service,
            (requireContext().applicationContext as SajoApplication).tokenManager,
            (requireContext().applicationContext as SajoApplication).taskRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_statistics, container,
            false
        )
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = viewModel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }
}
