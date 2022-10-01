# Getting Report
Gradle Analytics Plugin uses daily basis data to generate reports. So you could use the below instructions to generate your build analysis.

<strong>Execute Gradle Task</strong><br/>
```Gradle
./gradlew reportAnalytics --task="REQUESTED_TASK" --branch="BRANCH_NAME" --period="can be like today, s:yyyy/MM/dd,e:yyyy/MM/dd, 1y, 4m, 38d, 3m 06d"
```

<br/>
!!! Note ""
    
    <strong>--period Examples</strong><br/>

    - <strong>today</strong> - Generates report only for the current day.
    - <strong>1d</strong> - Generates report from 1 day ago till now.
    - <strong>1m 3d</strong> - Generates report from 1 month and 3 days ago till now.
    - <strong>1y</strong> - Generates report from 1 year ago till now.
    - <strong>s:2022/03/24,e:2022/04/25</strong> - Generates report from `2022/03/24` till `2022/04/25`.
    

    Plugin only holds the metrics results in the caching database up to one year.

<br/>

!!! warning

    If using <a href="https://docs.gradle.org/current/userguide/configuration_cache.html">`configuration-cache`</a> make sure to put `org.gradle.unsafe.configuration-cache-problems=warn
    ` into the `gradle.properties` file or run <strong>`reportAnalytics`</strong> task with `--configuration-cache-problems=warn`.


<br/>

To understand the metrics and report that plugin provides, It is required to understand Gradle basics and how this build
system works.<br /><a href="https://docs.gradle.org/current/userguide/what_is_gradle.html" target="_blank">https://docs.gradle.org/current/userguide/what_is_gradle.html</a>
<br/>

<br/>
