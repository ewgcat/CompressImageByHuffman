package com.lishuaihua.imageselector.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;

public class ImgLanguageUtils {

    public static final String packageName = "com.language";
    public static final String LANGUAGE = "language";
    public static final String LANGUAGE_EN = "en-US";
    public static final String LANGUAGE_HI_IN = "hi-IN"; //印度

    private static Context getContext(Context context) {
          return context;
    }

    public static void putString(Context context, String key, String dValue) {
        SharedPreferences sharedPreferences = getContext(context).getSharedPreferences(packageName, Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString(key, dValue);
        editor.commit();//提交修改
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = getContext(context).getSharedPreferences(packageName, Context.MODE_PRIVATE); //私有数据
        return sharedPreferences.getString(key, "");
    }

    public static Context selectLanguage(Context context, String language) {
        Context sourceContext = getContext(context);
        Context updateContext;
        //设置语言类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateContext = createConfigurationResources(sourceContext, language);
        } else {
            applyLanguage(sourceContext, language);
            updateContext = sourceContext;
        }
        putString(sourceContext, LANGUAGE, language);
        return updateContext;
    }

    //    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context, String language) {
        Context mContext = getContext(context);
        //设置语言类型
        Resources resources = mContext.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = null;
        switch (language) {
            case LANGUAGE_EN:
                locale = Locale.ENGLISH;
                break;
            case LANGUAGE_HI_IN:
                locale = new Locale("hi", "IN");
                break;
            default:
                getSystemLocale(mContext);
                break;
        }

        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
            mContext = mContext.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return mContext;
    }

    private static Locale getSystemLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return LocaleList.getDefault().get(0);
        } else {
            return Locale.getDefault();
        }
    }

    private static void applyLanguage(Context context, String language) {
        //设置语言类型
        Resources resources = getContext(context).getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = null;
        switch (language) {
            case LANGUAGE_EN:
                locale = Locale.ENGLISH;
                break;
            case LANGUAGE_HI_IN:
                locale = new Locale("hi", "IN");
                break;
            default:
                locale = Locale.getDefault();
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale);
        } else {
            // updateConfiguration
            configuration.locale = locale;
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }
    }

    public static Context updateLanguage(Context context) {
        Context ctx = getContext(context);
        String curLanguage = getString(ctx, LANGUAGE);
        if (null == curLanguage || TextUtils.isEmpty(curLanguage)) {
            Locale locale = getSystemLocale(ctx);
            curLanguage = locale.getLanguage();
            if (TextUtils.equals(curLanguage, "hi")) {
                curLanguage = LANGUAGE_HI_IN;
            } else {
                curLanguage = LANGUAGE_EN;
            }
        }
        return selectLanguage(ctx, curLanguage);
    }

    public static boolean switchLanguage(Context context, String value) {
        Context ctx = getContext(context);
        String curLanguage = getString(ctx, LANGUAGE);
        if (value.equals(curLanguage) || TextUtils.isEmpty(value)) {
            return false;
        }
        if (TextUtils.equals(value, LANGUAGE_HI_IN)) {
            putString(ctx, LANGUAGE, LANGUAGE_HI_IN);
        } else {
            putString(ctx, LANGUAGE, LANGUAGE_EN);
        }
        selectLanguage(ctx, getString(ctx, LANGUAGE));
        return true;
    }

}
