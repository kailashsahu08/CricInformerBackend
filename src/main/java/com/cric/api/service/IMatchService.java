package com.cric.api.service;

import com.cric.api.entity.Match;

import java.util.List;
import java.util.Map;

public interface IMatchService {
    //get All Matches
    public List<Match> getAllMatches();;
    //Get Live Message
    public List<Match> getLiveMatches();
    //Get Point Table
    public List<List<String>> getPointTable();
}
