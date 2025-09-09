package com.easytiwu.starterdb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类，所有业务实体类的基类
 * 包含通用字段和对应的方法，提供基础的审计字段
 * 实现Serializable接口以支持序列化
 */
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 记录创建时间，自动填充
     * 使用LocalDateTime类型，适用于MySQL 8+等支持日期时间类型的数据库
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间，插入和更新时自动填充
     * 用于实现数据版本控制和并发控制
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识字段
     * 0 表示未删除
     * 1 表示已删除
     * 使用@TableLogic注解实现软删除功能
     */
    @TableLogic
    private Integer deleted;

    // Getter and Setter methods
    /**
     * 获取记录创建时间
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置记录创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取记录最后更新时间
     * @return 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置记录最后更新时间
     * @param updateTime 更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取逻辑删除标识
     * @return 删除标识（0:未删除 1:已删除）
     */
    public Integer getDeleted() {
        return deleted;
    }

    /**
     * 设置逻辑删除标识
     * @param deleted 删除标识（0:未删除 1:已删除）
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    /**
     * 返回对象的字符串表示，用于调试和日志
     * @return 包含核心字段的字符串
     */
    @Override
    public String toString() {
        return "BaseEntity{" +
                "createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleted=" + deleted +
                '}';
    }
}