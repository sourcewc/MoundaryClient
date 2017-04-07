package com.team.moundary.fragments;

import android.app.Application;
import android.content.Context;

/**
 * Created by Shin on 2016-07-27.
 */
public class GirlsApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

/*        setDefaultFont(this, "DEFAULT", "NanumBarunGothicLight.ttf");
        setDefaultFont(this, "SANS_SERIF", "NanumBarunGothicLight.ttf");
        setDefaultFont(this, "SERIF", "NanumBarunGothicLight.ttf");*/

    }


    public static  Context getGirlsContext(){
        return mContext;
    }

 /*   public static void setDefaultFont(Context ctx, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }*/


}
