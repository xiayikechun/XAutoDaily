package me.teble.xposed.autodaily.hook.function.impl

import com.github.kyuubiran.ezxhelper.utils.argTypes
import com.github.kyuubiran.ezxhelper.utils.method
import com.tencent.qqnt.kernelgpro.nativeinterface.GProGuild
import com.tencent.qqnt.kernelgpro.nativeinterface.IGProGetGuildsInContactCallback
import me.teble.xposed.autodaily.hook.base.hostClassLoader
import me.teble.xposed.autodaily.hook.base.loadAs
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.task.model.GuildInfo
import me.teble.xposed.autodaily.utils.invoke
import java.lang.reflect.Proxy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

open class GuildManager : BaseFunction(
    TAG = "GuildsManager"
) {
    lateinit var guildService: Any

    override fun init() {
        val appRuntime = QApplicationUtil.appRuntime
        val session = appRuntime.getRuntimeService(loadAs("com.tencent.mobileqq.qqguildsdk.api.IGProSession"), "")
        guildService = session.invoke("getGuildService")!!
    }

    @Suppress("UNCHECKED_CAST")
    open fun getGuildInfoList(): List<GuildInfo>? {
        val guilds = mutableListOf<GProGuild>()
        guildService.let { service ->
            val countDownLatch = CountDownLatch(1)
            val callbackClass = IGProGetGuildsInContactCallback::class.java
            val callback = Proxy.newProxyInstance(hostClassLoader, arrayOf(callbackClass)) { _, method, args ->
                val createdGuilds = args[0] as ArrayList<GProGuild>
                guilds.addAll(createdGuilds)
                val managedGuilds = args[1] as ArrayList<GProGuild>
                guilds.addAll(managedGuilds)
                val joinedGuilds = args[2] as ArrayList<GProGuild>
                guilds.addAll(joinedGuilds)
                countDownLatch.countDown()
            }
            val getGuildsMethod = service.method("getGuildsInContact", argTypes = argTypes(callbackClass))
            getGuildsMethod.invoke(service, callback)
            countDownLatch.await(10, TimeUnit.SECONDS)
        }
        return guilds.map {
            GuildInfo(
                (it.guildId).toString(),
                it.guildInfo.guildName
            )
        }
    }
}