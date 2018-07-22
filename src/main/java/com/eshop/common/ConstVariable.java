package com.eshop.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by windvalley on 2018/7/14.
 */
public class ConstVariable {
    public static final String CURRENTUSER = "currentUser";

    public static final String USERNAME = "userName";
    public static final String EMAIL = "email";

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }
}
