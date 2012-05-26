/*
 * 使用GreenDroid的App必须添加这样一个类，并将其用作AndroidManifest中声明Application的name属性
 * 每个App只需一个这样的类
 * 主要功能是保存Home键的目标Activity，添加App Logo的目标网址
 * 由于Home键被自定义，因此该类目前没有任何作用
 */

package edu.tongji.fiveidiots.ui;

import greendroid.app.GDApplication;


public class IDoItApplication extends GDApplication {
    @Override
    public Class<?> getHomeActivityClass() {
        return IDoItActivity.class;
    }
}
