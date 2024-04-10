package com.cric.api.service;
import com.cric.api.entity.Match;
import com.cric.api.entity.MatchStatus;
import com.cric.api.repository.MatchRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class MatchServiceImpl implements IMatchService {
    @Autowired
    private MatchRepo matchRepo;
    @Override
    public List<Match> getAllMatches() {
        return matchRepo.findAll();
    }

    @Override
    public List<Match> getLiveMatches() {
        List<Match> matches = new ArrayList<>();
        try{
            String url = "https://www.espncricinfo.com/live-cricket-score";
            Document document = Jsoup.connect(url).get();
            Elements matchData = document.select("div[class=\"ds-px-4 ds-py-3\"]");
            matchData.forEach(oneMatchData ->{
                Match match = new Match();
                String matchStatus = oneMatchData.select("span[class=\"ds-text-tight-xs ds-font-bold ds-uppercase ds-leading-5\"]").text();
                match.setStatus(matchStatus.equalsIgnoreCase("RESULT") ?MatchStatus.COMPLETED:MatchStatus.LIVE);
                Elements matchPrev = oneMatchData.select("div[class=\"ds-text-tight-xs ds-truncate ds-text-typo-mid3\"]");
                String matchPreview  = matchPrev.get(0).child(0).text();//not change index  matchPrev.get(0).text() +
                match.setMatchNumberVenue(matchPreview);

                Elements team = oneMatchData.select("div[class=\"ds-flex ds-flex-col ds-mt-2 ds-mb-2\"]>*");
                Elements eachBatingTeam = team.get(0).children();
                String BattingTeamName = "";
                String BattingTeamScore = "";
                if(eachBatingTeam.size()>1){
                    BattingTeamName = eachBatingTeam.get(0).select("p").text();
                    BattingTeamScore = eachBatingTeam.get(1).select("strong").text() + eachBatingTeam.get(1).select("span").text();
                }
                else{
                    BattingTeamName = eachBatingTeam.get(0).select("p").text();
                }
                match.setBattingTeam(BattingTeamName);
                match.setBattingTeamScore(BattingTeamScore);
                Elements eachBowlingTeam = team.get(1).children();
                String BowlingTeamName = "";
                String BowlingTeamScore = "";
                if(eachBowlingTeam.size()>1){
                    BowlingTeamName = eachBowlingTeam.get(0).select("p").text();
                    BowlingTeamScore = eachBowlingTeam.get(1).select("strong").text() + eachBowlingTeam.get(1).select("span").text();
                }
                else{
                    BowlingTeamName = eachBowlingTeam.get(0).select("p").text();
                }
                match.setBowlingTeam(BowlingTeamName);
                match.setBowlingTeamScore(BowlingTeamScore);
                String desc = oneMatchData.select("p[class=\"ds-text-tight-s ds-font-medium ds-truncate ds-text-typo\"]>span").text();
                if(match.getStatus()==MatchStatus.LIVE)
                    match.setLiveText(desc);
                else {
                    matchRepo.save(match);
                    match.setTextComplete(desc);
                }
                match.setTeamHeading(BattingTeamName.toUpperCase()+" VS "+BowlingTeamName.toUpperCase());
                matches.add(match);
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return matches;
    }
    private void updateMatch(Match match1) {

        Match match = this.matchRepo.findByTeamHeading(match1.getTeamHeading()).orElse(null);
        if (match == null) {
            this.matchRepo.save(match1);
        } else {

            match1.setMatchId(match.getMatchId());
            this.matchRepo.save(match1);
        }

    }
    @Override
    public List<List<String>> getPointTable() {
        List<List<String>> pointTable = new ArrayList<>();
        String tableURL = "https://www.cricbuzz.com/cricket-series/7607/indian-premier-league-2024/points-table";
        try {
            Document document = Jsoup.connect(tableURL).get();
            Elements table = document.select("table.cb-srs-pnts");
            Elements bodyTrs = table.select("tbody>*");
            bodyTrs.forEach(tr -> {
                List<String> points = new ArrayList<>();
                if (tr.hasAttr("class")) {
                    Elements tds = tr.select("td");
                    String team = tds.get(0).select("div.cb-col-84").text();
                    points.add(team);
                    tds.forEach(td -> {
                        if (!td.hasClass("cb-srs-pnts-name")) {
                            points.add(td.text());
                        }
                    });
                    pointTable.add(points);
                }
            });

            System.out.println(pointTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pointTable;
    }

    @Override
    public String clearDataBaseRows() {
        try {
            this.matchRepo.deleteAll();
            return "true";
        }
        catch (Exception e){
            return "false";
        }
    }
}
