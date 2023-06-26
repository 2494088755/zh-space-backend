package com.hy.hzspacebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hy.hzspacebackend.domain.Site;
import com.hy.hzspacebackend.service.SiteService;
import com.hy.hzspacebackend.mapper.SiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
* @author 黄勇
* @description 针对表【table_site】的数据库操作Service实现
* @createDate 2023-06-06 11:32:23
*/
@Service
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site>
    implements SiteService{

    @Autowired
    private SiteMapper siteMapper;

    @Override
    public ResponseEntity<?> getSite() {
        return ResponseEntity.ok(list());
    }

    @Override
    public ResponseEntity<?> insertSite(Site site) {
        siteMapper.insert(site);
        return ResponseEntity.ok("添加成功");
    }
}




