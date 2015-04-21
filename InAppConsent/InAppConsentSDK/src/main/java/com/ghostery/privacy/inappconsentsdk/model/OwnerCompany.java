package com.ghostery.privacy.inappconsentsdk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Used to store ad notice ids associated with a company in order to retrieve Owner Company ID (ocid param)
 */
public class OwnerCompany {

    public static Map<String, OwnerCompanyData> OWNER_COMPANY_MAP = new HashMap<String, OwnerCompanyData>();

    public void addOwnerCompany(OwnerCompanyData data) {
        if (!OWNER_COMPANY_MAP.containsKey(data.id.toString())) {
            OWNER_COMPANY_MAP.put(data.id.toString(), data);
        }
    }

    public static class OwnerCompanyData {

        public Integer id;
        public List<String> adNoticeId;

        public OwnerCompanyData(Integer companyId, List<String> adNoticeId) {
            this.id = companyId;
            this.adNoticeId = adNoticeId;
        }
    }
}
