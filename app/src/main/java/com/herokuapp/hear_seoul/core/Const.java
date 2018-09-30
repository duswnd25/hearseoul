/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core;

public class Const {
    public static class INTENT_EXTRA {
        public static final String LOCATION = "location";
        public static final String SPOT = "spot";
        public static final String IS_NEW_INFORMATION = "is_new_information";
        public static final String OBJECT_ID = "objectId";
        public static final String EVENT = "events";
    }

    public static class DB {
        public static final String DB_NAME = "data";
        public static final int VERSION = 1;
    }

    public static class PREFERENCE {
        public static final String SAVED_LOCATION = "saved_location";
        public static final int INFLUENCER_NEAR_BY_DISTANCE = 5000;
        public static final int INFLUENCER_NEAR_BY_COUNT = 5;
    }

    public static class BAAS {
        public static class SUGGESTION {
            public static final String TABLE_NAME = "Suggestion";
            public static final String ID = "id";
            public static final String SUGGEST = "suggest";
        }

        public static class SPOT {
            public static final String ID = "id";
            public static final String TABLE_NAME = "Spot";
            public static final String TITLE = "title";
            public static final String TIME = "time";
            public static final String DESCRIPTION = "description";
            public static final String LOCATION = "location";
            public static final String IMG_SRC = "img_src";
            public static final String ADDRESS = "address";
            public static final String PHONE = "phone";
            public static final String TAG = "tag";
            public static final String IS_INFLUENCER = "influencer";
        }

        public static class REVIEW {
            public static final String TABLE_NAME = "Review";
            public static final String TITLE = "title";
            public static final String CONTENT = "content";
            public static final String RATE = "rate";
            public static final String IMG_LIST = "image_list";
            public static final String SPOT_ID = "spot_id";
            public static final String UPLOADER = "uploader";
        }
    }
}
