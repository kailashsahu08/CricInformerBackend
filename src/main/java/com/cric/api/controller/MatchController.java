package com.cric.api.controller;

import com.cric.api.entity.Match;
import com.cric.api.service.IMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "http://localhost:5173/")
public class MatchController {
    @Autowired
    private IMatchService matchService;
    @GetMapping("/live")
    public ResponseEntity<List<Match>> getLiveMatches(){
        return new ResponseEntity<>(this.matchService.getLiveMatches(), HttpStatus.OK);
    }
    //get all matches
    @GetMapping
    public  ResponseEntity<List<Match>> getAllMessage(){
        return new   ResponseEntity<>(this.matchService.getAllMatches(),HttpStatus.OK);
    }
    //get pont table
    @GetMapping("/point-table")
    public  ResponseEntity<?> getPointTable(){
        return new   ResponseEntity<>(this.matchService.getPointTable(),HttpStatus.OK);
    }
    @GetMapping("/clear-db")
    public String clearDbTable(){
        return this.matchService.clearDataBaseRows();
    }

}
