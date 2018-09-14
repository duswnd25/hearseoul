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
    }

    public static class PREFERENCE {
        public static final String SAVED_LOCATION = "saved_location";
    }

    public static class BAAS {
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
