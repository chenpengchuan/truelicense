package de.schlichtherle.app.databases;

import de.schlichtherle.app.entity.LicenseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
public class LicenseQuery {

    @Autowired
    private LicenseRepository licenseRepository;

    public List<LicenseEntity> queryLicenseList(){
        Sort sort = new Sort(Sort.Direction.DESC,"notAfter");
        List<LicenseEntity> result = licenseRepository.findAll(sort);

        return result;
    }
    public Page<LicenseEntity> queryLicenseList(String user,int page,int limit){
        Sort sort = new Sort(Sort.Direction.DESC,"notAfter");
        Pageable pageable = new PageRequest(page,limit,sort);
        if(StringUtils.isEmpty(user)){
            user = "%";
        }else{
           user = "%"+user+"%";
        }
        Page<LicenseEntity> result=licenseRepository.findAllByUser(user+"%",pageable);
        return result;
    }
    public List<LicenseEntity> findByName(){
        Sort sort = new Sort(Sort.Direction.DESC,"notAfter");
        List<LicenseEntity> result = licenseRepository.findAll(sort);

        return result;
    }

    public Optional<LicenseEntity> findOneById(String id){
        Optional<LicenseEntity> result = licenseRepository.findById(id);
        return result;
    }
}
