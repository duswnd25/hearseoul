package com.herokuapp.hear_seoul.core;

public class Const {
    public static class BAAS {
        public static class SPOT {
            public static final String TABLE_NAME = "Spot";
            public static final String TITLE = "title";
            public static final String DESCRIPTION = "description";
            public static final String LOCATION = "location";
            public static final String VISIT = "visit";
            public static final String IMG_SRC = "img_src";
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
