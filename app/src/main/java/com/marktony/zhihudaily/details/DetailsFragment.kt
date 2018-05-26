/*
 * Copyright 2016 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marktony.zhihudaily.details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatTextView
import android.text.Html
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.customtabs.CustomTabsHelper
import com.marktony.zhihudaily.data.*
import com.marktony.zhihudaily.glide.loadImage
import com.marktony.zhihudaily.util.KEY_NIGHT_MODE
import com.marktony.zhihudaily.util.KEY_NO_IMG_MODE
import kotlinx.android.synthetic.main.fragment_details.*

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Main UI for the details screen.
 * Display the content of [ZhihuDailyContent], [DoubanMomentContent] and [GuokrHandpickContentResult].
 * Shown by [DetailsActivity]. Works with [DetailsPresenter].
 */

class DetailsFragment : Fragment(), DetailsContract.View {

    private var mId: Int = 0
    private var mType: ContentType = ContentType.TYPE_ZHIHU_DAILY
    private var mTitle: String? = null

    private var mIsNightMode = false
    private var mIsFavorite = false

    override val isActive: Boolean
        get() = isAdded && isResumed

    override lateinit var mPresenter: DetailsContract.Presenter

    companion object {

        var REQUEST_SHARE = 0
        var REQUEST_COPY_LINK = 1
        var REQUEST_OPEN_WITH_BROWSER = 2

        fun newInstance(): DetailsFragment = DetailsFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(activity?.intent ?: return) {
            mId = getIntExtra(DetailsActivity.KEY_ARTICLE_ID, -1)
            mType = getSerializableExtra(DetailsActivity.KEY_ARTICLE_TYPE) as ContentType
            mTitle = getStringExtra(DetailsActivity.KEY_ARTICLE_TITLE)
            mIsNightMode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_NIGHT_MODE, false)
            mIsFavorite = getBooleanExtra(DetailsActivity.KEY_ARTICLE_IS_FAVORITE, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(activity as DetailsActivity? ?: return) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        }

        mTitle?.let {
            setCollapsingToolbarLayoutTitle(it)
        }

        toolbar.setOnClickListener {
            nested_scroll_view.smoothScrollTo(0, 0)
        }

        with(web_view) {
            isScrollbarFadingEnabled = true
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = false
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true
            settings.setAppCacheEnabled(false)

            // Show the images or not.
            settings.blockNetworkImage = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_NO_IMG_MODE, false)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    CustomTabsHelper.openUrl(context, url)
                    return true
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
        when {
            mType === ContentType.TYPE_ZHIHU_DAILY -> mPresenter.loadZhihuDailyContent(mId)
            mType === ContentType.TYPE_DOUBAN_MOMENT -> mPresenter.loadDoubanContent(mId)
            else -> mPresenter.loadGuokrHandpickContent(mId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_more, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            android.R.id.home -> activity?.onBackPressed()
            R.id.action_more -> {
                activity?.let {
                    val dialog = BottomSheetDialog(it)
                    val view = it.layoutInflater.inflate(R.layout.actions_details_sheet, null)

                    val favorite = view.findViewById<AppCompatTextView>(R.id.text_view_favorite)
                    val copyLink = view.findViewById<AppCompatTextView>(R.id.text_view_copy_link)
                    val openWithBrowser = view.findViewById<AppCompatTextView>(R.id.text_view_open_with_browser)
                    val share = view.findViewById<AppCompatTextView>(R.id.text_view_share)

                    favorite.setText(if (mIsFavorite) R.string.unfavorite else R.string.favorite)

                    // add to bookmarks or delete from bookmarks
                    favorite.setOnClickListener {
                        dialog.dismiss()
                        mIsFavorite = !mIsFavorite
                        mPresenter.favorite(mType, mId, mIsFavorite)
                    }

                    // copy the article's link to clipboard
                    copyLink.setOnClickListener {
                        mPresenter.getLink(mType, REQUEST_COPY_LINK, mId)
                        dialog.dismiss()
                    }

                    // open the link in system browser
                    openWithBrowser.setOnClickListener {
                        mPresenter.getLink(mType, REQUEST_OPEN_WITH_BROWSER, mId)
                        dialog.dismiss()
                    }

                    // getLink the content as text
                    share.setOnClickListener {
                        mPresenter.getLink(mType, REQUEST_SHARE, mId)
                        dialog.dismiss()
                    }

                    dialog.setContentView(view)
                    dialog.show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showMessage(stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
    }

    override fun showZhihuDailyContent(content: ZhihuDailyContent) {

        var result = content.body
        result = result.replace("<div class=\"img-place-holder\">", "")
        result = result.replace("<div class=\"headline\">", "")

        val css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">"

        var theme = "<body className=\"\" onload=\"onLoaded()\">"
        if (mIsNightMode) {
            theme = "<body className=\"\" onload=\"onLoaded()\" class=\"night\">"
        }

        result = ("<!DOCTYPE html>\n"
                + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n"
                + "\t<meta charset=\"utf-8\" />"
                + css
                + "\n</head>\n"
                + theme
                + result
                + "</body></html>")

        web_view.loadDataWithBaseURL("x-data://base", result, "text/html", "utf-8", null)

        image_view.loadImage(content.image)

    }

    override fun showDoubanMomentContent(content: DoubanMomentContent, list: List<DoubanMomentNewsThumbs>?) {

        val css: String
        var body = content.content
        if (mIsNightMode) {
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/douban_dark.css\" type=\"text/css\">"
        } else {
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/douban_light.css\" type=\"text/css\">"
        }
        if (list != null && !list.isEmpty()) {
            image_view.loadImage(list[0].medium.url)
            for ((medium, _, _, tagName) in list) {
                val old = "<img id=\"$tagName\" />"
                val newStr = ("<img id=\"" + tagName + "\" "
                        + "src=\"" + medium.url + "\"/>")
                body = body.replace(old, newStr)
            }
        }
        val result = ("<!DOCTYPE html>\n"
                + "<html lang=\"ZH-CN\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n<meta charset=\"utf-8\" />\n"
                + css
                + "\n</head>\n<body>\n"
                + "<div class=\"container bs-docs-container\">\n"
                + "<div class=\"post-container\">\n"
                + body
                + "</div>\n</div>\n</body>\n</html>")

        web_view.loadDataWithBaseURL("x-data://base", result, "text/html", "utf-8", null)
    }

    override fun showGuokrHandpickContent(content: GuokrHandpickContentResult) {

        image_view.loadImage(content.imageInfo.url)

        val body = content.content

        val css: String = if (mIsNightMode) {
            "<div class=\"article\" id=\"contentMain\" style=\"background-color:#212b30\">"
        } else {
            "<div class=\"article\" id=\"contentMain\">"
        }

        val result = ("<!DOCTYPE html>\n"
                + "<html lang=\"ZH-CN\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n<meta charset=\"utf-8\" />\n"
                + "\n<link rel=\"stylesheet\" href=\"file:///android_asset/guokr_master.css\" />\n"
                + css
                + "<script src=\"file:///android_asset/guokr.base.js\"></script>\n"
                + "<script src=\"file:///android_asset/guokr.articleInline.js\"></script>"
                + "<script>\n"
                + "var ukey = null;\n"
                + "</script>\n"
                + "\n</head>\n<div class=\"content\" id=\"articleContent\"><body>\n"
                + body
                + "\n</div></body>\n</html>")
        web_view.loadDataWithBaseURL("x-data://base", result, "text/html", "utf-8", null)
    }

    override fun share(link: String?) {
        try {
            val shareIntent = Intent().setAction(Intent.ACTION_SEND).setType("text/plain")
            val shareText = "$mTitle $link"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
        } catch (ex: android.content.ActivityNotFoundException) {
            showMessage(R.string.something_wrong)
        }

    }

    override fun copyLink(link: String?) {
        if (link != null) {
            val manager = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", Html.fromHtml(link).toString())
            manager.primaryClip = clipData
            showMessage(R.string.copied_to_clipboard)
        } else {
            showMessage(R.string.something_wrong)
        }
    }

    override fun openWithBrowser(link: String?) {
        if (link != null) {
            context?.let {
                CustomTabsHelper.openUrl(it, link)
            }
        } else {
            showMessage(R.string.something_wrong)
        }
    }

    // to change the title's font size of toolbar layout
    private fun setCollapsingToolbarLayoutTitle(title: String) {
        toolbar_layout.title = title
        toolbar_layout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        toolbar_layout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
        toolbar_layout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1)
        toolbar_layout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1)
    }

}
