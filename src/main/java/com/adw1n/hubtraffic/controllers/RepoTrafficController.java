package com.adw1n.hubtraffic.controllers;

import com.adw1n.hubtraffic.models.GithubRepository;
import com.adw1n.hubtraffic.models.GithubRepositoryClones;
import com.adw1n.hubtraffic.models.GithubRepositoryViews;
import com.adw1n.hubtraffic.models.GithubUser;
import com.adw1n.hubtraffic.respositories.GithubRepositoryClonesRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryRepository;
import com.adw1n.hubtraffic.respositories.GithubRepositoryViewsRepository;
import com.adw1n.hubtraffic.utils.GithubAPI;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Data
@RestController
public class RepoTrafficController {
    @Autowired
    private final GithubRepositoryRepository githubRepositoryRepository;
    @Autowired
    private final GithubRepositoryViewsRepository githubRepositoryViewsRepository;
    @Autowired
    private final GithubRepositoryClonesRepository githubRepositoryClonesRepository;

    @RequestMapping(value = "/api/repository/views/{repositoryName}", method = RequestMethod.GET)
    public ResponseEntity<List<GithubRepositoryViews>> getViews(@PathVariable("repositoryName") String repositoryName,
                                                                Principal principal){
        GithubUser user = GithubAPI.getUser(principal);
        if(user==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        GithubRepository githubRepository = githubRepositoryRepository.findByNameAndUser(repositoryName, user);
        if(githubRepository==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        List<GithubRepositoryViews> repoViews = githubRepositoryViewsRepository.findByRepository(githubRepository);
        return ResponseEntity.ok(repoViews);
    }

    @RequestMapping(value = "/api/repository/clones/{repositoryName}", method = RequestMethod.GET)
    public ResponseEntity<List<GithubRepositoryClones>> getClones(@PathVariable("repositoryName") String repositoryName,
                                                                 Principal principal){
        GithubUser user = GithubAPI.getUser(principal);
        if(user==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        GithubRepository githubRepository = githubRepositoryRepository.findByNameAndUser(repositoryName, user);
        if(githubRepository==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        List<GithubRepositoryClones> repoClones = githubRepositoryClonesRepository.findByRepository(githubRepository);
        return ResponseEntity.ok(repoClones);
    }

    @Data
    static class RepoTraffic{
        private GithubRepository repository;
        private List<GithubRepositoryViews> repositoryViews;
        private List<GithubRepositoryClones> repositoryClones;

        public RepoTraffic(GithubRepository repository, List<GithubRepositoryViews> repositoryViews, List<GithubRepositoryClones> repositoryClones) {
            this.repository = repository;
            this.repositoryViews = repositoryViews;
            this.repositoryClones = repositoryClones;
        }
    }
    @RequestMapping(value = "/api/repository/traffic", method = RequestMethod.GET)
    public ResponseEntity<List<RepoTraffic>> getTraffic(Principal principal){
        GithubUser user = GithubAPI.getUser(principal);
        if(user==null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        List<GithubRepository> repos = githubRepositoryRepository.findByUser(user);
        if(repos.isEmpty())
            GithubAPI.fetchUpdates(user);
        List<RepoTraffic> ans = new ArrayList<>();

        for(GithubRepository repo: repos){
            List<GithubRepositoryViews> repositoryViews = githubRepositoryViewsRepository.findByRepository(repo);
            List<GithubRepositoryClones> repositoryClones = githubRepositoryClonesRepository.findByRepository(repo);

            RepoTraffic trafficStats = new RepoTraffic(repo, repositoryViews, repositoryClones);
            ans.add(trafficStats);
        }
        return ResponseEntity.ok(ans);
    }

}
