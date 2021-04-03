package com.abatra.android.allowance;

public enum ConsentStatus {
    REQUIRED {
        @Override
        public boolean canLoadAds() {
            return false;
        }
    },
    NOT_REQUIRED {
        @Override
        public boolean canLoadAds() {
            return true;
        }
    },
    OBTAINED {
        @Override
        public boolean canLoadAds() {
            return true;
        }
    },
    UNKNOWN {
        @Override
        public boolean canLoadAds() {
            return false;
        }
    };

    public abstract boolean canLoadAds();
}
