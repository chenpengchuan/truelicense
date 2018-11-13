package de.schlichtherle.app.databases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface CommonRepostory<T, ID extends Serializable> extends JpaRepository<T, ID> {

}
