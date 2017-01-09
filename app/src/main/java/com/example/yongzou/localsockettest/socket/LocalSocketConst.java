package com.example.yongzou.localsockettest.socket;

/**
 * Created by yong.zou on 2016/12/9.
 */

public interface LocalSocketConst {
    byte TYPE_LOGIN = 0x01;
    byte TYPE_CONTENT_JSON = 0x02;
    byte TYPE_CONTENT_XML = 0x03;
    byte TYPE_CLOSE = 0x10;
}
