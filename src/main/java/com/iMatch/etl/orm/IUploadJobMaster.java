package com.iMatch.etl.orm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Astaputhra on 19-03-2020.
 */
public interface IUploadJobMaster extends JpaRepository<UploadJobMaster, Long> {

    public List<UploadJobMaster> findByChecksum(String checksum);

}
