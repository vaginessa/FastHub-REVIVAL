package com.fastaccess.ui.modules.repos.wiki

import android.content.Intent
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.data.dao.wiki.FirebaseWikiConfigModel
import com.fastaccess.data.dao.wiki.WikiContentModel
import com.fastaccess.data.dao.wiki.WikiSideBarModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.jsoup.JsoupProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.HttpException
import java.util.*

/**
 * Created by Kosh on 13 Jun 2017, 8:14 PM
 */
class WikiPresenter : BasePresenter<WikiMvp.View>(), WikiMvp.Presenter {

    @com.evernote.android.state.State var repoId: String? = null
    @com.evernote.android.state.State var login: String? = null

    override fun onActivityCreated(intent: Intent?) {
        if (intent != null) {
            val bundle = intent.extras
            repoId = bundle?.getString(BundleConstant.ID)
            login = bundle?.getString(BundleConstant.EXTRA)
            val page = bundle?.getString(BundleConstant.EXTRA_TWO)
            if (!page.isNullOrEmpty()) {
                sendToView { it.onSetPage(page) }
            }
            if (!repoId.isNullOrEmpty() && !login.isNullOrEmpty()) {
                onSidebarClicked(WikiSideBarModel("Home", "$login/$repoId/wiki" + if (!page.isNullOrEmpty()) "/$page" else ""))
            }
        }
    }

    override fun onSidebarClicked(sidebar: WikiSideBarModel) {
    }

    private fun callApi(sidebar: WikiSideBarModel) {
        manageViewDisposable(RxHelper.getObservable(JsoupProvider.getWiki().getWiki(sidebar.link))
            .flatMap { s -> RxHelper.getObservable(getWikiContent(s)) }
            .doOnSubscribe { sendToView { it.showProgress(0) } }
            .subscribe(
                { response -> sendToView { view -> view.onLoadContent(response) } },
                { throwable ->
                    if (throwable is HttpException) {
                        if (throwable.code() == 404) {
                            sendToView { it.showPrivateRepoError() }
                            return@subscribe
                        }
                    }
                    onError(throwable)
                },
                { sendToView { it.hideProgress() } }
            )
        )
    }

    private fun getWikiContent(body: String?): Observable<WikiContentModel> {
        return Observable.fromPublisher { s ->
            val document: Document = Jsoup.parse(body, "")
            s.onNext(WikiContentModel("<h2 align='center'>Wiki in app not supported</h4>", "", arrayListOf()))

            s.onComplete()
        }
    }
}