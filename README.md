# DingDingCalendarView
仿 钉钉请假 时间选择 控件

## 导入

```groovy
    allprojects {
      repositories {
        maven { url 'https://jitpack.io' }
      }

      dependencies {
            implementation 'com.github.huifeideyema:DingDingCalendarView:1.0.5'
    }
```
## 用法

```java
     DingCalendarViewDialogFragment dialogFragment = new DingCalendarViewDialogFragment();
            dialogFragment.setSelTimeListener(new DingCalendarViewDialogFragment.OnSelTimeListener() {
                @Override
                public void selTimeCallBack(String date) {

                }
            });
            dialogFragment.show(getSupportFragmentManager(), "dialogFragment");
```
	
