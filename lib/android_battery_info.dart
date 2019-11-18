import 'dart:async';

import 'package:flutter/services.dart';

/// Android battery info provides capacity and temperature
class AndroidBatteryInfo {
  static const MethodChannel _channel =
      const MethodChannel('android_battery_info');

// TODO: Steam to notify
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

  /// Cancel listening battery temperature
  static Future<void> stopListenTemperature() async {
    await _channel.invokeMethod('stopListenTemperature');
  }

  /// Get battery temperature
  static Future<double> get temperature async {
    return await _channel.invokeMethod('getTemperature');
  }

  /// Get battery capacity (mAh)
  static Future<int> get capacity async {
    return await _channel.invokeMethod('getCapacity');
  }

  /// Get battery capacity by reflection (mAh)
  static Future<int> get capacityByReflection async {
    return await _channel.invokeMethod('getCapacityByReflection');
  }
}
