package com.kingston.jforgame.orm;

import javax.persistence.Column;
import java.lang.reflect.Method;
import java.util.*;

public class OrmBridge {
    /** 对应的数据库表名称 */
    private String tableName;
    /** 缓存所有表字段及其对应的getter method */
    private Map<String, Method> getterMap = new HashMap<>();
    /** 缓存所有表字段及其对应的setter method */
    private Map<String, Method> setterMap = new HashMap<>();
    /** 被覆写的property与表column的映射 */
    private Map<String, String> propertyToColumnOverride = new HashMap<>();
    /** 被覆写的表column与property的映射 */
    private Map<String, String> columnToPropertyOverride = new HashMap<>();

    /**表column与Column注解的映射*/
    private Map<String, Column> columnAnnotation =new HashMap<>();
    /** 实体所有的主键字段 */
    private Set<String> uniqueProperties = new HashSet<>();
    /** 需要持久化的字段 */
    private Set<String> properties = new LinkedHashSet<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, Method> getGetterMap() {
        return getterMap;
    }

    public void addGetterMethod(String field, Method method) {
        this.getterMap.put(field, method);
    }

    public Map<String, Method> getSetterMap() {
        return setterMap;
    }

    public void addSetterMethod(String field, Method method) {
        this.setterMap.put(field, method);
    }

    /**
     * 返回查询实体的id组合
     * @return
     */
    public List<String> getQueryProperties() {
        return new ArrayList<>(this.uniqueProperties);
    }

    public Method getGetterMethod(String field) {
        return this.getterMap.get(field);
    }

    public void addUniqueKey(String id) {
        this.uniqueProperties.add(id);
    }

    public void addProperty(String property) {
        this.properties.add(property);
    }

    public void addPropertyColumnOverride(String property, String column) {
        this.propertyToColumnOverride.put(property, column);
        this.columnToPropertyOverride.put(column, property);
    }

    public String getOverrideProperty(String property) {
        return this.propertyToColumnOverride.get(property);
    }

    public Map<String, String> getPropertyToColumnOverride() {
        return propertyToColumnOverride;
    }

    public Map<String, String> getColumnToPropertyOverride() {
        return columnToPropertyOverride;
    }

    public void setColumnToPropertyOverride(Map<String, String> columnToPropertyOverride) {
        this.columnToPropertyOverride = columnToPropertyOverride;
    }

    public void setPropertyToColumnOverride(Map<String, String> propertyToColumnOverride) {
        this.propertyToColumnOverride = propertyToColumnOverride;
    }

    public List<String> listProperties() {
        return new ArrayList<>(this.properties);
    }

    public void addColumnAnnotation(String column, Column columnAnnotation){
        this.columnAnnotation.put(column,columnAnnotation);
    }

    public Column getColumnAnnotation(String column){
        return columnAnnotation.get(column);
    }
}
