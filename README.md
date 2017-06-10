<div align="center">
  <img src="logo.png" width="250" height="250"/>
  <h1>IIFYM - Calorie Counter</h1>
  <!-- Travis -->
  <a href="https://travis-ci.org/Karim94/IIFYM">
    <img alt="Build Status" src="https://travis-ci.org/Karim94/IIFYM.svg?branch=master">
  </a>
  <!-- License -->
  <a href="https://github.com/Karim94/IIFYM/blob/master/LICENSE">
    <img alt="license: GPL-3.0"
      src="https://img.shields.io/github/license/karim94/iifym.svg">
  </a>
  <br>
  <!-- Google Play -->
  <a href='https://play.google.com/store/apps/details?id=com.karimchehab.IIFYM&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
    <img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/>
  </a>
</div>

## Prerequisites

* JDK 1.8
* JRE 1.8
* Android Studio 2.3

## Development

1. Clone repository locally
2. Open cloned repository with Android Studio

* Before committing, ensure the build is not broken and linted

```
bash ./gradlew assemble
bash ./gradlew lint --stacktrace
bash ./gradlew check
```
