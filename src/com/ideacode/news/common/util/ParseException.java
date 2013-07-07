package com.ideacode.news.common.util;

/**
 * 解析文档异常类
 * @author Vic su
 * @data 2011-10-24
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = -3824561947913147233L;
    private String mesg;
    private static final String ERR_TIP = "Parseing XML doc failure ! Caused by ";

    public ParseException(){}

    public ParseException(String mesg){
        this.mesg = mesg;
    }

    public ParseException(Exception e){
        this.mesg = e.getMessage();
    }

    @Override
    public String getMessage() {
        return ERR_TIP + mesg;
    }
    @Override
    public String toString(){
        return getMessage();
    }
}
