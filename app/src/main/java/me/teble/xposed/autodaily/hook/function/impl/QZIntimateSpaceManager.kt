package me.teble.xposed.autodaily.hook.function.impl

import com.github.kyuubiran.ezxhelper.utils.findMethod
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.utils.new
import mqq.app.AppRuntime
import java.lang.reflect.Method

open class QZIntimateSpaceManager : BaseFunction(
    TAG = "QZIntimateSpaceManager"
) {
    lateinit var viewModel: Any
    lateinit var checkInMethod: Method

    override fun init() {
        val viewModelClz = load("Lcom/qzone/reborn/intimate/viewmodel/QZIntimateCheckInViewModel;")!!
        viewModel = viewModelClz.new()
        checkInMethod = findMethod(viewModelClz) {
            val types = parameterTypes
            if (types.size != 2) return@findMethod false
            val oldParam = types[0] == String::class.java && types[1] == AppRuntime::class.java // 9.2.30
            val newParam = types[0] == AppRuntime::class.java && types[1] == String::class.java // 9.2.80
            oldParam || newParam
        }
    }

    open fun doCheckInRequest(spaceId: String) {
        checkInMethod.also { m ->
            if (m.parameterTypes[0] == String::class.java) {
                m.invoke(viewModel, spaceId, QApplicationUtil.appRuntime)
            } else {
                m.invoke(viewModel, QApplicationUtil.appRuntime, spaceId)
            }
        }
    }
}