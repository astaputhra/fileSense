package com.iMatch.etl.orm;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Astaputhra on 19-03-2020.
 */
public interface IUploadErrors extends JpaRepository<UploadErrors, Long> {
}
