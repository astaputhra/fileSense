package com.iMatch.etl.models;

/**
 * Created with IntelliJ IDEA.
 * User: daya
 * Date: 16/1/17
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IDuplicableDO {

    Object[] getKey();

    default boolean skipError() {
        return false;
    }
}
