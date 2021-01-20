package com.cylt.pojo.sys;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 通知
 */
@Getter
@Setter
public class SysPush implements Cloneable, Serializable {

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
     * 回调data
     */
    private String callbackData;

    /**
     * 推送状态：0=已推送;1=未推送
     */
    private int pushState;

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


    @Override
    public SysPush clone() {
        SysPush stu = null;
        try{
            stu = (SysPush) super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }

    public SysPush() {
    }

    public SysPush(SysNotice notice) {
        setId(UUID.randomUUID().toString());
        setCallbackUrl(notice.getCallbackUrl());
        setCode(notice.getCode());
        setContent(notice.getContent());
        setTitle(notice.getTitle());
        setPushDate(new Date());
        setIcon(notice.getIcon());
        setCallbackData(notice.getJsonData());
        setRead(false);
        setPushState(0);
    }
}
