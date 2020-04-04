package com.iMatch.etl.orm;

import java.math.BigDecimal;

public interface IEntity extends IHexGenDictionaryBased {

    public void setId(BigDecimal id);

    public BigDecimal getId();
    
}
