/*
 * Copyright (c) 2019. Dylan Cai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dylanc.loadinghelper.sample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.dylanc.loadinghelper.LoadingHelper
import com.dylanc.loadinghelper.ViewType

/**
 * 一种封装思路，用法更接近 BaseActivity，考虑的是把 BaseActivity 改成 BaseFragment 时尽量少改代码。
 *
 * 使用该基类时注意以下事项：
 * 重写 onCreate 方法并调用 setContentView 方法设置布局，这个写法与 Activity 保持一致。
 * 控件初始化或逻辑代码建议写在 onActivityCreated 中。
 *
 * @author Dylan Cai
 */
@Suppress("unused")
abstract class BaseFragment : androidx.fragment.app.Fragment() {

  lateinit var loadingHelper: LoadingHelper
    private set
  private var layoutResID: Int = 0
  private var contentViewId: Int = 0
  private var contentAdapter: LoadingHelper.ContentAdapter<*>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val rootView = inflater.inflate(layoutResID, container, false)
    return if (contentViewId > 0) {
      val contentView = rootView.findViewById<View>(contentViewId)
      loadingHelper = LoadingHelper(contentView, contentAdapter)
      loadingHelper.setOnReloadListener(this::onReload)
      rootView
    } else {
      loadingHelper = LoadingHelper(rootView, contentAdapter)
      loadingHelper.setOnReloadListener(this::onReload)
      loadingHelper.decorView
    }
  }

  // 如果有默认的 ContentAdapter，参数中的 null 改成创建该对象，比如把第三个参数改成：
  // contentAdapter: LoadingHelper.ContentAdapter<*>? = DefContentAdapter()
  @JvmOverloads
  fun setContentView(
    @LayoutRes layoutResID: Int,
    @IdRes contentViewId: Int = 0,
    contentAdapter: LoadingHelper.ContentAdapter<*>? = null
  ) {
    this.layoutResID = layoutResID
    this.contentViewId = contentViewId
    this.contentAdapter = contentAdapter
  }

  @JvmOverloads
  fun setToolbar(
    title: String, type: NavIconType = NavIconType.NONE,
    menuId: Int = 0, listener: ((MenuItem) -> Boolean)? = null
  ) =
    setToolbar(ToolbarConfig(title, type, menuId, listener))

  private fun setToolbar(config: ToolbarConfig) {
    val toolbarAdapter: BaseToolbarAdapter<ToolbarConfig, *> =
      loadingHelper.getAdapter(ViewType.TITLE)
    toolbarAdapter.config = config
    loadingHelper.setDecorHeader(ViewType.TITLE)
  }

  fun showLoadingView() = loadingHelper.showLoadingView()

  fun showContentView() = loadingHelper.showContentView()

  fun showErrorView() = loadingHelper.showErrorView()

  fun showEmptyView() = loadingHelper.showEmptyView()

  fun showCustomView(viewType: Any) = loadingHelper.showView(viewType)

  open fun onReload() {}
}