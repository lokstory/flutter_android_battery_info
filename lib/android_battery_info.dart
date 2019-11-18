import 'dart:async';

import 'package:flutter/services.dart';

class AndroidBatteryInfo {
  static const MethodChannel _channel =
      const MethodChannel('android_battery_info');

//  int _temperature = null;
//  Stream<int> temperature = Stream.fromFuture(future);

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// Listening battery temperature
  static Future<void> startListenTemperature() async {
    await _channel.invokeMethod('startListenTemperature');
  }

  /// cancel listening battery temperature
  static Future<void> stopListenTemperature() async {
    await _channel.invokeMethod('stopListenTemperature');
  }

  /// Get battery temperature
  Future<String> get temperature async {
    return await _channel.invokeMethod('getTemperature');
  }

  /// Get battery temperature
  static Future<int> get capacity async {
    return await _channel.invokeMethod('getCapacity');
  }
}
