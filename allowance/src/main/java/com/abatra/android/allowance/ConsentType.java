package com.abatra.android.allowance;

import androidx.annotation.Nullable;

public enum ConsentType {
    NPA {
        @Override
        public String getAdmobNpaSettingValue() {
            return "1";
        }

        @Override
        public String getAdColonyNpaSettingValue() {
            return "0";
        }
    },
    PA {
        @Override
        public String getAdmobNpaSettingValue() {
            return null;
        }

        @Override
        public String getAdColonyNpaSettingValue() {
            return "1";
        }
    },
    UNKNOWN {
        @Override
        public String getAdmobNpaSettingValue() {
            return null;
        }

        @Override
        public String getAdColonyNpaSettingValue() {
            return null;
        }
    };



    @Nullable
    public abstract String getAdmobNpaSettingValue();

    @Nullable
    public abstract String getAdColonyNpaSettingValue();


}
