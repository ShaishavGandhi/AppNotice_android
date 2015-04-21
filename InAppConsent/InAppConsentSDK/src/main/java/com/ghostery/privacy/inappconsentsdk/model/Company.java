package com.ghostery.privacy.inappconsentsdk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Company Class
 * Used to keep the data we are interested in from the XML file.
 */
public class Company {

    /*
     * An array of companies
     */
    public static List<CompanyData> COMPANY = new ArrayList<CompanyData>();

    /*
     * A map of companies by id
     */
    public static Map<String, CompanyData> COMPANY_MAP = new HashMap<String, CompanyData>();

    public void addCompany(CompanyData data) {
        // add company if it does not exist
        if (!COMPANY_MAP.containsKey(data.id.toString())) {
            COMPANY.add(data);
            COMPANY_MAP.put(data.id.toString(), data);
        }
    }

    public static class CompanyData implements Comparable<CompanyData> {

        public Integer id;
        public String name;
        public String logo;
        public String desc;
        public String collect;
        public String website;
        public String about;
        public String privacy;
        public String moo;
        public Boolean goToSite;
        public String daaDescription;
        public String daaLink;
        public List<String> category;
        public String method;
        public String requestBodyPOST;

        public CompanyData(Integer id, String name, String logo, String desc, String collect, String website, String about, String privacy, String moo,
                           List<String> category, Boolean goToSite, String daaDesc, String daaL, String method, String requestBodyPOST) {

            this.id = id;
            this.name = name;
            this.logo = logo;
            this.desc = desc;
            this.collect = collect;
            this.website = website;
            this.about = about;
            this.privacy = privacy;
            this.moo = moo;
            this.goToSite = goToSite;
            this.category = category;
            this.daaDescription = daaDesc;
            this.daaLink = daaL;
            this.method = method;
            this.requestBodyPOST = requestBodyPOST;
        }

        @Override
        public int compareTo(CompanyData another) {
            return this.name.compareToIgnoreCase(another.name);
        }
    }

}

