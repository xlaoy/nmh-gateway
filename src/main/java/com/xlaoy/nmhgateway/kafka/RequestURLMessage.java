package com.xlaoy.nmhgateway.kafka;

import com.xlaoy.common.utils.JSONUtil;
import lombok.Data;

/**
 * Created by Administrator on 2018/12/18 0018.
 */
@Data
public class RequestURLMessage {

    private String guid;

    private String url;

    private String time;

    public RequestURLMessage(String url) {
        this.url = url;
    }

}
