package me.teble.xposed.autodaily.hook.function.impl

import com.github.kyuubiran.ezxhelper.utils.invokeMethod
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.utils.new
import mqq.app.AppRuntime

open class QZIntimateSpaceManager : BaseFunction(
    TAG = "QZIntimateSpaceManager"
) {
    lateinit var viewModel: Any

    override fun init() {
        val viewModelClz = load("Lcom/qzone/reborn/intimate/viewmodel/QZIntimateCheckInViewModel;")!!
        viewModel = viewModelClz.new()
    }

    open fun doCheckInRequest(spaceId: String) {
        viewModel.invokeMethod(QApplicationUtil.appRuntime, spaceId) {
            val paramTypes = parameterTypes
            paramTypes.size == 2 && paramTypes[0] == AppRuntime::class.java && paramTypes[1] == String::class.java
        }
    }
}