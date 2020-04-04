package com.iMatch.etl.orm;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class AbstractCommonsEntity implements IEntity {
    private static final long serialVersionUID = -7688533921144816171L;
    @Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tableBasedIdGenerator")
	@TableGenerator(name = "tableBasedIdGenerator", table = "SEQUENCE_TABLE", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", allocationSize = 100)

	@Column(name = "ID", precision = 10, scale = 0)
	private BigDecimal id;

	@Column(name = "PARENT_ID", precision = 10, scale = 0)
	private BigDecimal parentId = BigDecimal.ZERO;

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getParentId() {
		return parentId;
	}

	public void setParentId(BigDecimal parentId) {
		this.parentId = parentId;
	}

	public static List<BigDecimal> getIdList(Iterable<? extends AbstractCommonsEntity> abstractHexGenEntityList)
	{
		List<BigDecimal> idList = new ArrayList<BigDecimal>();
		if(abstractHexGenEntityList == null) return idList;
		for(AbstractCommonsEntity abstractHexGenEntity: abstractHexGenEntityList){
			idList.add(abstractHexGenEntity.getId());
		}
		return idList;
	}

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
