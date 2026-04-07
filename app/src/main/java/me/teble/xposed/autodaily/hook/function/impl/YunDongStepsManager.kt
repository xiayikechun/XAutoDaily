package me.teble.xposed.autodaily.hook.function.impl

import com.github.kyuubiran.ezxhelper.utils.buildJSONArray
import com.github.kyuubiran.ezxhelper.utils.buildJSONObject
import com.tencent.mobileqq.vashealth.websso.WebSSOAgent
import me.teble.xposed.autodaily.config.MsfService
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.hook.utils.WupUtil
import me.teble.xposed.autodaily.task.util.CalculationUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.new
import mqq.app.Packet

open class YunDongStepsManager : BaseFunction(
    TAG = "YunDongStepsManager"
) {
    override fun init() {
    }

    open fun reportSteps(steps: Int) {
        val packet = Packet::class.java.new("$currentUin")
        val req = WebSSOAgent.UniSsoServerReq().apply {
            reqdata.set(buildJSONObject {
                put("oauth_consumer_key", 1002)
                put("data", buildJSONArray {
                    put(buildJSONObject {
                        put("type", 1)
                        put("time", CalculationUtil.getSecondTime())
                        put("steps", steps)
                    })
                })
                put("lastRecordTime", 0)
                put("mode", 1)
                put("stepSource", 0)
                put("foreground", 1)
            }.toString())
        }
        packet.let {
            it.setSSOCommand("yundong_report.steps")
            LogUtil.d("req.toByteArray(): ${req.toByteArray().joinToString("") { "%02X".format(it) }}")
            it.putSendData(WupUtil.encode(req.toByteArray()))
            it.setTimeout(9999L)
        }
        val toServiceMsg = packet.toMsg()
        toServiceMsg.let {
            it.appId = 537344683
            it.appSeq = 1278
            it.serviceName = MsfService
        }
        QApplicationUtil.send(toServiceMsg)
    }
}