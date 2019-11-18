package moon.dream.android_battery_info

import android.content.BroadcastReceiver
import android.content.Context
import android.os.BatteryManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import android.content.Context.BATTERY_SERVICE
import android.os.Build
import android.content.Intent
import android.content.IntentFilter

class AndroidBatteryInfoPlugin : MethodCallHandler, BroadcastReceiver() {
    lateinit var context: Context
    var filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    private var temperature: Int? = null

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = AndroidBatteryInfoPlugin()
            plugin.init(registrar)

            val channel = MethodChannel(registrar.messenger(), "android_battery_info")
            channel.setMethodCallHandler(AndroidBatteryInfoPlugin())
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "startListenTemperature" -> {
                startListenTemperature(result)
            }
            "stopListenTemperature" -> {
                stopListenTemperature(result)
            }
            "getTemperature" -> {
                getTemperature(result)
            }
            "getCapacity" -> {
                getCapacity(result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }

        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
    }

    private fun init(registrar: Registrar) {
        context = registrar.context()
    }

    private fun startListenTemperature(result: Result) {
        context.registerReceiver(this ,filter)
        result.success(null)
    }

    private fun stopListenTemperature(result: Result) {
        context.unregisterReceiver(this)
        result.success(null)
    }

    private fun getCapacity(result: Result) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            result.success(null)
            return
        }

        val batteryManager = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        result.success(capacity)
    }

    private fun getTemperature(result: Result) {
        result.success(temperature)
    }
}
