package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.vo.TaskDetailVo;

/**
 * 任务追踪明细响应体
 * 开发者：jimmyzhang
 * 日期：16/6/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TaskDetailResponse extends BaseResponse {

    private TaskDetailVo data;

    public TaskDetailVo getData() {
        return data;
    }

    public void setData(TaskDetailVo data) {
        this.data = data;
    }
}
