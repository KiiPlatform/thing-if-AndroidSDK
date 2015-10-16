adb shell am instrument -w -e package com.kii.iotcloud.largetests com.kii.iotcloud.largetests/pl.polidea.instrumentation.PolideaInstrumentationTestRunner
adb pull /data/data/com.kii.iotcloud.largetests/files/ junit-results/
