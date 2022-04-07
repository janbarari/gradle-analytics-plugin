if (extra.has("initializationTime").not()) {
    println(java.time.Instant.now().toEpochMilli())
    extra.set("initializationTime", java.time.Instant.now().toEpochMilli())
}
rootProject.name = "gradle-analytics-plugin"
include(":core")
