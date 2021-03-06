package com.adw1n.hubtraffic.respositories;

import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryClones;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GithubRepositoryClonesRepository extends CrudRepository<GithubRepositoryClones, Long> {
    List<GithubRepositoryClones> findByRepository(GithubRepository repository);
    GithubRepositoryClones findByRepositoryAndTimestamp(GithubRepository repo, Date timestamp);
}
