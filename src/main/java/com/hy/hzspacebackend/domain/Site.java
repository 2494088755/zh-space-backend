package com.hy.hzspacebackend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName table_site
 */
@TableName(value ="table_site")
@Data
public class Site implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String url;

    /**
     *
     */
    private String content;

    private String category;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
