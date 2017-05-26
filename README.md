# MaterialDesignProgressView
原理超简单的进度条:material design 风格 进度条
 * 支持功能：
 * 自定义颜色  
 * 自定义宽度  
 * 自定义速度，最快是15 目前有三档 slow,medium,quick 
 * 可设置开始运行或者暂停  
 * 自定义半径 
 * 自定义开始位置默认为270度
 
 ## 集成方式
 
 - Step1. Add it in your root build.gradle at the end of repositories:
  ```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 ```
 - Step 2. Add the dependency
 ```
	dependencies {
	        compile 'com.github.guohaiyang1992:MaterialDesignProgressView:1.0'
	}

```
