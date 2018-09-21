[![Release](https://jitpack.io/v/stanwood/Core_Framework_android.svg?style=flat-square)](https://jitpack.io/#stanwood/Core_Framework_android)

# Stanwood Core Framework (Android)

This set of general purpose utilities and base classes is used throughout the various projects we do at stanwood.

## Import

The stanwood Core Framework is hosted on JitPack. Therefore you can simply
import the modules by adding

```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

to your project's `build.gradle`.

Then add this to you app's `build.gradle`:

```groovy
dependencies {
    // aar version available as well
    implementation 'com.github.stanwood.Core_Framework_android:framework-base:<insert latest version here>'
    implementation 'com.github.stanwood.Core_Framework_android:framework-ui:<insert latest version here>'
    implementation 'com.github.stanwood.Core_Framework_android:framework-databinding:<insert latest version here>'
}
```
## Modules

_There are three modules: UI, data binding and general purpose (base) classes. All can be imported separately._

### Base

This module contains a collection of general purpose classes like an `IntentCreator` for common actions, or a helper class for hiding the on-screen keyboard.

### UI

The UI module mostly contains ViewGroups for easy View composition and positioning.

### Databinding

Currently the Databinding module mostly contains classes for performing data binding with `RecyclerView`s and `ViewPager`s. You can find appropriate adapters as well as `ViewHolder`s here.