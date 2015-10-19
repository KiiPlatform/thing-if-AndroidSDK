adb shell am instrument -w -e package com.kii.thingif.largetests com.kii.thingif.largetests/pl.polidea.instrumentation.PolideaInstrumentationTestRunner
adb pull /data/data/com.kii.thingif.largetests/files/ junit-results/
