package moon.dream.android_battery_info

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import android.widget.Toast



class AndroidBatteryInfoPlugin : MethodCallHandler, BroadcastReceiver() {
    private lateinit var context: Context
    private var temperature: Double? = null
    private val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    private val powerProfileClass = "com.android.internal.os.PowerProfile"

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = AndroidBatteryInfoPlugin()
            plugin.init(registrar)

            val channel = MethodChannel(registrar.messenger(), "android_battery_info")
            channel.setMethodCallHandler(plugin)
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
            "getCapacityByReflection" -> {
                getCapacityByReflection(result)
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

        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toDouble() / 10
    }

    private fun init(registrar: Registrar) {
        context = registrar.context()
    }

    private fun startListenTemperature(result: Result) {
        context.registerReceiver(this, filter)
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
        val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        if (capacity <= 0) {
            result.success(null)
            return
        }

        val value = (chargeCounter.toDouble() / capacity.toDouble()) * 100 / 1000

        result.success(value.toInt())
    }

    @SuppressLint("PrivateApi")
    private fun getCapacityByReflection(result: Result) {
        try {
            val profile = Class.forName(powerProfileClass)
                    .getConstructor(Context::class.java).newInstance(context)

            val capacity = Class
                    .forName(powerProfileClass)
                    .getMethod("getBatteryCapacity")
                    .invoke(profile) as Double

            result.success(capacity.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
            result.success(null)
        }
    }

    private fun getTemperature(result: Result) {
        result.success(temperature)
    }
}
