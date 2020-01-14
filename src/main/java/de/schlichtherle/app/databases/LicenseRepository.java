package de.schlichtherle.app.databases;

import de.schlichtherle.app.entity.LicenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface LicenseRepository extends CommonRepostory<LicenseEntity,String>{

    @Query("select t from LicenseEntity t where t.user like ?1")
    Page<LicenseEntity> findAllByUser(String user, Pageable pageable);

    @Query("select t from LicenseEntity t where t.sid like ?1")
    Page<LicenseEntity> findPageBySid(String sid, Pageable pageable);
}
