### 无名杀`Android`版

无名杀运行在WebView内部，更新版本、安装扩展通过JavaScript脚本运行，如果更新、导入扩展导致运行crash，会导致浏览器
无响应，这时候无法进行恢复或者禁用扩展，清除数据会导致游戏数据丢失。因此开发此应用用于扩展原有网页版的功能，除了WebView
原有的功能外，可以实现原生Android的功能，例如更换图标、通过JavaScriptInterface在WebView内部调用Java原生方法来播放
声音、播放动画、文件管理、创建联机服务器等。

#### 编译方法
`git clone`项目后，使用Android studio打开目录`noname-android\noname\platforms\android`
等待自动导入并且gradle脚本执行完成，执行 gradle -> app -> install -> installDebug脚本即可
运行后默认是不带资源文件的，需要安装另一个模块`lib_assets`,执行 gradle -> lib_assets -> install -> installDebug脚本即可

安装后桌面会有两个应用，打开noname即可自动从lib_assets导入素材

#### lib_assets
该模块是独立的application，同时将无名杀原仓库作为项目的submodule放到了assets/resource文件夹，git pull一次以后即可
同步仓库，然后编译apk实现资源的更新，同时也可以自行替换目录内的内容，实现个人资源的发布与分享

#### 如何扩展功能
项目右键 new -> module，建议使用如下目标的配置
名称：module_xxx
包名：online.nonamekill.android.module.xxx

1、编辑module的`AndroidManifest.xml`文件，替换package为module的包名，否则引用R.layout等资源是会有问题
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="online.nonamekill.android.module.xxx">

</manifest>
```

2、编辑`build.gradle`文件，添加lib_common模块的引用
```groovy
dependencies {
    // ...

    implementation project(":lib_common")
}
```
3、创建ModuleXXX继承BaseModule，例如关于模块
getName返回的数据会显示在container的左侧选项中，点击后展开的UI通过getView方法返回
```java
public class ModuleAbout extends BaseModule {

    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_about_layout, null);

        return view;
    }

    @Override
    public String getName() {
        return "关于";
    }
}
```

