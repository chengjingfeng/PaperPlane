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

package com.marktony.zhihudaily.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import com.bumptech.glide.Glide
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.customtabs.CustomTabsHelper
import com.marktony.zhihudaily.util.KEY_NIGHT_MODE

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * A preference fragment that displays the setting options and
 * about page.
 */

class InfoPreferenceFragment : PreferenceFragmentCompat() {

    companion object {

        private const val MSG_GLIDE_CLEAR_CACHE_DONE = 1

        fun newInstance(): InfoPreferenceFragment = InfoPreferenceFragment()

    }

    private val handler = Handler { message ->
        when (message.what) {
            MSG_GLIDE_CLEAR_CACHE_DONE -> showMessage(R.string.clear_image_cache_successfully)
            else -> {
            }
        }
        true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.info_preference)

        // Setting of night mode
        findPreference(KEY_NIGHT_MODE).setOnPreferenceChangeListener { p, o ->
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            activity?.window?.setWindowAnimations(R.style.WindowAnimationFadeInOut)
            activity?.recreate()
            true
        }

        // Clear the cache of glide
        findPreference("clear_glide_cache").setOnPreferenceClickListener { v ->
            Thread {
                Glide.get(context!!).clearDiskCache()
                handler.sendEmptyMessage(MSG_GLIDE_CLEAR_CACHE_DONE)
            }.start()
            true
        }

        // Rate
        findPreference("rate").setOnPreferenceClickListener { p ->
            try {
                val uri = Uri.parse("market://details?id=" + activity!!.packageName)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (ex: android.content.ActivityNotFoundException) {
                showMessage(R.string.something_wrong)
            }

            true
        }

        // Open the github contributors page
        findPreference("contributors").setOnPreferenceClickListener {
            context?.let {
                CustomTabsHelper.openUrl(it, getString(R.string.contributors_desc))
            }
            true
        }

        // Open the github links
        findPreference("follow_me_on_github").setOnPreferenceClickListener {
            context?.let {
                CustomTabsHelper.openUrl(it, getString(R.string.follow_me_on_github_desc))
            }
            true
        }

        // Open the zhihu links
        findPreference("follow_me_on_zhihu").setOnPreferenceClickListener {
            context?.let {
                CustomTabsHelper.openUrl(it, getString(R.string.follow_me_on_zhihu_desc))
            }
            true
        }

        // Feedback through sending an email
        findPreference("feedback").setOnPreferenceClickListener { p ->
            try {
                val uri = Uri.parse(getString(R.string.sendto))
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_topic))
                intent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.device_model) + Build.MODEL + "\n"
                                + getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n"
                                + getString(R.string.version))
                startActivity(intent)
            } catch (ex: android.content.ActivityNotFoundException) {
                showMessage(R.string.no_mail_app)
            }

            true
        }

        // Open the github home page
        findPreference("source_code").setOnPreferenceClickListener {
            context?.let {
                CustomTabsHelper.openUrl(it, getString(R.string.source_code_desc))
            }
            true
        }

        // Show the donation dialog
        findPreference("coffee").setOnPreferenceClickListener {
            val dialog = AlertDialog.Builder(context!!).create()
            dialog.setTitle(R.string.donate)
            dialog.setMessage(getString(R.string.donate_content))
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.positive)) { dialogInterface, i ->
                // add the alipay account to clipboard
                val manager = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", getString(R.string.donate_account))
                manager.primaryClip = clipData
                showMessage(R.string.copied_to_clipboard)
            }
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.negative)) { _, _ ->

            }
            dialog.show()
            true
        }

        // Show the open source licenses
        findPreference("open_source_license").setOnPreferenceClickListener {
            startActivity(Intent(activity, LicenseActivity::class.java))
            true
        }
    }

    private fun showMessage(@StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

}
