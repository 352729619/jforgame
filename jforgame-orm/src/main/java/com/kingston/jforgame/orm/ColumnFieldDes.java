package com.kingston.jforgame.orm;

import javax.persistence.Column;

/**
 * @author: JieYu.W    Q:352729619
 * Created on 2020/5/4
 * Describe : 数据库表字段的描述
 */

public class ColumnFieldDes {

    public static String getDes(String property, OrmBridge bridge) {
        Class<?> aClass = bridge.getGetterMethod(property).getReturnType();
        //待完善数据类型,
        String type = "varchar";
        if (int.class.isAssignableFrom(aClass) || Integer.class.isAssignableFrom(aClass)) {
            type = "int";
        } else if (long.class.isAssignableFrom(aClass) || Long.class.isAssignableFrom(aClass)) {
            type = "bigint";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(type);
        sb.append("(");
        sb.append(getLength(property, bridge));
        sb.append(")");
        return sb.toString();
    }

    private static int getLength(String property, OrmBridge bridge) {
        Column annotation = bridge.getColumnAnnotation(property);
        return annotation.length();
    }
}
