/**
 * 
 */
package com.iMatch.etl.enums;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Chandru
 * /*
    Modified By Subhash, Added the User roles to the enums.
 */

public enum RoleGroup {
    /**
     * Role Group - Front Office
     */
    FOS(new String[] {"PMU","DLR","CMO","CIO"}),

    /**
     * Role Group - Back Office
     */
    BOS(new String[]{"BOM","BOE","MOE"}),

    /**
     * Role Group - Both
     */

    BOTH(new String[]{"CEO","CMS","SYS","COO","DUM"}),

    /**
     * Role Group - Support, no domain functions or restricted db access
     * Add roles that should connect with restricted access here for e.g. that of support user
     */
    SUPP(new String[]{"EXTERN"});


    private String userRole[];

    RoleGroup(String userRole[])
    {
        this.userRole = userRole;
    }

    public String[] getUserRole() {
        return userRole;
    }

    public static String getUserRoleGroupByUserRole(String userRole)
    {
        for(RoleGroup roleGroup:RoleGroup.values())
        {
            if ( ArrayUtils.contains(roleGroup.getUserRole(), userRole) )
            {
                return roleGroup.name();
            }
        }
        return null;
    }

}
