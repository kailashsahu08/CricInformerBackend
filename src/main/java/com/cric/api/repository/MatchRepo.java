package com.cric.api.repository;

import com.cric.api.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepo extends JpaRepository<Match,Integer> {
        public Optional<Match> findByTeamHeading(String teamHeading);

}
