package com.cylt.pojo.sys;
import com.cylt.common.base.pojo.BasePojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 通知
 */
@Getter
@Setter
public class SysPush extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 推送id
     */
    private String id;

    /**
     * 编号
     */
    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 回调url
     */
    private String callbackUrl;

    /**
     * 推送类型：0=推送即可;1=响应即可
     */
    private int pushType;

    /**
     * 推送图标
     */
    private String icon;

    /**
     * 是否已读
     */
    private boolean read;

    /**
     * 推送时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pushDate;
}
