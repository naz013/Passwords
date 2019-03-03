package com.cray.software.passwords.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cray.software.passwords.R
import com.cray.software.passwords.cloud.DropboxLogin
import com.cray.software.passwords.cloud.GoogleLogin
import com.cray.software.passwords.databinding.FragmentCloudBinding
import com.cray.software.passwords.fragments.NestedFragment

/**
 * Copyright 2017 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class CloudFragment : NestedFragment(), GoogleLogin.LoginCallback, DropboxLogin.LoginCallback {

    private var binding: FragmentCloudBinding? = null
    private var mGoogleLogin: GoogleLogin? = null
    private var mDropboxLogin: DropboxLogin? = null

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCloudBinding.inflate(inflater, container, false)

        if (activity != null) mDropboxLogin = DropboxLogin(activity!!, this)
        mGoogleLogin = GoogleLogin(activity, this)

        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.linkGDrive.setOnClickListener { view1 ->
            if (mGoogleLogin!!.isLogged)
                mGoogleLogin!!.logOut()
            else
                mGoogleLogin!!.login()
        }
        binding!!.linkDropbox.setOnClickListener { view12 -> mDropboxLogin!!.login() }
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (anInterface != null) {
            anInterface!!.setClick(null)
            anInterface!!.setTitle(getString(R.string.cloud_drives_settings_title))
        }
        mDropboxLogin!!.checkDropboxStatus()
    }

    override fun onSuccess() {
        binding!!.linkGDrive.text = getString(R.string.logout_button)
    }

    override fun onFail() {
        binding!!.linkGDrive.text = getString(R.string.login_button)
    }

    override fun onSuccess(logged: Boolean) {
        if (logged) {
            binding!!.linkDropbox.text = getString(R.string.logout_button)
        } else {
            binding!!.linkDropbox.text = getString(R.string.login_button)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mGoogleLogin!!.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        val TAG = "CloudFragment"

        fun newInstance(): CloudFragment {
            return CloudFragment()
        }
    }
}
