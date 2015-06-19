//package com.ghostery.privacy.inappconsentsdk.model;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//
///*
// * Stores the relevant opt out data when network
// * connection is not available and user opted out
// */
//public class Optout {
//
//    /*
//     * A map of companies by id
//     */
//    public static Map<String, OptoutData> OPTOUT_MAP = new HashMap<String, OptoutData>();
//
//    public void addOptout(OptoutData data) {
//        // add company if it does not exist
//        if (!OPTOUT_MAP.containsKey(data.companyId.toString())) {
//            OPTOUT_MAP.put(data.companyId.toString(), data);
//        }
//    }
//
//    public static class OptoutData implements Serializable {
//
//        private static final long serialVersionUID = 2441736;
//
//        public Integer companyId;
//        public boolean optoutStatus;
//        public boolean storeOptout;
//        public boolean storeOptoutAll;
//
//        public OptoutData(Integer companyId) {
//            this.companyId = companyId;
//            this.optoutStatus = false;
//            this.storeOptout = false;
//            this.storeOptoutAll = false;
//        }
//
//    }
//
//}
