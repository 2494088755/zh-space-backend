package com.hy.hzspacebackend.service;

import com.hy.hzspacebackend.domain.Site;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

/**
* @author 黄勇
* @description 针对表【table_site】的数据库操作Service
* @createDate 2023-06-06 11:32:23
*/
public interface SiteService extends IService<Site> {

    ResponseEntity<?> getSite();

    ResponseEntity<?> insertSite(Site site);
}
