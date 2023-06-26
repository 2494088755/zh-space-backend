package com.hy.hzspacebackend.controller;

import com.hy.hzspacebackend.domain.Site;
import com.hy.hzspacebackend.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author 黄勇
 * @since 2023/6/6
 */
@RestController
@CrossOrigin
public class SiteController {

    @Autowired
    private SiteService siteService;

    @GetMapping("/site")
    public ResponseEntity<?> getSite() {
        return siteService.getSite();
    }

    @PostMapping("/site")
    public ResponseEntity<?> insertSite(@RequestBody Site site) {
        return siteService.insertSite(site);
    }
}
