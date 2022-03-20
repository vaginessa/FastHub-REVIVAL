package com.fastaccess.ui.modules.profile.org.repos

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.model.Repo
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.ReposAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.profile.repos.ProfileReposFilterBottomSheetDialog
import com.fastaccess.ui.modules.profile.repos.ProfileReposFilterBottomSheetDialog.ProfileReposFilterChangeListener
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */
class OrgReposFragment : BaseFragment<OrgReposMvp.View, OrgReposPresenter>(), OrgReposMvp.View,
    ProfileReposFilterChangeListener {
    @JvmField
    @BindView(R.id.recycler)
    var recycler: DynamicRecyclerView? = null

    @JvmField
    @BindView(R.id.refresh)
    var refresh: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.stateLayout)
    var stateLayout: StateLayout? = null

    @JvmField
    @BindView(R.id.fastScroller)
    var fastScroller: RecyclerViewFastScroller? = null
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: ReposAdapter? = null
    override fun onNotifyAdapter(items: List<Repo>?, page: Int) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter!!.clear()
            return
        }
        if (page <= 1) {
            adapter!!.insertItems(items)
        } else {
            adapter!!.addItems(items)
        }
    }

    override fun fragmentLayout(): Int {
        return R.layout.micro_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw NullPointerException("Bundle is null, username is required")
        }
        stateLayout!!.setEmptyText(R.string.no_repos)
        stateLayout!!.setOnReloadListener(this)
        refresh!!.setOnRefreshListener(this)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        adapter = ReposAdapter(presenter!!.repos, false)
        adapter!!.listener = presenter
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        recycler!!.addDivider()
        if (presenter!!.repos.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun providePresenter(): OrgReposPresenter {
        return OrgReposPresenter()
    }

    override fun showProgress(@StringRes resId: Int) {
        refresh!!.isRefreshing = true
        stateLayout!!.showProgress()
    }

    override fun hideProgress() {
        refresh!!.isRefreshing = false
        stateLayout!!.hideProgress()
    }

    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override val loadMore: OnLoadMore<String>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            onLoadMore!!.parameter = requireArguments().getString(BundleConstant.EXTRA)
            return onLoadMore!!
        }

    override fun onRefresh() {
        presenter!!.onCallApi(1, requireArguments().getString(BundleConstant.EXTRA))
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (recycler != null) recycler!!.scrollToPosition(0)
    }

    override fun onRepoFilterClicked() {
        ProfileReposFilterBottomSheetDialog.newInstance(presenter.filterOptions)
            .show(childFragmentManager, "ProfileReposFilterBottomSheetDialog")
    }

    override fun onFilterApply() {
        presenter!!.onFilterApply(requireArguments().getString(BundleConstant.EXTRA))
    }

    override fun onTypeSelected(selectedType: String?) {
        presenter!!.onTypeSelected(selectedType)
    }

    override fun onSortOptionSelected(selectedSortOption: String?) {
        //Not supported for org profile
    }

    override fun onSortDirectionSelected(selectedSortDirection: String?) {
        //Not supported for org profile
    }

    override val login: String?
        get() = requireArguments().getString(BundleConstant.EXTRA)!!
    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        fun newInstance(username: String): OrgReposFragment {
            val view = OrgReposFragment()
            view.arguments = start().put(BundleConstant.EXTRA, username).end()
            return view
        }
    }
}