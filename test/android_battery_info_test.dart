import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:android_battery_info/android_battery_info.dart';

void main() {
  const MethodChannel channel = MethodChannel('android_battery_info');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AndroidBatteryInfo.platformVersion, '42');
  });
}
