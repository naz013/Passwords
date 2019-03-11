package com.cray.software.passwords.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.FragmentOssBinding
import com.cray.software.passwords.modern_ui.NestedFragment

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

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

class OssFragment : NestedFragment() {

    private var binding: FragmentOssBinding? = null

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOssBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread {
            val text = readFile()
            binding!!.textView.post { binding!!.textView.text = text }
        }.start()
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        if (anInterface != null) {
            anInterface!!.setClick(null)
            anInterface!!.setTitle(getString(R.string.open_source_licenses))
        }
    }

    private fun readFile(): String? {
        if (activity == null) return null
        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            val input: InputStreamReader
            input = InputStreamReader(activity!!.assets.open("files/LICENSE.txt"))
            reader = BufferedReader(input)

            var line: String

            while ((line = reader.readLine()) != null) {
                sb.append(line)
                sb.append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return sb.toString()
    }

    companion object {

        val TAG = "OssFragment"

        fun newInstance(): OssFragment {
            return OssFragment()
        }
    }
}
