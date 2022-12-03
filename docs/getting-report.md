<!--
 MIT License
 Copyright (c) 2022 Mehdi Janbarari (@janbarari)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
-->

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

To understand the metrics and report that plugin provides, It is required to understand Gradle basics and how this build
system works.<br /><a href="https://docs.gradle.org/current/userguide/what_is_gradle.html" target="_blank">https://docs.gradle.org/current/userguide/what_is_gradle.html</a>
<br/>

<br/>
