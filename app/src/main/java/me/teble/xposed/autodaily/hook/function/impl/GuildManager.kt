package me.teble.xposed.autodaily.hook.function.impl

import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.task.model.GuildInfo

open class GuildManager : BaseFunction(
    TAG = "GuildsManager"
) {
    override fun init() {
    }

    open fun getGuildInfoList(): List<GuildInfo>? {
        return arrayListOf(
            // TODO 待完善
            GuildInfo("MT管理器", "586834103978145428")
        )
    }
}