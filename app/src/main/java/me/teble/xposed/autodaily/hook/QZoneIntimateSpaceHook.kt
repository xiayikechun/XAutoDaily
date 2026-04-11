package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.utils.ClipboardUtil
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.getFields

class QZoneIntimateSpaceHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain

    override val enabled: Boolean
        get() = true

    @MethodHook("亲密空间ID复制")
    private fun spaceIdCopyHook() {
        val coverPartClz = load("Lcom/qzone/reborn/intimate/part/QZoneIntimateSpaceCoverPart;")!!
        val viewModelClz = load("Lcom/qzone/reborn/intimate/viewmodel/QZIntimateCheckInViewModel;")!!
        findMethod(coverPartClz) { name == "onInitView" }.hookAfter { param ->
            val instance = param.thisObject
            runCatching {
                val viewModel = instance.fieldValueAs<Any>(viewModelClz, false)!!
                viewModel.getFields(false)
                    .asSequence()
                    .filter { it.type == String::class.java }
                    .onEach { it.isAccessible = true }
                    .mapNotNull { it.get(viewModel) as? String }
                    .singleOrNull { it.isNotBlank() }
                    ?: error("获取spaceId失败")
            }.onSuccess { spaceId ->
                ClipboardUtil.copy(spaceId)
                ToastUtil.send("复制spaceId成功")
            }.onFailure {
                ToastUtil.send("获取spaceId失败")
            }
        }
    }
}